package com.lgtm.simple_timer.page.timer.data

import com.lgtm.simple_timer.page.timer.dialtimer.DefaultProgressBarConfig
import com.lgtm.simple_timer.page.timer.dialtimer.ProgressBarConfig

data class TimerUiState(
    val restartTime: Long = 0L,
    val remainTime: Long = 0L,
    val progress: Float = 0f,
    val maxProgress: Float = 100f,
    val angle: Float = 0f,
    val state: TimerState = TimerState.Init,
    val dialProgressConfiguration: ProgressBarConfig = DefaultProgressBarConfig(),
)
