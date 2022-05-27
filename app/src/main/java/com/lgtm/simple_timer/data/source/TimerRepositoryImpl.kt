package com.lgtm.simple_timer.data.source

import com.lgtm.simple_timer.page.timer.data.TimerUiState
import com.lgtm.simple_timer.page.timer.data.mapToTimerData
import com.lgtm.simple_timer.page.timer.data.mapToTimerUiState

class TimerRepositoryImpl(
    private val localTimerDataSource: TimerDataSource,
) : TimerRepository {

    override fun getStoredTimer() = localTimerDataSource.getStoredTimer()?.mapToTimerUiState()

    override fun storeTimer(timerUiState: TimerUiState) {
        localTimerDataSource.storeTimer(timerUiState.mapToTimerData())
    }

}