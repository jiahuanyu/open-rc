package me.jiahuan.stf.device.background.manager

import android.os.IInterface
import android.view.InputEvent
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class InputManager(private val manager: IInterface) {
    companion object {
        const val INJECT_INPUT_EVENT_MODE_ASYNC = 0
    }

    private var injectInputEventMethod: Method? = null

    fun injectInputEvent(inputEvent: InputEvent?, mode: Int): Boolean {
        return try {
            if (injectInputEventMethod == null) {
                injectInputEventMethod = manager.javaClass.getMethod("injectInputEvent", InputEvent::class.java, Int::class.javaPrimitiveType)
            }
            injectInputEventMethod?.invoke(manager, inputEvent, mode) as? Boolean ?: false
        } catch (e: InvocationTargetException) {
            false
        } catch (e: IllegalAccessException) {
            false
        } catch (e: NoSuchMethodException) {
            false
        }
    }
}