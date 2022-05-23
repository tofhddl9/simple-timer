package com.lgtm.simple_timer.page.timer

data class TimerUiState(
    val settingTime: Long = 60_000L,
    val remainTime: Long = 60_000L,
    val progress: Int = 0,
    val maxProgress: Int = 100,
    val state: TimerState = TimerState.Init,
)
