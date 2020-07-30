package me.jiahuan.openrc.device.foreground.data

import android.content.Context
import androidx.preference.PreferenceDataStore
import com.google.gson.Gson
import me.jiahuan.openrc.device.AppConstants
import me.jiahuan.openrc.device.R
import me.jiahuan.openrc.device.model.Setting
import me.jiahuan.openrc.device.utils.FileUtils
import java.io.File

class SettingPreferenceDataStore(private val context: Context) : PreferenceDataStore() {
    companion object {
        private val gson = Gson()
    }

    private val file = File(context.getExternalFilesDir(null), AppConstants.STF_DEVICE_SETTING_FILE_NAME)

    init {
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    override fun putString(key: String?, value: String?) {
        if (key == context.resources.getString(R.string.setting_key_agent_address)) {
            val settings = getSetting()
            settings.agentAddress = value
            FileUtils.writeFileContent(file, gson.toJson(settings))
        }
    }

    override fun getString(key: String?, defValue: String?): String? {
        if (key == context.resources.getString(R.string.setting_key_agent_address)) {
            return getSetting().agentAddress
        }
        return defValue
    }

    private fun getSetting(): Setting {
        val fileContent = FileUtils.getFileContent(file)
        if (fileContent.isNullOrBlank()) {
            return Setting()
        }
        return gson.fromJson(fileContent, Setting::class.java)
    }
}