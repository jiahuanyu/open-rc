package me.jiahuan.openrc.device.background.net.model.input

import androidx.annotation.Keep

@Keep
enum class InputEventAction(val code: Int, val desc: String) {
    ACTION_DOWN(1, "按下"),
    ACTION_MOVE(2, "移动"),
    ACTION_UP(3, "弹起")
}