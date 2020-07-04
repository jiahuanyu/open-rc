package me.jiahuan.stf.device.server.device

import me.jiahuan.stf.device.server.model.Size

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