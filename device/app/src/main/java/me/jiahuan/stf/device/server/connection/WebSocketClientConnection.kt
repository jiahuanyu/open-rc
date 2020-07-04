package me.jiahuan.stf.device.server.connection

import android.os.Build
import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.MotionEvent.PointerCoords
import android.view.MotionEvent.PointerProperties
import com.google.gson.Gson
import me.jiahuan.stf.device.server.device.Device
import me.jiahuan.stf.device.server.device.DeviceInfo
import me.jiahuan.stf.device.server.device.PointersState
import me.jiahuan.stf.device.server.encoder.ScreenEncoder
import me.jiahuan.stf.device.server.manager.ServiceManager
import me.jiahuan.stf.device.server.net.model.WebSocketMessage
import me.jiahuan.stf.device.server.net.model.input.InputEventAction
import me.jiahuan.stf.device.server.net.model.input.InputEventData
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import java.nio.ByteBuffer


/**
 * WebSocket 服务端
 */
class WebSocketClientConnection(device: Device) : Connection(device), AppWebSocketClient.ClientEventsHandler {

    companion object {
        private val gson = Gson()
    }

    private val appWebSocketClient = AppWebSocketClient(this)

    override fun start() {
        appWebSocketClient.connect()
        ScreenEncoder(this, device).start()
    }

    override fun stop() {
        appWebSocketClient.disconnect()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("onOpen")
        val displayInfo = ServiceManager().displayManager.getDisplayInfo(0)
        val deviceInfo = gson.toJson(
            WebSocketMessage(
                "device_join",
                gson.toJsonTree(
                    DeviceInfo(
                        manufacturer = Build.MANUFACTURER,
                        model = Build.MODEL,
                        os = "android",
                        osVersion = Build.VERSION.RELEASE,
                        size = displayInfo.size
                    )
                )
            )
        )
        // 发送设备信息
        appWebSocketClient.send(deviceInfo)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {

    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("onMessage, text = $text")
        val webSocketMessage = try {
            gson.fromJson(text, WebSocketMessage::class.java)
        } catch (e: Exception) {
            println(e.message)
            null
        }

        if (webSocketMessage != null) {
            when (webSocketMessage.name) {
                "input_event" -> {
                    disposeInputEvent(gson.fromJson(webSocketMessage.data, InputEventData::class.java))
                }
            }
        }
    }

    private var lastTouchDown = 0L
    private val pointersState = PointersState()
    private val pointerProperties = arrayOfNulls<PointerProperties>(PointersState.MAX_POINTERS)
    private val pointerCoords = arrayOfNulls<PointerCoords>(PointersState.MAX_POINTERS)

    init {
        initPointers()
    }

    private fun initPointers() {
        for (i in 0 until PointersState.MAX_POINTERS) {
            val props = PointerProperties()
            props.toolType = MotionEvent.TOOL_TYPE_FINGER
            val coords = PointerCoords()
            coords.orientation = 0f
            coords.size = 1f
            pointerProperties[i] = props
            pointerCoords[i] = coords
        }
    }

    private fun disposeInputEvent(inputEventData: InputEventData) {
        val pointerIndex = pointersState.getPointerIndex(0)
        if (pointerIndex == -1) {
            return
        }
        println("pointerIndex = $pointerIndex")
        val point = inputEventData.description.coordinate
        val pointer = pointersState.get(pointerIndex)
        pointer.point = point
        pointer.pressure = 1.0f
        pointer.isUp = InputEventAction.ACTION_UP.code == inputEventData.action
        val pointerCount = pointersState.update(pointerProperties, pointerCoords)
        println("pointerCount = $pointerCount")
        val now = SystemClock.uptimeMillis()
        when (inputEventData.action) {
            InputEventAction.ACTION_DOWN.code -> {
                lastTouchDown = now
                val result = device.injectEvent(MotionEvent.obtain(lastTouchDown, now, MotionEvent.ACTION_DOWN, pointerCount, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0))
                println("ACTION_DOWN_RESULT = $result")
            }
            InputEventAction.ACTION_UP.code -> {
                val result = device.injectEvent(MotionEvent.obtain(lastTouchDown, now, MotionEvent.ACTION_UP, pointerCount, pointerProperties, pointerCoords, 0, 0, 1.0f, 1.0f, -1, 0, InputDevice.SOURCE_TOUCHSCREEN, 0))
                println("ACTION_UP_RESULT = $result")
            }
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("onClosing")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("onClosed")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("onFailure")
    }

    override fun send(byteBuffer: ByteBuffer) {
        appWebSocketClient.send(byteBuffer)
    }
}