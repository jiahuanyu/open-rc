package com.xingren.stf

import android.graphics.Rect
import java.util.*

class Size(val width: Int, val height: Int) {

    fun rotate(): Size {
        return Size(height, width)
    }

    fun toRect(): Rect {
        return Rect(0, 0, width, height)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Size
        return width == other.width && height == other.height
    }

    override fun hashCode(): Int {
        return Objects.hash(width, height)
    }

    override fun toString(): String {
        return "Size(width=$width, height=$height)"
    }
}