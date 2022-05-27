package com.lgtm.simple_timer.data.source

import com.lgtm.simple_timer.data.TimerData

interface TimerDataSource {
    fun getStoredTimer(): TimerData?
    fun storeTimer(timerData: TimerData)
}