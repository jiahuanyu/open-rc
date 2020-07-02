package me.jiahuan.stf.server.connection

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier


/**
 * WebSocket服务
 */
class AppWebSocketClient(private val clientEventsHandler: ClientEventsHandler) : WebSocketListener() {
    companion object {
        private const val TAG = "WebSocketService"

        private const val TIME_GAP_SEND_HEART_BEAT = 30 * 1000L

        private const val TIME_GAP_CHECK_PONG_PACKET = 10 * 1000L

        private const val TIME_GAP_CHECK_CONNECTION = 10 * 1000L

        private const val MSG_WHAT_SEND_HEART_BEAT = 1

        private const val MSG_WHAT_CHECK_PONG = 2

        private const val MSG_WHAT_CHECK_CONNECT = 3
    }

    enum class WebSocketConnectState {
        // 链接断开
        DISCONNECTED,

        // 正在断开链接
        DISCONNECTING,

        // 正在连接
        CONNECTING,

        // 已连接
        CONNECTED,
    }

    // 当前Socket链接状态
    private var currentWebSocketConnectState = WebSocketConnectState.DISCONNECTED

    // 当前WebSocket链接
    private var webSocket: WebSocket? = null

    // 检查是否有pong包
    private var checkPongSuccess = false

    // Handler
    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                // 发送心跳
                MSG_WHAT_SEND_HEART_BEAT -> {
                    if (sendHeaderBeatPacket()) {
                        postCheckPong()
                        postSendHeartBeat(false)
                    } else {
                        reconnect()
                    }
                }
                MSG_WHAT_CHECK_PONG -> {
                    if (!checkPongSuccess) {
                        reconnect()
                    }
                }
                MSG_WHAT_CHECK_CONNECT -> {
                    if (currentWebSocketConnectState != WebSocketConnectState.CONNECTED) {
                        reconnect()
                    } else {
                        postCheckConnection()
                    }
                }
            }
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("onOpen webSocket = $webSocket, response = $response")
        this.webSocket = webSocket
        this.currentWebSocketConnectState = WebSocketConnectState.CONNECTED
        clientEventsHandler.onOpen(webSocket, response)
        checkPongSuccess = false
//        postSendHeartBeat(true)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        if (this.webSocket == webSocket) {
            println("onMessage webSocket = $webSocket, text = $text")
            clientEventsHandler.onMessage(webSocket, text)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        println("onMessage webSocket = $webSocket, bytes = $bytes")
        clientEventsHandler.onMessage(webSocket, bytes)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        if (this.webSocket == webSocket) {
            println("onClosing webSocket = $webSocket, code = $code, reason = $reason")
            this.currentWebSocketConnectState = WebSocketConnectState.DISCONNECTING
            clientEventsHandler.onClosing(webSocket, code, reason)
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        if (this.webSocket == webSocket) {
            println("onClosed webSocket = $webSocket, code = $code, reason = $reason")
            this.currentWebSocketConnectState = WebSocketConnectState.DISCONNECTED
            clientEventsHandler.onClosed(webSocket, code, reason)
            this.webSocket = null
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        if (this.webSocket == webSocket) {
            println("onFailure webSocket = $webSocket, t = $t, response = $response")
            this.currentWebSocketConnectState = WebSocketConnectState.DISCONNECTED
            clientEventsHandler.onFailure(webSocket, t, response)
            this.webSocket = null
        }
    }

    /**
     * 连接
     */
    fun connect() {
        // 判断当前Socket连接状态，是否需要连接
        if (currentWebSocketConnectState == WebSocketConnectState.DISCONNECTED) {
            println("connect()")
            cleanHandlerMessages()
            this.currentWebSocketConnectState = WebSocketConnectState.CONNECTING
            getOkHttpClient().newWebSocket(obtainRequest(), this)
            postCheckConnection()
        } else {
            println("connect(), Socket处于 $currentWebSocketConnectState")
        }
    }


    /**
     * 断开链接
     */
    fun disconnect() {
        println("disconnect(), 请求断开链接")
        try {
            // 这里调用 close 不会回调 closed 方法
            webSocket?.close(1000, "I'm done")
        } catch (e: Exception) {
            println(e.message)
        }
        if (this.currentWebSocketConnectState != WebSocketConnectState.DISCONNECTED) {
            this.currentWebSocketConnectState = WebSocketConnectState.DISCONNECTED
        }
        this.webSocket = null
        cleanHandlerMessages()
    }

    /**
     * 重新链接
     */
    fun reconnect() {
        println("reconnect(), 请求重新连接")
        disconnect()
        handler.postDelayed({
            connect()
        }, 1000)
    }

    /**
     * 发送数据
     */
    fun send(text: String): Boolean {
        println("send(), 发送 text = $text")
        return webSocket?.send(text) == true
    }

    fun send(byteBuffer: ByteBuffer): Boolean {
        return webSocket?.send(byteBuffer.toByteString()) == true
    }

    /**
     * 清理Handler数据
     */
    private fun cleanHandlerMessages() {
        handler.removeCallbacksAndMessages(null)
        checkPongSuccess = false
    }

    /**
     * 发送心跳包
     */
    private fun sendHeaderBeatPacket(): Boolean {
        checkPongSuccess = false
        return send(String.format("{\"guid\": \"%s\",\"name\": \"ping\"}", UUID.randomUUID()))
    }

    /**
     * 通知Handler发送心跳包
     */
    private fun postSendHeartBeat(immediately: Boolean) {
        if (immediately) {
            handler.removeMessages(MSG_WHAT_SEND_HEART_BEAT)
            handler.sendEmptyMessage(MSG_WHAT_SEND_HEART_BEAT)
        } else {
            if (!handler.hasMessages(MSG_WHAT_SEND_HEART_BEAT)) {
                handler.sendEmptyMessageDelayed(MSG_WHAT_SEND_HEART_BEAT, TIME_GAP_SEND_HEART_BEAT)
            }
        }
    }

    /**
     * 通知handler检查pong包
     */
    private fun postCheckPong() {
        handler.removeMessages(MSG_WHAT_CHECK_PONG)
        handler.sendEmptyMessageDelayed(MSG_WHAT_CHECK_PONG, TIME_GAP_CHECK_PONG_PACKET)
    }

    /**
     * 通知handler检查当前链接
     */
    private fun postCheckConnection() {
        handler.removeMessages(MSG_WHAT_CHECK_CONNECT)
        handler.sendEmptyMessageDelayed(MSG_WHAT_CHECK_CONNECT, TIME_GAP_CHECK_CONNECTION)
    }

    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
        return builder.build()
    }

    private fun obtainRequest(): Request {
        return Request.Builder()
            .url("ws://172.16.10.111:8888/ws/device")
            .build()
    }

    interface ClientEventsHandler {
        fun onOpen(webSocket: WebSocket, response: Response)
        fun onMessage(webSocket: WebSocket, bytes: ByteString)
        fun onMessage(webSocket: WebSocket, text: String)
        fun onClosing(webSocket: WebSocket, code: Int, reason: String)
        fun onClosed(webSocket: WebSocket, code: Int, reason: String)
        fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?)
    }
}