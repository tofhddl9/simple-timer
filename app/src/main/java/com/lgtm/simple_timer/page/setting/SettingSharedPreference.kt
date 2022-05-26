package com.lgtm.simple_timer.page.setting

import android.content.Context
import android.content.SharedPreferences

class SettingSharedPreference(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, 0)

    /**
     * Alarm Setting
     * */
    var doesRing: Boolean
        get() = prefs.getBoolean(KEY_PREF_RINGTONE_ON_OFF, true)
        set(value) = prefs.edit().putBoolean(KEY_PREF_RINGTONE_ON_OFF, value).apply()

    var ringtone: String?
        get() = prefs.getString(KEY_PREF_RINGTONE, null)
        set(value) = prefs.edit().putString(KEY_PREF_RINGTONE, value).apply()

    var doesVibrate: Boolean
        get() = prefs.getBoolean(KEY_PREF_VIBRATION_ON_OFF, true)
        set(value) = prefs.edit().putBoolean(KEY_PREF_VIBRATION_ON_OFF, value).apply()

    var doesRingInfinitely: Boolean
        get() = prefs.getBoolean(KEY_PREF_INFINITE_RINGTONE, false)
        set(value) = prefs.edit().putBoolean(KEY_PREF_INFINITE_RINGTONE, value).apply()

    /**
     * Energy Setting
     * */
    var doesAlwaysOnDisplay: Boolean
        get() = prefs.getBoolean(KEY_PREF_ALWAYS_ON_DISPLAY, false)
        set(value) = prefs.edit().putBoolean(KEY_PREF_ALWAYS_ON_DISPLAY, value).apply()


    companion object {
        const val PREF_NAME = "SETTING_PREFERENCE"

        // Alarm
        const val KEY_PREF_RINGTONE_ON_OFF = "KEY_PREF_SOUND_ON_OFF"
        const val KEY_PREF_VIBRATION_ON_OFF = "KEY_PREF_VIBRATION_ON_OFF"
        const val KEY_PREF_RINGTONE = "KEY_PREF_RINGTONE"
        const val KEY_PREF_INFINITE_RINGTONE = "KEY_PREF_INFINITE_RINGTONE"

        // Energy
        const val KEY_PREF_ALWAYS_ON_DISPLAY = "KEY_PREF_ALWAYS_DISPLAY"

    }
}