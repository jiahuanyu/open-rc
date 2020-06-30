package com.xingren.stf

import android.graphics.Rect
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.IBinder
import android.view.Surface
import com.xingren.stf.connection.Connection
import kotlinx.coroutines.*

class ScreenEncoder(private val connection: Connection, private val connKey: Any) {

    private var mediaCodecJob: Job? = null

    private fun createDisplay(): IBinder {
        return SurfaceControl.createDisplay("scrcpy", true)
    }

    private fun setDisplaySurface(display: IBinder, surface: Surface, orientation: Int, deviceRect: Rect, displayRect: Rect, layerStack: Int) {
        SurfaceControl.openTransaction()
        try {
            SurfaceControl.setDisplaySurface(display, surface)
            SurfaceControl.setDisplayProjection(display, orientation, deviceRect, displayRect)
            SurfaceControl.setDisplayLayerStack(display, layerStack)
        } finally {
            SurfaceControl.closeTransaction()
        }
    }

    private fun configMediaFormat(videoRect: Rect): MediaFormat {
        // 创建视频编码所需
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, videoRect.width(), videoRect.height())
        mediaFormat.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_VIDEO_AVC)
        // 比特率，RGBA 4字节
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, videoRect.width() * videoRect.height() * 4)
        // 帧率
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        // 颜色
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        // 关键帧 1秒
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10)
        mediaFormat.setInteger("level", MediaCodecInfo.CodecProfileLevel.AVCLevel3)

        mediaFormat.setLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 100_000)

        mediaFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline)
        return mediaFormat
    }

    private fun configMediaCodeC(videoRect: Rect): MediaCodec {
        val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec.configure(configMediaFormat(videoRect), null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        return mediaCodec
    }

    fun start() {
        val displayInfo = ServiceManager().displayManager.getDisplayInfo(0)
        println("displayInfo = $displayInfo")

        val screenInfo = ScreenInfo.computeScreenInfo(displayInfo, 720, -1)
        println("screenInfo = $screenInfo")

        val mediaCodec = configMediaCodeC(screenInfo.videoSize.toRect())
        val display = createDisplay()
        setDisplaySurface(display, mediaCodec.createInputSurface(), screenInfo.videoRotation, screenInfo.contentRect, screenInfo.unlockedVideoSize.toRect(), displayInfo.layerStack)

        val bufferInfo = MediaCodec.BufferInfo()
        mediaCodec.start()

        mediaCodecJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                var eof = false
                while (!eof && isActive) {
                    val outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, -1)
                    eof = bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                    if (outputBufferIndex >= 0) {
                        try {
                            val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
                            if (outputBuffer != null) {
                                connection.send(connKey, outputBuffer)
                            }
                        } finally {
                            mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                        }
                    }
                }
            } finally {
                mediaCodec.stop()
                mediaCodec.release()
                SurfaceControl.destroyDisplay(display)
            }
        }
    }

    fun stop() {
        runBlocking {
            mediaCodecJob?.cancelAndJoin()
        }
    }
}