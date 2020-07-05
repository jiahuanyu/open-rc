package me.jiahuan.stf.device

import android.app.Application
import me.jiahuan.stf.device.foreground.service.ForegroundSocketService

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ForegroundSocketService().connect()
    }
}