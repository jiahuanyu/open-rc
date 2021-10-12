package me.jiahuan.openrc.device.background.connection

import me.jiahuan.openrc.device.background.controller.Controller
import me.jiahuan.openrc.device.background.device.Device
import java.nio.ByteBuffer

/**
 * 数据传输链接
 */
abstract class Connection(protected val device: Device) {

    protected val controller by lazy {
        Controller(this, device)
    }

    abstract fun start()

    abstract fun stop()

    abstract fun send(byteBuffer: ByteBuffer)
}