package me.jiahuan.stf.device.background.connection

import android.os.Build
import com.google.gson.Gson
import me.jiahuan.stf.device.background.device.Device
import me.jiahuan.stf.device.background.device.DeviceInfo
import me.jiahuan.stf.device.background.encoder.ScreenEncoder
import me.jiahuan.stf.device.background.manager.ServiceManager
import me.jiahuan.stf.device.background.net.model.WebSocketMessage
import me.jiahuan.stf.device.background.net.model.input.InputEventData
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
        try {
            val webSocketMessage = try {
                gson.fromJson(text, WebSocketMessage::class.java)
            } catch (e: Exception) {
                println(e.message)
                null
            }

            if (webSocketMessage != null) {
                when (webSocketMessage.name) {
                    "input_event" -> {
                        controller.disposeInputEvent(gson.fromJson(webSocketMessage.data, InputEventData::class.java))
                    }
                }
            }
        } catch (e: Exception) {
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