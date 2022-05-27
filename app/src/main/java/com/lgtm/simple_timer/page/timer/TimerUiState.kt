package com.lgtm.simple_timer.page.timer

import com.lgtm.simple_timer.page.timer.dialtimer.DefaultProgressBarConfig
import com.lgtm.simple_timer.page.timer.dialtimer.ProgressBarConfig

data class TimerUiState(
    val restartTime: Long = 10000L,
    val remainTime: Long = 10000L,
    val progress: Float = 2f,
    val maxProgress: Float = 100f,
    val angle: Float = 20f,
    val state: TimerState = TimerState.Init,
    val dialProgressConfiguration: ProgressBarConfig = DefaultProgressBarConfig(),
)
