package com.xingren.stf

import android.graphics.Rect
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.IBinder
import android.view.Surface
import com.xingren.stf.connection.Connection
import java.io.File


/**
 * 屏幕录制
 */
class TestScreenRecorder() {

    companion object {
        private const val WAIT_TIME_US = 50000L
    }

    private lateinit var mediaCodec: MediaCodec

    private lateinit var mediaMuxer: MediaMuxer

    private var isMediaEncodeToStop = false

    private var videoTrackIndex = -1

    private var isMediaMuxerStated = false

    private var videoEncoderThread: VideoEncoderThread? = null

    private lateinit var display: IBinder

    init {
        initializeMediaCodec()
        val screenFile = File("/sdcard/screen.mp4")
        screenFile.deleteOnExit()
        initMediaMuxer(screenFile)
    }

    private fun initializeMediaCodec() {
        val serviceManager = ServiceManager()

        val displayInfo = serviceManager.displayManager.getDisplayInfo(0)
        val layerStack = displayInfo.layerStack
        println("displayInfo = $displayInfo")
        //
        val screenInfo = ScreenInfo.computeScreenInfo(displayInfo, -1, -1)
        println("screenInfo = $screenInfo")
        val contentRect = screenInfo.contentRect
        val videoRect = screenInfo.videoSize.toRect()
        val unlockedVideoRect = screenInfo.unlockedVideoSize.toRect()
        val videoRotation = screenInfo.videoRotation

        // 创建视频编码所需
        val videoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, videoRect.width(), videoRect.height())
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        // 比特率，RGBA 4字节
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, videoRect.width() * videoRect.height() * 4)
        // 帧率
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        // 关键帧 1秒
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        val surface = mediaCodec.createInputSurface()
        display = createDisplay()
        setDisplaySurface(display, surface, videoRotation, contentRect, unlockedVideoRect, layerStack)

        println("initialize success")
    }

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

    /**
     * 初始化MediaMuxer
     */
    private fun initMediaMuxer(file: File) {
        mediaMuxer = MediaMuxer(file.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    /**
     * 视频（不包括音频）编码线程
     */
    inner class VideoEncoderThread : Thread() {
        private var presentationTimeUs = 0L
        private var realExit = false

        override fun run() {
            try {
                val bufferInfo = MediaCodec.BufferInfo()
                mediaCodec.start()

                while (true) {
                    if (realExit) {
                        mediaCodec.stop()
                        mediaCodec.release()
                        mediaMuxer.release()
                        SurfaceControl.destroyDisplay(display)
                        break
                    }

                    if (isMediaEncodeToStop && !hasSignalEndOfStream) {
                        mediaCodec.signalEndOfInputStream()
                        hasSignalEndOfStream = true
                    }

                    var outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, WAIT_TIME_US)
                    if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        videoTrackIndex = mediaMuxer.addTrack(mediaCodec.outputFormat)
                        mediaMuxer.start()
                        isMediaMuxerStated = true
                    } else {
                        if (isMediaMuxerStated) {
                            if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0 && isMediaEncodeToStop) {
                                println("视频结束")
                                realExit = true
                                continue
                            }

                            while (outputBufferIndex >= 0) {
                                println("outputBufferIndex = $outputBufferIndex")
                                val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)
                                if (outputBuffer != null) {
                                    outputBuffer.position(bufferInfo.offset)
                                    outputBuffer.limit(bufferInfo.offset + bufferInfo.size)
                                    if (presentationTimeUs == 0L) {
                                        presentationTimeUs = bufferInfo.presentationTimeUs
                                    }
                                    bufferInfo.presentationTimeUs = bufferInfo.presentationTimeUs - presentationTimeUs
                                    if (bufferInfo.presentationTimeUs >= 0) {
                                        println("time = ${bufferInfo.presentationTimeUs / 1000000}")
                                        mediaMuxer.writeSampleData(videoTrackIndex, outputBuffer, bufferInfo)
                                        mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                                    }
                                }
                                if (isMediaEncodeToStop && !hasSignalEndOfStream) {
                                    mediaCodec.signalEndOfInputStream()
                                    hasSignalEndOfStream = true
                                }
                                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, WAIT_TIME_US)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    private var hasSignalEndOfStream = false

    fun start() {
        videoEncoderThread = VideoEncoderThread()
        videoEncoderThread?.start()
    }

    fun stop() {
        isMediaEncodeToStop = true
        videoEncoderThread?.join()
    }
}