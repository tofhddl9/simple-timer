package com.lgtm.simple_timer.page.timer.data

import com.lgtm.simple_timer.data.TimerData
import java.util.*
import kotlin.math.round

fun TimerData.mapToTimerUiState(): TimerUiState {
    val elapsedTimeInMillis = (round((Calendar.getInstance().timeInMillis - leaveTime).toFloat() / 1000) * 1000).toLong()
    val elapsedTimeInSec = elapsedTimeInMillis / 1000
    val anglePerTick = angle / (remainTime / 1000)
    val newAngle = angle - anglePerTick * elapsedTimeInSec

    if (state == TimerState.Running) {
        return if (elapsedTimeInMillis > remainTime) {
            TimerUiState(
                restartTime = settingTime,
                remainTime = 0,
                state = TimerState.Finished,
                angle = 0f
            )
        } else {
            TimerUiState(
                restartTime = settingTime,
                remainTime = remainTime - elapsedTimeInMillis,
                state = state,
                angle = newAngle,
            )
        }
    }

    return TimerUiState(
        restartTime = settingTime,
        remainTime = remainTime,
        state = state ?: TimerState.Init,
        angle = angle,
    )
}

fun TimerUiState.mapToTimerData(): TimerData {
    return TimerData(
        settingTime = restartTime,
        remainTime = (round(remainTime.toFloat() / 1000) * 1000).toLong(),
        state = state,
        angle = angle
    )
}