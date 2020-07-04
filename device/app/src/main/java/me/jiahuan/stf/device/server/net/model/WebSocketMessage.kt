package me.jiahuan.stf.device.server.net.model

import com.google.gson.JsonElement
import java.util.*

class WebSocketMessage(
    var name: String,
    var data: JsonElement? = null,
    var guid: String = UUID.randomUUID().toString()
)