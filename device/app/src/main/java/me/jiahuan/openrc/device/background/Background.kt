package me.jiahuan.openrc.device.background

import android.os.Looper
import androidx.annotation.Keep
import me.jiahuan.openrc.device.background.communication.BackgroundSocketService
import me.jiahuan.openrc.device.background.connection.WebSocketClientConnection
import me.jiahuan.openrc.device.background.device.Device

@Keep
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