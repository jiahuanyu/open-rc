package me.jiahuan.stf.device.server.controller

import me.jiahuan.stf.device.server.connection.Connection
import me.jiahuan.stf.device.server.device.Device

/**
 * 控制相关
 */
class Controller(private val connection: Connection, private val device: Device) {
    private val DEVICE_ID_VIRTUAL = -1
}