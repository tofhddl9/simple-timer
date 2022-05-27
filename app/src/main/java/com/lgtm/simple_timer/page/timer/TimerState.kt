package com.lgtm.simple_timer.page.timer

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class TimerState {
    Init,
    Running,
    Paused,
    Finished
}