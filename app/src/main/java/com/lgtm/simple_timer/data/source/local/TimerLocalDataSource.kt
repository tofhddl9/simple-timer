package com.lgtm.simple_timer.data.source.local

import com.lgtm.simple_timer.data.TimerData
import com.lgtm.simple_timer.data.source.TimerDataSource
import com.lgtm.simple_timer.page.timer.TimerSharedPreference

class TimerLocalDataSource(
    private val timerSharedPref: TimerSharedPreference,
) : TimerDataSource {

    override fun getStoredTimer() = timerSharedPref.prevTimerData

    override fun storeTimer(timerData: TimerData) {
        timerSharedPref.prevTimerData = timerData
    }

}
