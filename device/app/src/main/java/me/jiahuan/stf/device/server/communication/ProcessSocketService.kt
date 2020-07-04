package me.jiahuan.stf.device.server.communication

import kotlinx.coroutines.*
import me.jiahuan.stf.device.server.ServerConstants
import java.net.ServerSocket
import java.net.Socket

/**
 * 服务端 Socket 服务
 */
class ProcessSocketService {

    private var socketServerJob: Job? = null

    private fun startListen(serverSocket: ServerSocket) {
        socketServerJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val socket = serverSocket.accept()
                println("有 Client 接入")
            }
        }
    }

    private fun startSocketClient(socket: Socket) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                while (true) {

                }
            } catch (e: Exception) {
                println("Client 通信发生错误 ${e.message}")
            }
        }
    }

    fun start() {
        try {
            val serverSocket = ServerSocket(ServerConstants.SOCKET_PORT)
            startListen(serverSocket)
        } catch (e: Exception) {
            throw AssertionError("无法建立 ${ServerConstants.SOCKET_PORT} 端口的 Socket 监听, error = ${e.message}")
        }
    }
}