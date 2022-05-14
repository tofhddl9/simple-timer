package com.lgtm.simple_timer.page.timer

sealed class TimerEvent {
    object TouchStart : TimerEvent()
    object TouchMove : TimerEvent()
    object TouchRelease : TimerEvent()
    object ClickTimer : TimerEvent()
}