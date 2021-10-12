package me.jiahuan.openrc.device.foreground.event

import androidx.lifecycle.MutableLiveData

object AppEventCenter  {
    val backgroundServiceAliveLiveData = MutableLiveData<Boolean?>()
}