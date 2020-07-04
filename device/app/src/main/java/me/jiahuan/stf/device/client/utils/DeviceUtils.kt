package me.jiahuan.stf.device.client.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import me.jiahuan.stf.device.server.ServerConstants
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


object DeviceUtils {
    fun isDeviceRooted(): Boolean {
        return checkDebugSystem() || checkSuFile() || checkWhichSu()
    }

    private fun checkDebugSystem(): Boolean {
        val buildTags = android.os.Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkSuFile(): Boolean {
        val paths = arrayOf("/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su")
        for (path in paths) {
            if (File(path).exists()) {
                return true
            }
        }
        return false
    }

    private fun checkWhichSu(): Boolean {
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("/system/xbin/which su")
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            if (bufferedReader.readLine() != null) {
                return true
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            process?.destroy()
        }
        return false
    }

    fun checkStfServerProcess(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos = activityManager.runningAppProcesses
        for (info in processInfos) {
            Log.d("device", info.processName)
            if (ServerConstants.PROCESS_NAME == info.processName) {
                return true
            }
        }
        return false
    }
}