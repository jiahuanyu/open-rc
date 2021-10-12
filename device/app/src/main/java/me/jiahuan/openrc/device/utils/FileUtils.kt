package me.jiahuan.openrc.device.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object FileUtils {
    fun getFileContent(file: File): String? {
        val fileInputStream = FileInputStream(file)
        try {
            val buffer = ByteArray(1024)
            val stringBuilder = StringBuilder()
            var len = fileInputStream.read(buffer)
            while (len > 0) {
                stringBuilder.append(String(buffer, 0, len))
                len = fileInputStream.read(buffer)
            }
            return stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileInputStream.close()
            } catch (e: Exception) {
            }
        }
        return null
    }

    fun writeFileContent(file: File, value: String): Boolean {
        val fileOutputStream = FileOutputStream(file)
        try {
            fileOutputStream.write(value.toByteArray())
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileOutputStream.close()
            } catch (e: Exception) {
            }
        }
        return false
    }
}