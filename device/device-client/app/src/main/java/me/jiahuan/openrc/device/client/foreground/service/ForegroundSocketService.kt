package me.jiahuan.openrc.device.foreground.service

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import kotlinx.coroutines.*
import me.jiahuan.openrc.device.AppConstants
import me.jiahuan.openrc.device.foreground.event.AppEventCenter
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket

class ForegroundSocketService {

    companion object {
        private const val TAG = "ForegroundSocketService"
        private const val MSG_WHAT_CHECK_CONNECT = 1
        private const val TIME_GAP_CHECK_CONNECTION = 5000L
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
        Log.d(TAG, "connect()")
        currentSocketConnectState = SocketConnectState.CONNECTED
        CoroutineScope(Dispatchers.IO).launch {
            try {
                socket = Socket(InetAddress.getLoopbackAddress(), AppConstants.SOCKET_PORT)
                socket?.let { socket ->
                    val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    val bufferedWriter = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                    bufferedWriter.write(AppConstants.CODE_AUTH.toString())
                    bufferedWriter.newLine()
                    bufferedWriter.flush()
                    val readLine = bufferedReader.readLine()
                    AppEventCenter.backgroundServiceAliveLiveData.postValue(AppConstants.CODE_SUCCESS.toString() == readLine)
                    while (isActive) {
                        bufferedReader.readLine() ?: break
                    }
                    try {
                        bufferedReader.close()
                    } catch (e: Exception) {
                    }
                    try {
                        bufferedWriter.close()
                    } catch (e: Exception) {
                    }
                    try {
                        socket.close()
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
            }
            withContext(Dispatchers.Main) {
                AppEventCenter.backgroundServiceAliveLiveData.postValue(false)
                currentSocketConnectState = SocketConnectState.DISCONNECTED
                cleanHandlerMessages()
                postCheckConnection()
            }
        }
    }

    /**
     * 断开链接
     */
    fun disconnect() {
        Log.d(TAG, "disconnect()")
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
        Log.d(TAG, "reconnect()")
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