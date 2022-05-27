package com.lgtm.simple_timer.data.source

import com.lgtm.simple_timer.page.timer.TimerUiState

interface TimerRepository {
    fun getStoredTimer(): TimerUiState?
    fun storeTimer(timerUiState: TimerUiState)
}