package me.jiahuan.openrc.device

import android.app.Application
import me.jiahuan.openrc.device.foreground.service.ForegroundSocketService

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ForegroundSocketService().connect()
    }
}