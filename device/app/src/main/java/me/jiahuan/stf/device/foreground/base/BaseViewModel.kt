package me.jiahuan.stf.device.foreground.base

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    // 显示隐藏loadingDialog
    val loadingDialogStateLiveData = MutableLiveData<LoadingDialogWrapper?>()

    // toast
    val longToastShowLiveData = MutableLiveData<ArrayList<String>?>()
    val shortToastShowLiveData = MutableLiveData<ArrayList<String>?>()

    // finish
    val finishLiveData = MutableLiveData<ActivityResult?>()

    protected fun showLoadingDialog(message: String = "") {
        loadingDialogStateLiveData.postValue(LoadingDialogWrapper(message, true))
    }

    protected fun dismissLoadingDialog() {
        loadingDialogStateLiveData.postValue(LoadingDialogWrapper("", false))
    }

    protected fun finish(result: ActivityResult? = ActivityResult()) {
        finishLiveData.postValue(result)
    }

    protected fun showLongToast(message: String?) {
        if (message == null) {
            return
        }
        var list = longToastShowLiveData.value
        if (list == null) {
            list = ArrayList()
        }
        list.add(message)
        longToastShowLiveData.postValue(list)
    }

    protected fun showShortToast(message: String?) {
        if (message == null) {
            return
        }
        var list = shortToastShowLiveData.value
        if (list == null) {
            list = ArrayList()
        }
        list.add(message)
        shortToastShowLiveData.postValue(list)
    }

    class ActivityResult(
        var resultCode: Int = Activity.RESULT_CANCELED,
        var data: Intent? = null
    )

    class LoadingDialogWrapper(
        val message: String,
        val show: Boolean
    )
}