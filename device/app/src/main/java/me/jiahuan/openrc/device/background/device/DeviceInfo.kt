package me.jiahuan.openrc.device.background.device

import me.jiahuan.openrc.device.background.model.Size

/**
 * 设备信息
 */
class DeviceInfo(
    var manufacturer: String,
    var model: String,
    var os: String,
    var osVersion: String,
    var size: Size
)