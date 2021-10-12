package me.jiahuan.openrc.device.server.connection

import me.jiahuan.openrc.device.server.controller.Controller
import me.jiahuan.openrc.device.server.device.Device
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