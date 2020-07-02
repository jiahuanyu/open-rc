package me.jiahuan.stf.server.connection

import java.nio.ByteBuffer

/**
 * 数据传输链接
 */
interface Connection {
    fun start()

    fun stop()

    fun send(byteBuffer: ByteBuffer)
}