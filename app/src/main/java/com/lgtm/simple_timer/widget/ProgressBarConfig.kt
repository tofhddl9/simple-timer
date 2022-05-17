package com.lgtm.simple_timer.widget

import android.animation.ValueAnimator
import com.lgtm.simple_timer.R

interface ProgressBarConfig {
    val maxProgress: Float
    val progressAnimator: ValueAnimator?

    val progressBarWidth: Float
    val progressBarColor: Int

    val backgroundProgressBarWidth: Float
    val backgroundProgressBarColor: Int
    // val progressDirection: Boolean
}

class DefaultProgressBarConfig : ProgressBarConfig {
    override val maxProgress: Float
        get() = 100f
    override val progressAnimator: ValueAnimator?
        get() = null
    override val progressBarWidth: Float
        get() = 10f
    override val progressBarColor: Int
        get() = R.color.black
    override val backgroundProgressBarWidth: Float
        get() = 20f
    override val backgroundProgressBarColor: Int
        get() = R.color.purple_700
}