package me.jiahuan.stf.device.foreground.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.jiahuan.stf.device.AppConstants
import me.jiahuan.stf.device.foreground.base.BaseViewModel
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket

class HomeFragmentViewModel : BaseViewModel() {

    val backgroundServiceAliveLiveData = MutableLiveData<Boolean?>()

    /**
     * 检查后端服务是否存活
     */
//    fun checkBackgroundServiceAlive() {
//        showLoadingDialog("正在检测STF后台服务是否启动")
//        viewModelScope.launch {
//            backgroundServiceAliveLiveData.value = withContext(Dispatchers.IO) {
//                var socket: Socket? = null
//                var bufferedReader: BufferedReader? = null
//                var bufferedWriter: BufferedWriter? = null
//                try {
//                    socket = Socket(InetAddress.getLoopbackAddress(), AppConstants.SOCKET_PORT)
//                    bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
//                    bufferedWriter = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
//                    bufferedWriter.write(AppConstants.CODE_AUTH.toString())
//                    bufferedWriter.newLine()
//                    bufferedWriter.flush()
//                    val readLine = bufferedReader.readLine()
//                    Log.d("HomeFragment", "readLine = $readLine")
//                    return@withContext AppConstants.CODE_SUCCESS.toString() == readLine
//                } catch (e: Exception) {
//                } finally {
//                    try {
//                        bufferedReader?.close()
//                    } catch (e: Exception) {
//                    }
//                    try {
//                        bufferedWriter?.close()
//                    } catch (e: Exception) {
//                    }
//                    try {
//                        socket?.close()
//                    } catch (e: Exception) {
//                    }
//                }
//                return@withContext false
//            }
//            dismissLoadingDialog()
//        }
//    }
}