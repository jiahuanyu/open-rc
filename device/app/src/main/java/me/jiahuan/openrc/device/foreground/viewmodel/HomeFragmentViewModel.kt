package me.jiahuan.openrc.device.foreground.viewmodel

import androidx.lifecycle.MutableLiveData
import me.jiahuan.openrc.device.foreground.base.BaseViewModel

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