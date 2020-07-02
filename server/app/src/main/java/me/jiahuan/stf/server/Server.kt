package me.jiahuan.stf.server

import android.os.Looper
import me.jiahuan.stf.server.connection.WebSocketClientConnection
import me.jiahuan.stf.server.connection.WebSocketServerConnection

object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Server run")
        Looper.prepare()
        Thread.setDefaultUncaughtExceptionHandler { t, e -> println("Exception on thread $t, errMessage = ${e.message}") }
        val webSocketConnection = WebSocketServerConnection()
        try {
            webSocketConnection.start()
        } catch (e: Exception) {
            println(e.message)
            webSocketConnection.stop()
        }
        Looper.loop()
        println("Server finish")
    }
}