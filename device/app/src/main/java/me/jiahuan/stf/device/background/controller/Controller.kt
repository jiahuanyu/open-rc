package me.jiahuan.stf.device.background.controller

import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import me.jiahuan.stf.device.background.connection.Connection
import me.jiahuan.stf.device.background.device.Device
import me.jiahuan.stf.device.background.device.PointersState
import me.jiahuan.stf.device.background.net.model.input.InputEventAction
import me.jiahuan.stf.device.background.net.model.input.InputEventData

/**
 * 控制相关
 */
class Controller(private val connection: Connection, private val device: Device) {
    private val DEVICE_ID_VIRTUAL = -1

    private var lastTouchDown = 0L
    private val pointersState = PointersState()
    private val pointerProperties = arrayOfNulls<MotionEvent.PointerProperties>(PointersState.MAX_POINTERS)
    private val pointerCoords = arrayOfNulls<MotionEvent.PointerCoords>(PointersState.MAX_POINTERS)

    init {
        initPointers()
    }

    private fun initPointers() {
        for (i in 0 until PointersState.MAX_POINTERS) {
            val props = MotionEvent.PointerProperties()
            props.toolType = MotionEvent.TOOL_TYPE_FINGER
            val coords = MotionEvent.PointerCoords()
            coords.orientation = 0f
            coords.size = 1f
            pointerProperties[i] = props
            pointerCoords[i] = coords
        }
    }

    fun disposeInputEvent(inputEventData: InputEventData) {
        val pointerIndex = pointersState.getPointerIndex(0)
        if (pointerIndex == -1) {
            return
        }
        val point = inputEventData.description.coordinate
        val pointer = pointersState.get(pointerIndex)
        pointer.point = point
        pointer.pressure = 1.0f
        pointer.isUp = InputEventAction.ACTION_UP.code == inputEventData.action
        val pointerCount = pointersState.update(pointerProperties, pointerCoords)
        val now = SystemClock.uptimeMillis()
        when (inputEventData.action) {
            InputEventAction.ACTION_DOWN.code -> {
                lastTouchDown = now
                val result = device.injectEvent(MotionEvent.obtain(lastTouchDown, now, MotionEvent.ACTION_DOWN, pointerCount, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0))
                println("ACTION_DOWN_RESULT = $result")
            }
            InputEventAction.ACTION_MOVE.code -> {
                val result = device.injectEvent(MotionEvent.obtain(lastTouchDown, now, MotionEvent.ACTION_MOVE, pointerCount, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0))
                println("ACTION_MOVE_RESULT = $result")
            }
            InputEventAction.ACTION_UP.code -> {
                val result = device.injectEvent(MotionEvent.obtain(lastTouchDown, now, MotionEvent.ACTION_UP, pointerCount, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0))
                println("ACTION_UP_RESULT = $result")
            }
        }
    }
}