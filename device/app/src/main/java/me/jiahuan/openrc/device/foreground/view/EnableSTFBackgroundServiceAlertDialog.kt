package me.jiahuan.openrc.device.foreground.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

class EnableSTFBackgroundServiceAlertDialog(context: Context) : AlertDialog(context), DialogInterface.OnClickListener {
    override fun onClick(dialog: DialogInterface?, which: Int) {

    }

    override fun show() {
        setTitle("请启动STF后台服务")
        setMessage("xxxxxx")
        setButton(BUTTON_POSITIVE, "ADB", this)
        super.show()
    }
}