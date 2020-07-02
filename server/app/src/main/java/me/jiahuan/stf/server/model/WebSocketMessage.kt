package me.jiahuan.stf.server.model

import java.util.*

class WebSocketMessage(
    var name: String,
    var data: Any? = null,
    var guid: String = UUID.randomUUID().toString()
)