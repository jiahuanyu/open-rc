package me.jiahuan.stf.device.foreground.utils

object ShellUtils {
    fun exec(command: String) {
        try {
            Runtime.getRuntime().exec(command)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}