package com.xingren.stf.connection

import com.xingren.stf.ScreenEncoder
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import java.nio.ByteBuffer

/**
 * WebSocket 服务端
 */
class WebSocketClientConnection : Connection, AppWebSocketClient.ClientEventsHandler {

    private val appWebSocketClient = AppWebSocketClient(this)

    private val screenEncoder = ScreenEncoder(this, Any())

    override fun start() {
        appWebSocketClient.connect()
    }

    override fun stop() {
        appWebSocketClient.disconnect()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("onOpen")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
    }

    override fun send(connKey: Any, byteBuffer: ByteBuffer) {
        appWebSocketClient.send(byteBuffer)
    }
}