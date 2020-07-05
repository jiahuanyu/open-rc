package me.jiahuan.stf.device.background

import android.os.Looper
import me.jiahuan.stf.device.background.communication.BackgroundSocketService
import me.jiahuan.stf.device.background.connection.WebSocketClientConnection
import me.jiahuan.stf.device.background.device.Device

object Background {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Server run")
        Looper.prepare()
        // 先建立本地 Socket 服务监听
        BackgroundSocketService().start()
        WebSocketClientConnection(Device()).start()
        Looper.loop()
        println("Server finish")
    }
}