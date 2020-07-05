package me.jiahuan.stf.device.background.model

import androidx.annotation.Keep

@Keep
class Point(val x: Int, val y: Int) {
    override fun toString(): String {
        return "Point(x=$x, y=$y)"
    }
}