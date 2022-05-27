package com.lgtm.simple_timer.page.timer

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.lgtm.simple_timer.data.TimerData

class TimerSharedPreference(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, 0)

    private val gson = GsonBuilder().create()

    var prevTimerData: TimerData?
        get() {
            val jsonTimer = prefs.getString(KEY_PREV_TIMER, "")
            return gson.fromJson(jsonTimer, TimerData::class.java)

        }
        set(value) {
            val jsonTimer = gson.toJson(value)
            prefs.edit().putString(KEY_PREV_TIMER, jsonTimer).apply()
        }

    companion object {
        const val PREF_NAME = "TIMER_PREFERENCE"

        const val KEY_PREV_TIMER = "KEY_PREV_TIMER"
    }
}