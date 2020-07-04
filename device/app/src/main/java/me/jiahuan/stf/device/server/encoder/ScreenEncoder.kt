package me.jiahuan.stf.device.server.encoder

import android.graphics.Rect
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.IBinder
import android.view.Surface
import kotlinx.coroutines.*
import me.jiahuan.stf.device.server.connection.Connection
import me.jiahuan.stf.device.server.device.Device
import me.jiahuan.stf.device.server.device.SurfaceControl
import java.nio.ByteBuffer


class ScreenEncoder(private val connection: Connection, private val device: Device) {

    private var mediaCodecJob: Job? = null

    private var sps: ByteArray? = null
    private var pps: ByteArray? = null

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
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15)
        // 颜色
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        // 关键帧 1秒
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
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
        val displayInfo = device.displayInfo
        println("displayInfo = $displayInfo")

        val screenInfo = device.screenInfo
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
                    if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        // 获取 SPS 和 PPS 数据
                        println("INFO_OUTPUT_FORMAT_CHANGED")
                        val outputFormat: MediaFormat = mediaCodec.outputFormat
                        // 获取编码SPS和PPS信息
                        val spsByteBuffer = outputFormat.getByteBuffer("csd-0")
                        if (spsByteBuffer != null) {
                            sps = ByteArray(spsByteBuffer.remaining())
                            spsByteBuffer.get(sps!!, 0, sps!!.size)
                        }

                        val ppsByteBuffer = outputFormat.getByteBuffer("csd-1")
                        if (ppsByteBuffer != null) {
                            pps = ByteArray(ppsByteBuffer.remaining())
                            ppsByteBuffer.get(pps!!, 0, pps!!.size)
                        }
                    } else if (outputBufferIndex >= 0) {
                        val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
                        if (outputBuffer != null) {
                            if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                                sps?.apply {
                                    connection.send(ByteBuffer.wrap(this))
                                }
                                pps?.apply {
                                    connection.send(ByteBuffer.wrap(this))
                                }
                            }
                            connection.send(outputBuffer)
                        }
                        mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                    }
                    eof = bufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM
                }
            } catch (e: Exception) {
                println("ScreenEncoder error = " + e.message)
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