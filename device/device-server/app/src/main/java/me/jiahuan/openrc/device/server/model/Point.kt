package me.jiahuan.openrc.device.server.model

import androidx.annotation.Keep

@Keep
class Point(val x: Int, val y: Int) {
    override fun toString(): String {
        return "Point(x=$x, y=$y)"
    }
}