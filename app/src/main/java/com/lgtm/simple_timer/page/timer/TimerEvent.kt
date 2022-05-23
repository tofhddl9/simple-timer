package com.lgtm.simple_timer.page.timer

sealed class TimerEvent {
    class TouchDial(
        val dialTouchInfo: DialTouchInfo,
    ) : TimerEvent()
    object ClickStartOrPause : TimerEvent()
    object ClickReset : TimerEvent()
}

data class DialTouchInfo(
    val width: Int,
    val height: Int,
    val touchedX: Float,
    val touchedY: Float,
    val dx: Float,
    val dy: Float,
)