package com.lgtm.simple_timer.page.timer

sealed class TimerState {
    object Init : TimerState()
    object Running : TimerState()
    object Paused : TimerState()
    object Finished : TimerState()
}
