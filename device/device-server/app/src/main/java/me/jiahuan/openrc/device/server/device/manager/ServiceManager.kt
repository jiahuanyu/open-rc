package me.jiahuan.openrc.device.server.device.manager

import android.annotation.SuppressLint
import android.os.IBinder
import android.os.IInterface

@SuppressLint("PrivateApi,DiscouragedPrivateApi")
class ServiceManager {

    private val getServiceMethod = try {
        Class.forName("android.os.ServiceManager")
            .getDeclaredMethod("getService", String::class.java)
    } catch (e: Exception) {
        throw AssertionError(e)
    }

    val displayManager =
        DisplayManager(getService("display", "android.hardware.display.IDisplayManager"))

    val inputManager = InputManager(getService("input", "android.hardware.input.IInputManager"))

    private fun getService(service: String, type: String): IInterface {
        return try {
            val binder = getServiceMethod.invoke(null, service) as IBinder
            val asInterfaceMethod = Class.forName("$type\$Stub")
                .getMethod("asInterface", IBinder::class.java)
            asInterfaceMethod.invoke(null, binder) as IInterface
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }
}
