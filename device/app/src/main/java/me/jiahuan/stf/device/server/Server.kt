package me.jiahuan.stf.device.server

import android.os.Looper
import me.jiahuan.stf.device.server.communication.ProcessSocketService
import me.jiahuan.stf.device.server.connection.WebSocketClientConnection
import me.jiahuan.stf.device.server.device.Device

object Server {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Server run")
        Looper.prepare()
        // 先建立本地 Socket 服务监听
        ProcessSocketService().start()
        WebSocketClientConnection(Device()).start()
        Looper.loop()
        println("Server finish")
    }
}