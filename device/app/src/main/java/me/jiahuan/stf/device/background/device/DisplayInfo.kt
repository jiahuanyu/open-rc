package me.jiahuan.stf.device.background.device

import me.jiahuan.stf.device.background.model.Size

class DisplayInfo(val displayId: Int, val size: Size, val rotation: Int, val layerStack: Int, val flags: Int) {
    override fun toString(): String {
        return "DisplayInfo(displayId=$displayId, size=$size, rotation=$rotation, layerStack=$layerStack, flags=$flags)"
    }
}