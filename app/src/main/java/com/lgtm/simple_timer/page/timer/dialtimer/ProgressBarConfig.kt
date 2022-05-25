package com.lgtm.simple_timer.page.timer.dialtimer

import android.animation.ValueAnimator
import com.lgtm.simple_timer.R
import com.lgtm.simple_timer.utils.toMs

interface ProgressBarConfig {
    val maxProgressStep: Int
    val dialSensitivity: Float
    val progressAnimator: ValueAnimator?
    val dialTickInfo: DialTickInfo

    val progressBarWidth: Float
    val progressBarColor: Int

    val backgroundProgressBarWidth: Float
    val backgroundProgressBarColor: Int
}

class DefaultProgressBarConfig : ProgressBarConfig {
    override val maxProgressStep: Int by lazy { dialTickInfo.getTotalTimerTick() }
    override val dialSensitivity: Float = 0.03f
    override val progressAnimator: ValueAnimator?
        get() = null
    override val dialTickInfo: DialTickInfo
        get() = DialTickInfo(listOf(
            DialTickInfo.TickInfo(0, 60, 5),
            DialTickInfo.TickInfo(60, 300, 10),
            DialTickInfo.TickInfo(300, 900, 30),
            DialTickInfo.TickInfo(900, 1800, 60),
            DialTickInfo.TickInfo(1800, 2400, 150),
            DialTickInfo.TickInfo(2400, 3600, 300),
        ))
    override val progressBarWidth: Float
        get() = 100f
    override val progressBarColor: Int
        get() = R.color.red
    override val backgroundProgressBarWidth: Float
        get() = 100f
    override val backgroundProgressBarColor: Int
        get() = R.color.black
}

data class DialTickInfo(
    val tickInfoList: List<TickInfo>
) {

    data class TickInfo(
        val from: Long,
        val to: Long,
        val interval: Long
    )

    fun getTotalTimerTick(): Int {
        var total = 0
        tickInfoList.forEach {
            total += ((it.to - it.from) / it.interval).toInt()
        }

        return total
    }

    fun getMaximumTime(): Long = tickInfoList.last().to.toMs()
}