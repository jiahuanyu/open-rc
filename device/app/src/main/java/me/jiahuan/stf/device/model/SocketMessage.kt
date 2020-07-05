package me.jiahuan.stf.device.model

import androidx.annotation.Keep
import com.google.gson.JsonElement

@Keep
class SocketMessage(
    var name: String,
    var data: JsonElement
)