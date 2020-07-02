package me.jiahuan.stf.server

import android.os.IInterface

class DisplayManager(private val manager: IInterface) {
    fun getDisplayInfo(displayId: Int): DisplayInfo {
        return try {
            val displayInfo = manager.javaClass.getMethod("getDisplayInfo", Int::class.javaPrimitiveType).invoke(manager, displayId) ?: throw AssertionError("DisplayId is not correct, support displayIds = $displayIds")
            val cls: Class<*> = displayInfo.javaClass
            val width = cls.getDeclaredField("logicalWidth").getInt(displayInfo)
            val height = cls.getDeclaredField("logicalHeight").getInt(displayInfo)
            val rotation = cls.getDeclaredField("rotation").getInt(displayInfo)
            val layerStack = cls.getDeclaredField("layerStack").getInt(displayInfo)
            val flags = cls.getDeclaredField("flags").getInt(displayInfo)
            DisplayInfo(displayId, Size(width, height), rotation, layerStack, flags)
        } catch (e: Exception) {
            throw AssertionError(e)
        }
    }

    val displayIds: IntArray
        get() = try {
            manager.javaClass.getMethod("getDisplayIds").invoke(manager) as IntArray
        } catch (e: Exception) {
            throw AssertionError(e)
        }
}