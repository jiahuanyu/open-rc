package me.jiahuan.stf.server.connection

import me.jiahuan.stf.server.ScreenEncoder
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import java.nio.ByteBuffer

/**
 * WebSocket 服务端
 */
class WebSocketServerConnection : Connection, AppWebSocketServer.ServerEventsHandler {

    private val appWebSocketServer = AppWebSocketServer(this)

    private val screenEncoderMap = HashMap<WebSocket, ScreenEncoder>()

    private val connections = ArrayList<WebSocket>()

    init {
        appWebSocketServer.isReuseAddr = true
    }

    override fun start() {
        appWebSocketServer.start()
        ScreenEncoder(this).start()
    }

    override fun stop() {
        appWebSocketServer.stop()
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        println("onOpen")
        if (conn != null) {
            connections.add(conn)
        }
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        println("onClose code = $code, reason = $reason, remote = $remote")
        if (conn != null) {
            connections.remove(conn)
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

    override fun send(byteBuffer: ByteBuffer) {
        connections.forEach {
            it.send(byteBuffer)
        }
    }
}