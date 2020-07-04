package me.jiahuan.stf.device.client.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import me.jiahuan.stf.device.R
import me.jiahuan.stf.device.databinding.LayoutFragmentHomeBinding
import me.jiahuan.stf.device.client.utils.DeviceUtils


class HomeFragment : Fragment() {

    companion object {
        const val TAG = "HomeFragment"
    }

    private lateinit var binding: LayoutFragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "${DeviceUtils.isDeviceRooted()}")
        checkStfServerProcess()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_fragment_home, container, false)
        return binding.root
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    /**
     * 检查 stf_device_server 进程是否存在
     */
    private fun checkStfServerProcess() {
        val context = this.context ?: return
        val processExist = DeviceUtils.checkStfServerProcess(context)
        Log.d(TAG, "stf_device_server 进程是否存在 $processExist")
    }
}