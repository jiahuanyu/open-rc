package me.jiahuan.stf.device.server.model

import android.graphics.Rect

class Size(val width: Int, val height: Int) {
    fun rotate(): Size {
        return Size(height, width)
    }

    fun toRect(): Rect {
        return Rect(0, 0, width, height)
    }

    override fun toString(): String {
        return "Size(width=$width, height=$height)"
    }
}