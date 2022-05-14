package com.lgtm.simple_timer.page.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.lgtm.simple_timer.R

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}