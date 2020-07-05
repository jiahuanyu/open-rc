package me.jiahuan.stf.device.foreground.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.jiahuan.stf.device.R
import me.jiahuan.stf.device.foreground.viewmodel.SettingFragmentViewModel


class SettingFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    companion object {
        const val TAG = "SettingFragment"
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(SettingFragmentViewModel::class.java)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.layout_fragment_setting, rootKey)
        initialize()
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (preference?.key == resources.getString(R.string.setting_key_agenter_address)) {
            setAgenterAddress(newValue.toString())
        }
        return true
    }

    private fun initialize() {
        findPreference<Preference>(resources.getString(R.string.setting_key_agenter_address))?.onPreferenceChangeListener = this
        setAgenterAddress(preferenceManager.sharedPreferences.getString(resources.getString(R.string.setting_key_agenter_address), ""))
    }

    private fun setAgenterAddress(address: String?) {
        findPreference<Preference>(resources.getString(R.string.setting_key_agenter_address))?.summary = if (address.isNullOrBlank()) "未设置" else address
    }
}