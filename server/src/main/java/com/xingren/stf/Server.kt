package com.xingren.stf

import com.xingren.stf.connection.WebSocketConnection

object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Server run")
        Thread.setDefaultUncaughtExceptionHandler { t, e -> println("Exception on thread $t, errMessage = ${e.message}") }
        val webSocketConnection = WebSocketConnection()
        try {
            webSocketConnection.start()
        } catch (e: Exception) {
            println(e.message)
        } finally {
            webSocketConnection.stop()
            println("Server finish")
        }
    }
}