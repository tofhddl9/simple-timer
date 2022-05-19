package com.lgtm.simple_timer.widget

import android.animation.ValueAnimator
import com.lgtm.simple_timer.R

interface ProgressBarConfig {
    val maxProgressStep: Int
    val dialSensitivity: Float
    val progressAnimator: ValueAnimator?
    val timerTickInfo: TimerTickInfo

    val progressBarWidth: Float
    val progressBarColor: Int

    val backgroundProgressBarWidth: Float
    val backgroundProgressBarColor: Int
}

class DefaultProgressBarConfig : ProgressBarConfig {
    override val maxProgressStep: Int by lazy { timerTickInfo.getTotalTimerTick() }
    override val dialSensitivity: Float = 0.03f
    override val progressAnimator: ValueAnimator?
        get() = null
    override val timerTickInfo: TimerTickInfo
        get() = TimerTickInfo(listOf(
            TickInfo(0, 60, 5),
            TickInfo(60, 300, 10),
            TickInfo(300, 900, 30),
            TickInfo(900, 1800, 60),
            TickInfo(1800, 2400, 150),
            TickInfo(2400, 3600, 300),
        ))
    override val progressBarWidth: Float
        get() = 60f
    override val progressBarColor: Int
        get() = R.color.black
    override val backgroundProgressBarWidth: Float
        get() = 100f
    override val backgroundProgressBarColor: Int
        get() = R.color.purple_700
}