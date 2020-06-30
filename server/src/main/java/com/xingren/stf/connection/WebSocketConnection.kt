package com.xingren.stf.connection

import com.xingren.stf.ScreenEncoder
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import java.nio.ByteBuffer

/**
 * WebSocket 服务端
 */
class WebSocketConnection : Connection, AppWebSocketServer.EventsHandler {

    private val webSocketServer = AppWebSocketServer(this)

    private val screenEncoderMap = HashMap<WebSocket, ScreenEncoder>()

    init {
        webSocketServer.isReuseAddr = true
    }

    fun start() {
        webSocketServer.run()
    }

    fun stop() {
        webSocketServer.stop()
        screenEncoderMap.keys.forEach {
            screenEncoderMap[it]?.stop()
        }
        screenEncoderMap.clear()
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        println("onOpen")
        if (conn != null) {
            val screenEncoder = ScreenEncoder(this, conn)
            screenEncoderMap[conn] = screenEncoder
            screenEncoder.start()
        }
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        println("onClose code = $code, reason = $reason, remote = $remote")
        if (conn != null) {
            screenEncoderMap.remove(conn)?.stop()
        }
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        println("onMessage message = $message")
    }

    override fun onMessage(conn: WebSocket?, message: ByteBuffer?) {
        println("onMessage message = $message")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        println("onError = ${ex?.message}")
    }

    override fun onStart() {
        println("onStart")
    }

    override fun send(connKey: Any, byteBuffer: ByteBuffer) {
        connKey as WebSocket
        connKey.send(byteBuffer)
    }

    override fun hasConnection(): Boolean {
        return screenEncoderMap.isNotEmpty()
    }
}