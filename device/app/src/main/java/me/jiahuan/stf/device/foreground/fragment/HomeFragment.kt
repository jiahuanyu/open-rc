package me.jiahuan.stf.device.foreground.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import me.jiahuan.stf.device.R
import me.jiahuan.stf.device.databinding.LayoutFragmentHomeBinding
import me.jiahuan.stf.device.foreground.event.AppEventCenter
import me.jiahuan.stf.device.foreground.model.StatusInfo
import me.jiahuan.stf.device.foreground.model.StatusInfoSummary
import me.jiahuan.stf.device.foreground.viewmodel.HomeFragmentViewModel


class HomeFragment : Fragment() {

    companion object {
        const val TAG = "HomeFragment"
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
    }

    private lateinit var binding: LayoutFragmentHomeBinding

    private val statusInfoSummary = StatusInfoSummary()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_fragment_home, container, false)
        binding.statusInfoSummary = statusInfoSummary
        return binding.root
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            checkAll()
        }
    }

    private fun checkAll() {
        AppEventCenter.backgroundServiceAliveLiveData.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            showSTFBackgroundServiceStatusInfo(it)
        })
//        viewModel.checkBackgroundServiceAlive()
    }

    private fun showSTFBackgroundServiceStatusInfo(started: Boolean) {
        val stfBackgroundServiceStatusInfo = StatusInfo()
        stfBackgroundServiceStatusInfo.success = started
        stfBackgroundServiceStatusInfo.errMessage = if (started) {
            "STF后台服务已开启"
        } else {
            "STF后台服务未开启"
        }
        statusInfoSummary.stfBackgroundServiceStatusInfo = stfBackgroundServiceStatusInfo
    }

    override fun onResume() {
        super.onResume()
        checkAll()
    }
}