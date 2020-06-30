package com.xingren.stf.connection

import java.nio.ByteBuffer

/**
 * 数据传输链接
 */
interface Connection {
    fun send(connKey: Any, byteBuffer: ByteBuffer)

    fun hasConnection(): Boolean
}