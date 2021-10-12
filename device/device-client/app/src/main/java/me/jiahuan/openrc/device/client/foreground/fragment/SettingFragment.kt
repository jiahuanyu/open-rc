package me.jiahuan.openrc.device.foreground.fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.jiahuan.openrc.device.R
import me.jiahuan.openrc.device.foreground.data.SettingPreferenceDataStore


class SettingFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    companion object {
        const val TAG = "SettingFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.layout_fragment_setting, rootKey)
        preferenceManager.preferenceDataStore = SettingPreferenceDataStore(requireContext())
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
        setAgentAddress(preferenceManager.preferenceDataStore?.getString(resources.getString(R.string.setting_key_agent_address), ""))
    }

    private fun setAgentAddress(address: String?) {
        findPreference<Preference>(resources.getString(R.string.setting_key_agent_address))?.summary = if (address.isNullOrBlank()) "未设置" else address
    }
}