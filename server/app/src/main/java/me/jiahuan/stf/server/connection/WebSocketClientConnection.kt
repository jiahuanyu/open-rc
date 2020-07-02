package me.jiahuan.stf.server.connection

import android.os.Build
import com.google.gson.Gson
import me.jiahuan.stf.server.ScreenEncoder
import me.jiahuan.stf.server.model.DeviceInfo
import me.jiahuan.stf.server.model.WebSocketMessage
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import java.nio.ByteBuffer

/**
 * WebSocket 服务端
 */
class WebSocketClientConnection : Connection, AppWebSocketClient.ClientEventsHandler {

    companion object {
        private val gson = Gson()
    }

    private val appWebSocketClient = AppWebSocketClient(this)

    private val screenEncoder = ScreenEncoder(this)

    override fun start() {
        appWebSocketClient.connect()
    }

    override fun stop() {
        appWebSocketClient.disconnect()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("onOpen")
        val deviceInfo = gson.toJson(
            WebSocketMessage(
                "device_join",
                DeviceInfo(
                    manufacturer = Build.MANUFACTURER,
                    model = Build.MODEL,
                    os = "android",
                    osVersion = Build.VERSION.RELEASE
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
                "start" -> {
                    screenEncoder.start()
                }
                "stop" -> {
                    screenEncoder.stop()
                }
            }
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("onClosing")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("onClosed")
        screenEncoder.stop()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("onFailure")
        screenEncoder.stop()
    }

    override fun send(byteBuffer: ByteBuffer) {
        appWebSocketClient.send(byteBuffer)
    }
}