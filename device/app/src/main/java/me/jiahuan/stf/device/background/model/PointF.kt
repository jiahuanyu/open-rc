package me.jiahuan.stf.device.background.model

import androidx.annotation.Keep

@Keep
class PointF(val x: Float, val y: Float) {
    override fun toString(): String {
        return "PointF(x=$x, y=$y)"
    }
}