package me.jiahuan.openrc.device.server.model

import android.graphics.Rect
import androidx.annotation.Keep

@Keep
data class Size(val width: Int, val height: Int) {
    fun rotate(): Size {
        return Size(height, width)
    }

    fun toRect(): Rect {
        return Rect(0, 0, width, height)
    }
}