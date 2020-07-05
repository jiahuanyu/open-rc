package me.jiahuan.stf.device.foreground.model

import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import me.jiahuan.stf.device.BR

@Keep
class StatusInfo : BaseObservable() {
    @get:Bindable
    var success: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.success)
        }

    @get:Bindable
    var errMessage: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.errMessage)
        }
}