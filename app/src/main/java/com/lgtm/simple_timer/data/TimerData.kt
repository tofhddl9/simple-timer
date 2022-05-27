package com.lgtm.simple_timer.data

import com.lgtm.simple_timer.page.timer.data.TimerState
import java.io.Serializable
import java.util.*

data class TimerData(
    val settingTime: Long,
    val remainTime: Long,
    val leaveTime: Long = Calendar.getInstance().timeInMillis,
    val angle: Float,
    val state: TimerState? = null,
) : Serializable
