package com.lgtm.simple_timer.page.timer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lgtm.simple_timer.page.timer.dialtimer.CircleDialProgressCalculator
import com.lgtm.simple_timer.page.timer.dialtimer.ProgressBarConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.ceil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timer: Timer,
    private val progressTimerConfig: ProgressBarConfig,
    private val circleDialProgressCalculator: CircleDialProgressCalculator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState(dialProgressConfiguration = progressTimerConfig))
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    init {
        initTimer()
    }

    private fun initTimer() {
        timer.scope = viewModelScope

        viewModelScope.launch {
            timer.statusFlow.collect { timerState ->
                _uiState.value = _uiState.value.copy(state = timerState)
                when(timerState) {
                    is TimerState.Init -> {

                    }
                    is TimerState.Paused -> {

                    }
                    is TimerState.Running -> {

                    }
                    is TimerState.Finished -> {
                        // 알람 울리기
                        // 리셋 버튼 보여주기
                    }
                }
            }
        }
    }

    fun onEvent(event: TimerEvent) {
        when (event) {
            is TimerEvent.TouchDial -> {
                onDialTouched(event.dialTouchInfo)
            }
            is TimerEvent.ClickStartOrPause -> {
                onClickTimerToggleButton()
            }
            is TimerEvent.ClickRestart -> {
                onClickTimerRestartButton()
            }
        }
    }

    private fun onDialTouched(dialTouchInfo: DialTouchInfo) {
        if (_uiState.value.state == TimerState.Running) {
            return
        }

        val progressChange = circleDialProgressCalculator.calculateProgressStepByTouch(dialTouchInfo)
        val updatedProgress = (_uiState.value.progress + progressChange)
            .coerceAtLeast(0f)
            .coerceAtMost(progressTimerConfig.dialTickInfo.getTotalTimerTick().toFloat())

        val timeChange = circleDialProgressCalculator.calculateRemainTimeDiff(updatedProgress)
        val updatedRemainTime = timeChange
            .coerceAtLeast(0L)
            .coerceAtMost(progressTimerConfig.dialTickInfo.getMaximumTime())

        val angle = ceil(360f / progressTimerConfig.maxProgressStep * updatedProgress.toInt())

        _uiState.value = _uiState.value.copy(
            remainTime = updatedRemainTime,
            progress = updatedProgress,
            angle = angle,
        )

        timer.configure(startTime = _uiState.value.remainTime)
        Log.d("Doran7","uiState : ${_uiState.value}")
    }

    private fun onClickTimerToggleButton() {
        if (_uiState.value.state == TimerState.Running) {
            pauseTimer()
        } else {
            _uiState.value = _uiState.value.copy(
                restartTime = _uiState.value.remainTime
            )
            Log.d("Doran6","uiState : ${_uiState.value}")

            startTimer()
        }
    }

    private fun onClickTimerRestartButton() {
        val initialTime = _uiState.value.restartTime
        val initProgress = circleDialProgressCalculator.calculateRemainStepOfRemainTime(initialTime).toFloat()
        val initAngle = ceil(360f / progressTimerConfig.maxProgressStep * initProgress.toInt())

        _uiState.value = _uiState.value.copy(
            remainTime = initialTime,
            progress = initProgress,
            angle = initAngle
        )
        Log.d("Doran5","uiState : ${_uiState.value}")
        timer.configure(startTime = initialTime)

        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            timer.remainTimeFlow()
                .onEach { onTick(it) }
                .collect()
        }

        viewModelScope.launch {
            timer.start()
        }
    }

    private fun pauseTimer() {
        viewModelScope.launch {
            timer.pause()
        }
    }

    private fun onTick(remainTime: Long) {
        val prevAngle = _uiState.value.angle
        val prevRemainTime = _uiState.value.remainTime / 1_000

        _uiState.value = _uiState.value.copy(
            remainTime = remainTime,
            progress = circleDialProgressCalculator.calculateRemainStepOfRemainTime(remainTime).toFloat(),
            angle = prevAngle - prevAngle / prevRemainTime
        )

        Log.d("Doran4","uiState : ${_uiState.value}")
    }

}