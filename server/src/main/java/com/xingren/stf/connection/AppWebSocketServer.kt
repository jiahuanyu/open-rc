package com.xingren.stf.connection

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer


class AppWebSocketServer(private val serverEventsHandler: ServerEventsHandler) : WebSocketServer(InetSocketAddress(DEFAULT_PORT_NUMBER)) {
    companion object {
        private const val DEFAULT_PORT_NUMBER = 8887
    }

    interface ServerEventsHandler {
        fun onOpen(conn: WebSocket?, handshake: ClientHandshake?)
        fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean)
        fun onMessage(conn: WebSocket?, message: String?)
        fun onMessage(conn: WebSocket?, message: ByteBuffer?)
        fun onError(conn: WebSocket?, ex: Exception?)
        fun onStart()
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        serverEventsHandler.onOpen(conn, handshake)
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        serverEventsHandler.onClose(conn, code, reason, remote)
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        serverEventsHandler.onMessage(conn, message)
    }

    override fun onMessage(conn: WebSocket?, message: ByteBuffer?) {
        serverEventsHandler.onMessage(conn, message)
    }

    override fun onStart() {
        serverEventsHandler.onStart()
    }

    override fun onError(conn: WebSocket?, ex: java.lang.Exception?) {
        serverEventsHandler.onError(conn, ex)
    }
}

