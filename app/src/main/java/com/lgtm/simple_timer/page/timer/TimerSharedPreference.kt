package com.lgtm.simple_timer.page.timer

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lgtm.simple_timer.RuntimeTypeAdapterFactory
import com.lgtm.simple_timer.data.TimerData

class TimerSharedPreference(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, 0)

    private val gson = GsonBuilder().create()

    var prevTimerData: TimerData?
        get() {
            val jsonTimer = prefs.getString(KEY_PREV_TIMER, "")
            Log.d("Doran", "get $jsonTimer")
            return gson.fromJson(jsonTimer, TimerData::class.java)

        }
        set(value) {
            val jsonTimer = gson.toJson(value)
            Log.d("Doran", "put $jsonTimer")
            prefs.edit().putString(KEY_PREV_TIMER, jsonTimer).apply()
        }

    companion object {
        const val PREF_NAME = "TIMER_PREFERENCE"

        const val KEY_PREV_TIMER = "KEY_PREV_TIMER"
    }
}