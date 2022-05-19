package com.lgtm.simple_timer.page.timer

sealed class TimerEvent {
    class DialChanged(
        val remainTime: Long,
    ) : TimerEvent()
    object ClickStartOrPause : TimerEvent()
    object ClickReset : TimerEvent()
}