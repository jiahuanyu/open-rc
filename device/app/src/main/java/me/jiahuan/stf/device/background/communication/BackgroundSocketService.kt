package me.jiahuan.stf.device.background.communication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.jiahuan.stf.device.AppConstants
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket

/**
 * 服务端 Socket 服务
 */
class BackgroundSocketService {

    private fun startListen(serverSocket: ServerSocket) {
        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val socket = serverSocket.accept()
                println("有 Client 接入")
                startSocketClient(socket)
            }
        }
    }

    private fun startSocketClient(socket: Socket) {
        val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val bufferedWriter = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                while (isActive) {
                    val readLine = bufferedReader.readLine() ?: break
                    if (AppConstants.CODE_AUTH.toString() == readLine) {
                        bufferedWriter.write(AppConstants.CODE_SUCCESS.toString())
                        bufferedWriter.newLine()
                        bufferedWriter.flush()
                        println("auth success")
                    }
                }
            } catch (e: Exception) {
                println("Client 通信发生错误 ${e.message}")
            }
            println("Client 拜拜")
            try {
                bufferedReader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                bufferedWriter.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun start() {
        try {
            val serverSocket = ServerSocket(AppConstants.SOCKET_PORT)
            startListen(serverSocket)
        } catch (e: Exception) {
            throw AssertionError("无法建立 ${AppConstants.SOCKET_PORT} 端口的 Socket 监听, error = ${e.message}")
        }
    }
}