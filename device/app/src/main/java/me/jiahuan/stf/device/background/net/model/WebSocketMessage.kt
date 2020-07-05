package me.jiahuan.stf.device.background.net.model

import androidx.annotation.Keep
import com.google.gson.JsonElement
import java.util.*

@Keep
class WebSocketMessage(
    var name: String,
    var data: JsonElement? = null,
    var guid: String = UUID.randomUUID().toString()
)