package me.jiahuan.openrc.device.server.device

import me.jiahuan.openrc.device.server.model.Size

/**
 * 设备信息
 */
class DeviceInfo(
    // 制造商
    var manufacturer: String,
    //
    var model: String,
    // 系统
    var os: String,
    // 系统版本
    var osVersion: String,
    // 屏幕分辨率
    var screenSize: Size
)