package me.jiahuan.stf.device.foreground.service

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import me.jiahuan.stf.device.AppConstants
import java.net.InetAddress
import java.net.Socket

class ForeBackSocketService {

    companion object {
        private const val MSG_WHAT_CHECK_CONNECT = 1
        private const val TIME_GAP_CHECK_CONNECTION = 500L
    }

    private var socket: Socket? = null

    enum class SocketConnectState {
        // 链接断开
        DISCONNECTED,

        CONNECTED,
    }

    // 当前Socket链接状态
    private var currentSocketConnectState = SocketConnectState.DISCONNECTED

    // Handler
    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_WHAT_CHECK_CONNECT -> {
                    if (currentSocketConnectState != SocketConnectState.CONNECTED) {
                        reconnect()
                    } else {
                        postCheckConnection()
                    }
                }
            }
        }
    }

    /**
     * 链接
     */
    fun connect() {
        socket = Socket(InetAddress.getLoopbackAddress(), AppConstants.SOCKET_PORT)
        cleanHandlerMessages()
    }

    /**
     * 断开链接
     */
    fun disconnect() {
        try {
            socket?.close()
        } catch (e: Exception) {
            println(e.message)
        }
        if (this.currentSocketConnectState != SocketConnectState.DISCONNECTED) {
            this.currentSocketConnectState = SocketConnectState.DISCONNECTED
        }
        this.socket = null
        cleanHandlerMessages()
    }

    fun reconnect() {
        disconnect()
        handler.postDelayed({
            connect()
        }, 1000)
    }

    private fun cleanHandlerMessages() {
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * 通知handler检查当前链接
     */
    private fun postCheckConnection() {
        handler.removeMessages(MSG_WHAT_CHECK_CONNECT)
        handler.sendEmptyMessageDelayed(MSG_WHAT_CHECK_CONNECT, TIME_GAP_CHECK_CONNECTION)
    }
}