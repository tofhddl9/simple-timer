package com.lgtm.simple_timer.page.timer

data class TimerUiState(
    val settingTime: Long = 600L,
    val remainTime: Long = 600L,
    val state: TimerState = TimerState.Init,
)
