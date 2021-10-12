package me.jiahuan.openrc.device.server

import android.os.Looper
import me.jiahuan.openrc.device.server.connection.WebSocketClientConnection
import me.jiahuan.openrc.device.server.device.Device

/**
 * 程序入口
 */
object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Server run")
        Looper.prepare()
        // 建立 WebSocket 链接
        WebSocketClientConnection(Device()).start()
        Looper.loop()
        println("Server finish")
    }
}
