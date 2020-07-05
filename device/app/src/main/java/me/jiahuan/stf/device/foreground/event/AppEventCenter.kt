package me.jiahuan.stf.device.foreground.event

import androidx.lifecycle.MutableLiveData

object AppEventCenter  {
    val backgroundServiceAliveLiveData = MutableLiveData<Boolean?>()
}