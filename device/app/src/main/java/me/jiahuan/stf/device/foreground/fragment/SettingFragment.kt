package me.jiahuan.stf.device.foreground.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.jiahuan.stf.device.R
import me.jiahuan.stf.device.foreground.data.SettingPreferenceDataStore
import me.jiahuan.stf.device.foreground.viewmodel.HomeFragmentViewModel


class SettingFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    companion object {
        const val TAG = "SettingFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.preferenceDataStore = SettingPreferenceDataStore(requireContext())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.layout_fragment_setting, rootKey)
        initialize()
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (preference?.key == resources.getString(R.string.setting_key_agent_address)) {
            setAgentAddress(newValue.toString())
        }
        return true
    }

    private fun initialize() {
        findPreference<Preference>(resources.getString(R.string.setting_key_agent_address))?.onPreferenceChangeListener = this
        setAgentAddress(preferenceManager.sharedPreferences.getString(resources.getString(R.string.setting_key_agent_address), ""))
    }

    private fun setAgentAddress(address: String?) {
        findPreference<Preference>(resources.getString(R.string.setting_key_agent_address))?.summary = if (address.isNullOrBlank()) "未设置" else address
    }
}