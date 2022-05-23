package com.lgtm.simple_timer.page.timer

import com.lgtm.simple_timer.widget.ProgressBarConfig

class DialProgressCalculator(
    private val dialTouchInfoProcessor: DialTouchInfoProcessor
) {

    fun calculateProgressByTouch(dialTouchInfo: DialTouchInfo): Float {
        return dialTouchInfoProcessor.process(dialTouchInfo)
    }
}