package me.jiahuan.stf.device.foreground.base

import android.R.attr.colorPrimary
import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.gyf.immersionbar.ImmersionBar
import java.lang.reflect.InvocationTargetException

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_RESULT_RAW_DATA = "EXTRA_RESULT_RAW_DATA"
    }

    private var progressDialog: ProgressDialog? = null

    /**
     * 显示loading框，默认显示LoadingDialog
     * 需要自定义重写此方法即可
     */
    protected open fun showLoadingDialog(message: String = "") {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

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
        AlertDialog.Builder(this)
            .setItems(items, itemClickListener)
            .show()
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
        val dialog = AlertDialog.Builder(this)
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

    /**
     * 初始化 ViewModel
     */
    protected abstract fun initializeViewModel(): BaseViewModel

    /**
     * 设计尺寸，如果子类重写并返回大于0的值，则表示开启适配
     * 目前只支持适配宽度
     */
    open fun getDesignWidth(): Int {
        return 0
    }

    /**
     * 设置适配宽度
     * 适配模式需要使用pt作为尺寸单位
     * ScreenSize = sqrt(width * width + height * height) / 72 inch
     *
     * @param resources 资源对象
     * @param designWidth 设计宽度
     *
     * @return resources
     */
    private fun adaptScreen(resources: Resources, designWidth: Int): Resources {
        if (designWidth > 0) {
            val newXdpi = (resources.displayMetrics.widthPixels * 72f) / designWidth
            applyDisplayMetrics(resources, newXdpi)
        }
        return resources
    }

    /**
     * 将新的xdpi应用
     *
     * @param resources 资源对象
     * @param newXdpi 新计算出的xdpi
     *
     * @return resources
     */
    private fun applyDisplayMetrics(resources: Resources, newXdpi: Float) {
        resources.displayMetrics.xdpi = newXdpi
        getApplicationByReflect().resources.displayMetrics.xdpi = newXdpi
    }

    /**
     * 反射获取Application对象
     *
     * @return Application
     */
    private fun getApplicationByReflect(): Application {
        try {
            val activityThread = Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app = activityThread.getMethod("getApplication").invoke(thread)
                ?: throw NullPointerException("u should init first")
            return app as Application
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        throw NullPointerException("u should init first")
    }

    /**
     * 获取 Activity，避免在内部类中使用 XXXX.this this@XXXX 带来的麻烦
     *
     * @return 当前Activity
     */
    protected fun getActivity(): Activity {
        return this
    }

    /**
     * 获取 Activity 上下文，避免在内部类中使用 XXXX.this this@XXXX 带来的麻烦
     *
     * @return 当前 Activity 上下文
     */
    protected fun getContext(): Context {
        return this
    }

    /**
     * 设置全屏模式，系统状态栏隐藏，导航栏透明，Android 9刘海区域显示内容，滑动显示状态栏过段时间消失
     */
    protected fun setWindowFullScreenMode() {
        // >=Android 9 设置刘海显示模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val layoutParams = window.attributes
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = layoutParams
        }
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    /**
     * 设置全屏模式，系统状态栏隐藏，导航栏隐藏，Android 9刘海区域显示内容，滑动显示状态栏过段时间消失
     */
    protected fun setWindowTotalFullScreenMode() {
        // >=Android 9 设置刘海显示模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val layoutParams = window.attributes
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = layoutParams
        }
        // 不设置的话返回的时候导航条一片白
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    override fun getResources(): Resources {
        return adaptScreen(super.getResources(), getDesignWidth())
    }

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
                setResult(
                    result.resultCode,
                    result.data?.putExtra(EXTRA_RESULT_RAW_DATA, intent.extras)
                )
            }
            finish()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = supportFragmentManager.fragments
        if (!fragments.isNullOrEmpty()) {
            fragments.forEach {
                it.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    final override fun onBackPressed() {
        var hasFragmentDisposeBackPressed = false
        val fragment = supportFragmentManager.fragments.filterIsInstance<BaseFragment>().find { it.isVisible }
        if (fragment != null) {
            hasFragmentDisposeBackPressed = fragment.onBackPressed()
        }
        if (!hasFragmentDisposeBackPressed) {
            onBackKeyPressed()
        }
    }

    protected open fun onBackKeyPressed() {
        super.onBackPressed()
    }
}