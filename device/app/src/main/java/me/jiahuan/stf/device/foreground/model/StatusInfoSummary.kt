package me.jiahuan.stf.device.foreground.model

import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import me.jiahuan.stf.device.BR

@Keep
class StatusInfoSummary : BaseObservable() {
    // stf 后台状态
    @get:Bindable
    var stfBackgroundServiceStatusInfo = StatusInfo()
        set(value) {
            field = value
            notifyPropertyChanged(BR.stfBackgroundServiceStatusInfo)
        }
}
