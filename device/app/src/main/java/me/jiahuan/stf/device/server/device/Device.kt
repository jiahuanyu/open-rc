package me.jiahuan.stf.device.server.device

import android.os.Build
import android.os.SystemClock
import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyCharacterMap
import android.view.KeyEvent
import me.jiahuan.stf.device.server.manager.InputManager
import me.jiahuan.stf.device.server.manager.ServiceManager


/**
 * 设备
 */
class Device {
    private val serviceManager = ServiceManager()

    val displayId = 0

    val displayInfo = serviceManager.displayManager.getDisplayInfo(displayId)

    val screenInfo = ScreenInfo.computeScreenInfo(displayInfo, 720, -1)

    val supportInputEvents = displayId == 0 || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun injectEvent(inputEvent: InputEvent): Boolean {
        if (!supportInputEvents) {
            return false
        }
        return serviceManager.inputManager.injectInputEvent(inputEvent, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC)
    }

    fun injectKeycode(keyCode: Int): Boolean {
        return injectKeyEvent(KeyEvent.ACTION_DOWN, keyCode, 0, 0) && injectKeyEvent(KeyEvent.ACTION_UP, keyCode, 0, 0)
    }

    private fun injectKeyEvent(action: Int, keyCode: Int, repeat: Int, metaState: Int): Boolean {
        val now = SystemClock.uptimeMillis()
        val event = KeyEvent(
            now, now, action, keyCode, repeat, metaState, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
            InputDevice.SOURCE_KEYBOARD
        )
        return injectEvent(event)
    }

}