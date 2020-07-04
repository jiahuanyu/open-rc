package me.jiahuan.stf.device.server.net.model.input

enum class InputEventAction(val code: Int, val desc: String) {
    ACTION_DOWN(1, "按下"),
    ACTION_UP(2, "弹起")
}