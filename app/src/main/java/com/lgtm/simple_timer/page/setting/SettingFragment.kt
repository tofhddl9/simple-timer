package com.lgtm.simple_timer.page.setting

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.lgtm.simple_timer.R
import android.media.RingtoneManager

import android.content.Intent
import android.net.Uri


class SettingFragment : PreferenceFragmentCompat() {

    private val settingPreference by lazy { SettingSharedPreference(requireContext()) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<SwitchPreference>("key_does_ring")?.setOnPreferenceChangeListener { _, option ->
            settingPreference.doesRing = option as Boolean
            true
        }

        findPreference<SwitchPreference>("key_does_vibrate")?.setOnPreferenceChangeListener { _, option ->
            settingPreference.doesVibrate = option as Boolean
            true
        }

        findPreference<SwitchPreference>("key_does_ring_infinitely")?.setOnPreferenceChangeListener { _, option ->
            settingPreference.doesRingInfinitely = option as Boolean
            true
        }

        findPreference<SwitchPreference>("key_does_always_on_always")?.setOnPreferenceChangeListener { _, option ->
            settingPreference.doesAlwaysOnDisplay = option as Boolean
            true
        }

        findPreference<Preference>("key_select_ringtone")?.setOnPreferenceClickListener { it ->
            showRingtonePicker()
            true
        }
    }

    private fun showRingtonePicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
        }

        startActivityForResult(intent, REQUEST_CODE_RINGTONE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_RINGTONE_PICKER -> {
                    val uri = data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    settingPreference.ringtone = uri?.toString()
                }
            }
        }

    }

    companion object {
        const val REQUEST_CODE_RINGTONE_PICKER = 100
    }
}