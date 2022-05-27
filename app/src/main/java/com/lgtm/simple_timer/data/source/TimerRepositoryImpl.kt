package com.lgtm.simple_timer.data.source

import com.lgtm.simple_timer.page.timer.TimerUiState
import com.lgtm.simple_timer.page.timer.mapToTimerData
import com.lgtm.simple_timer.page.timer.mapToTimerUiState

class TimerRepositoryImpl(
    private val localTimerDataSource: TimerDataSource,
) : TimerRepository {

    override fun getStoredTimer() = localTimerDataSource.getStoredTimer()?.mapToTimerUiState()

    override fun storeTimer(timerUiState: TimerUiState) {
        localTimerDataSource.storeTimer(timerUiState.mapToTimerData())
    }

}