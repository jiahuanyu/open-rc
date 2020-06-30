package com.xingren.stf.connection

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.xingren.android.common.gson.GsonFactory
import com.xingren.android.logger.Logger
import com.xingren.community.doctor.foundation.Constants
import com.xingren.community.doctor.foundation.info.AppManager
import okhttp3.*
import okio.ByteString
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier


/**
 * WebSocket服务
 */
class WebSocketService(private val clientEventsHandler: AppWebSocketClient.ClientEventsHandler) : WebSocketListener() {
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
    private val handler = object : Handler(Looper.getMainLooper()) {
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
        Logger.i(TAG, "onOpen webSocket = $webSocket, response = $response")
        this.webSocket = webSocket
        this.currentWebSocketConnectState = WebSocketConnectState.CONNECTED
        callback.onConnectionStateChanged(WebSocketConnectState.CONNECTED)
        checkPongSuccess = false
        postSendHeartBeat(true)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        if (this.webSocket == webSocket) {
            Logger.i(TAG, "onMessage webSocket = $webSocket, text = $text")
            var webSocketRXMessage: WebSocketRXMessage? = null
            try {
                webSocketRXMessage = GsonFactory.gson.fromJson<WebSocketRXMessage>(text, WebSocketRXMessage::class.java)
            } catch (e: Exception) {
                Logger.w(TAG, e)
            }
            if (webSocketRXMessage != null && webSocketRXMessage.name == "pong") {
                checkPongSuccess = true
                return
            }
            callback.onMessage(text)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Logger.i(TAG, "onMessage webSocket = $webSocket, bytes = $bytes")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        if (this.webSocket == webSocket) {
            Logger.i(TAG, "onClosing webSocket = $webSocket, code = $code, reason = $reason")
            this.currentWebSocketConnectState = WebSocketConnectState.DISCONNECTING
            callback.onConnectionStateChanged(WebSocketConnectState.DISCONNECTING)
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        if (this.webSocket == webSocket) {
            Logger.i(TAG, "onClosed webSocket = $webSocket, code = $code, reason = $reason")
            this.currentWebSocketConnectState = WebSocketConnectState.DISCONNECTED
            callback.onConnectionStateChanged(WebSocketConnectState.DISCONNECTED)
            this.webSocket = null
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        if (this.webSocket == webSocket) {
            Logger.i(TAG, "onFailure webSocket = $webSocket, t = $t, response = $response")
            this.currentWebSocketConnectState = WebSocketConnectState.DISCONNECTED
            callback.onConnectionStateChanged(WebSocketConnectState.DISCONNECTED)
            this.webSocket = null
        }
    }

    /**
     * 连接
     */
    fun connect() {
        // 判断当前Socket连接状态，是否需要连接
        if (currentWebSocketConnectState == WebSocketConnectState.DISCONNECTED) {
            Logger.i(TAG, "connect()")
            cleanHandlerMessages()
            this.currentWebSocketConnectState = WebSocketConnectState.CONNECTING
            callback.onConnectionStateChanged(WebSocketConnectState.CONNECTING)
            getOkHttpClient().newWebSocket(obtainRequest(), this)
            postCheckConnection()
        } else {
            Logger.i(TAG, "connect(), 网络咨询Socket处于 $currentWebSocketConnectState")
        }
    }


    /**
     * 断开链接
     */
    fun disconnect() {
        Logger.i(TAG, "disconnect(), 网络咨询请求断开链接")
        try {
            // 这里调用 close 不会回调 closed 方法
            webSocket?.close(1000, "I'm done")
        } catch (e: Exception) {
            Logger.w(TAG, e)
        }
        if (this.currentWebSocketConnectState != WebSocketConnectState.DISCONNECTED) {
            this.currentWebSocketConnectState = WebSocketConnectState.DISCONNECTED
            callback.onConnectionStateChanged(WebSocketConnectState.DISCONNECTED)
        }
        this.webSocket = null
        cleanHandlerMessages()
    }

    /**
     * 重新链接
     */
    fun reconnect() {
        Logger.i(TAG, "reconnect(), 网络咨询请求重新连接")
        disconnect()
        handler.postDelayed({
            connect()
        }, 1000)
    }

    /**
     * 发送数据
     */
    fun send(text: String): Boolean {
        Logger.i(TAG, "send(), 网络咨询发送 text = $text")
        return webSocket?.send(text) == true
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
            .url(String.format(Locale.CHINA, "wss://api.%s/xiaoxing/ws/xiaoxing/app/%d?yzsChannel=%d", AppManager.instance.applicationContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getString(Constants.SHARED_PREFERENCES_KEY_HOST, Constants.HOST_PRODUCT) ?: Constants.HOST_PRODUCT, AppManager.instance.getLoginId(), AppManager.instance.getYZSChannel()))
            .addHeader("XRLoginId", AppManager.instance.getLoginId().toString())
            .addHeader("XRAppVer", AppManager.instance.applicationInfo.versionName)
            .addHeader("XROs", AppManager.instance.deviceInfo.os)
            .addHeader("XRDid", AppManager.instance.deviceInfo.deviceId)
            .addHeader("XRToken", AppManager.instance.getLoginToken())
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