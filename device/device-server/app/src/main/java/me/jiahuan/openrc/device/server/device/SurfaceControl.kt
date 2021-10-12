package me.jiahuan.openrc.device.server.device

import android.graphics.Rect
import android.os.IBinder
import android.view.Surface

object SurfaceControl {
    private val CLASS: Class<*> = try {
        Class.forName("android.view.SurfaceControl")
    } catch (e: ClassNotFoundException) {
        throw AssertionError(e)
    }

    fun openTransaction() {
        try {
            CLASS.getMethod("openTransaction").invoke(null)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    fun closeTransaction() {
        try {
            CLASS.getMethod("closeTransaction").invoke(null)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    fun setDisplayProjection(displayToken: IBinder?, orientation: Int, layerStackRect: Rect?, displayRect: Rect?) {
        try {
            CLASS.getMethod("setDisplayProjection", IBinder::class.java, Int::class.javaPrimitiveType, Rect::class.java, Rect::class.java)
                .invoke(null, displayToken, orientation, layerStackRect, displayRect)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    fun setDisplayLayerStack(displayToken: IBinder?, layerStack: Int) {
        try {
            CLASS.getMethod("setDisplayLayerStack", IBinder::class.java, Int::class.javaPrimitiveType).invoke(null, displayToken, layerStack)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    fun setDisplaySurface(displayToken: IBinder?, surface: Surface?) {
        try {
            CLASS.getMethod("setDisplaySurface", IBinder::class.java, Surface::class.java).invoke(null, displayToken, surface)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    fun createDisplay(name: String?, secure: Boolean): IBinder {
        return try {
            CLASS.getMethod("createDisplay", String::class.java, Boolean::class.javaPrimitiveType).invoke(null, name, secure) as IBinder
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    fun destroyDisplay(displayToken: IBinder?) {
        try {
            CLASS.getMethod("destroyDisplay", IBinder::class.java).invoke(null, displayToken)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }
}