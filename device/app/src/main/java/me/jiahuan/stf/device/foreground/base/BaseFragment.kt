package me.jiahuan.stf.device.foreground.base

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

abstract class BaseFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null

    /**
     * 显示Item选择对话框
     *
     * @param items 选择Item
     * @param itemClickListener Item点击事件
     */
    protected fun showItemDialog(
        items: Array<String>,
        itemClickListener: DialogInterface.OnClickListener? = null
    ) {
        context?.let { ctx ->
            AlertDialog.Builder(ctx)
                .setItems(items, itemClickListener)
                .show()
        }
    }


    /**
     * 显示确认对话框
     *
     * @param title 标题
     * @param message 显示信息
     * @param positiveButtonText Positive按钮标题
     * @param positiveClickListener Positive按钮点击事件
     * @param negativePositiveText Negative按钮标题
     * @param negativeClickListener Negative按钮点击事件
     * @param cancelable 点击弹框外是否消失
     * @param autoDismiss 点击Positive或者Negative是否自动消失
     *
     */
    protected fun showConfirmDialog(
        title: String = "",
        message: String = "",
        positiveButtonText: String = "",
        positiveClickListener: DialogInterface.OnClickListener? = null,
        negativePositiveText: String = "",
        negativeClickListener: DialogInterface.OnClickListener? = null,
        cancelable: Boolean = true,
        autoDismiss: Boolean = true
    ) {
        context?.let { ctx ->
            val dialog = AlertDialog.Builder(ctx)
                .setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, positiveClickListener)
                .setNegativeButton(negativePositiveText, negativeClickListener)
                .show()

            if (!autoDismiss) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    positiveClickListener?.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
                }

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                    negativeClickListener?.onClick(dialog, DialogInterface.BUTTON_NEGATIVE)
                }
            }
        }
    }

    /**
     * 显示loading框，默认显示LoadingDialog
     * 需要自定义重写此方法即可
     */
    protected open fun showLoadingDialog(message: String = "") {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context)
            progressDialog?.setCancelable(false)
        }
        progressDialog?.setMessage(message)
        progressDialog?.show()
    }

    /**
     * 隐藏loading框
     * 需要自定义重写此方法即可
     */
    protected open fun dismissLoadDialog() {
        progressDialog?.dismiss()
    }

    /**
     * 显示 Long Toast 提示
     *
     * @param message 提示信息
     */
    protected open fun showLongToast(message: String?) {
        if (message == null) {
            return
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * 显示 Short Toast 提示
     *
     * @param message 提示信息
     */
    protected open fun showShortToast(message: String?) {
        if (message == null) {
            return
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * 返回按键
     */
    internal fun onBackPressed(): Boolean {
        var hasFragmentDisposeBackPressed = false
        val fragment = childFragmentManager.fragments.filterIsInstance<BaseFragment>().find { it.isVisible }
        if (fragment != null) {
            hasFragmentDisposeBackPressed = fragment.onBackPressed()
        }
        if (!hasFragmentDisposeBackPressed) {
            return onBackKeyPressed()
        }
        return hasFragmentDisposeBackPressed
    }

    protected open fun onBackKeyPressed(): Boolean {
        return false
    }

    /**
     * 初始化 ViewModel
     */
    protected abstract fun initializeViewModel(): BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = initializeViewModel()

        viewModel.loadingDialogStateLiveData.observe(this, Observer { loadingDialogWrapper ->
            if (loadingDialogWrapper == null) {
                dismissLoadDialog()
            } else {
                if (loadingDialogWrapper.show) {
                    showLoadingDialog(loadingDialogWrapper.message)
                } else {
                    dismissLoadDialog()
                }
            }
        })

        viewModel.longToastShowLiveData.observe(this, Observer { messages ->
            messages?.forEach {
                showLongToast(it)
            }
            messages?.clear()
        })

        viewModel.shortToastShowLiveData.observe(this, Observer { messages ->
            messages?.forEach {
                showShortToast(it)
            }
            messages?.clear()
        })

        viewModel.finishLiveData.observe(this, Observer { result ->
            if (result != null) {
                activity?.setResult(
                    result.resultCode,
                    result.data?.putExtra(BaseActivity.EXTRA_RESULT_RAW_DATA, activity?.intent?.extras)
                )
            }
            activity?.finish()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = childFragmentManager.fragments
        if (!fragments.isNullOrEmpty()) {
            fragments.forEach {
                it.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}