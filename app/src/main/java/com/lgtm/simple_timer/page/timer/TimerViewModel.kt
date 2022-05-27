package com.lgtm.simple_timer.page.timer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lgtm.simple_timer.R
import com.lgtm.simple_timer.data.UiText
import com.lgtm.simple_timer.data.source.TimerRepository
import com.lgtm.simple_timer.page.timer.dialtimer.CircleDialProgressCalculator
import com.lgtm.simple_timer.page.timer.dialtimer.ProgressBarConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.ceil
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timer: Timer,
    private val progressTimerConfig: ProgressBarConfig,
    private val circleDialProgressCalculator: CircleDialProgressCalculator,
    private val timerRepository: TimerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState(dialProgressConfiguration = progressTimerConfig))
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val errorMessageChannel = Channel<UiText>()
    val errorMessageFlow = errorMessageChannel.receiveAsFlow()

    init {
        initTimer()
    }

    private fun initTimer() {
        timer.scope = viewModelScope

        viewModelScope.launch {
            timer.statusFlow.collect { timerState ->
                _uiState.value = _uiState.value.copy(state = timerState)
            }
        }

        timerRepository.getStoredTimer()?.let { prevTimer ->
            _uiState.value = _uiState.value.copy(
                restartTime = prevTimer.restartTime,
                remainTime = prevTimer.remainTime,
                state = prevTimer.state,
                angle = prevTimer.angle
            )
            timer.configure(startTime = prevTimer.remainTime)

            if (prevTimer.state == TimerState.Running) {
                startTimer()
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
    }

    private fun onClickTimerToggleButton() {
        if (_uiState.value.remainTime == 0L) {
            viewModelScope.launch {
                errorMessageChannel.send(
                    UiText.StringResource(
                        resId = R.string.error_message_no_remain_time,
                        0
                    )
                )
            }
            return
        }

        if (_uiState.value.state == TimerState.Running) {
            pauseTimer()
        } else {
            _uiState.value = _uiState.value.copy(
                restartTime = _uiState.value.remainTime
            )

            startTimer()
        }
    }

    private fun onClickTimerRestartButton() {
        viewModelScope.launch {
            timer.reset()
        }

        val initialTime = _uiState.value.restartTime
        val initProgress = circleDialProgressCalculator.calculateRemainStepOfRemainTime(initialTime).toFloat()
        val initAngle = ceil(360f / progressTimerConfig.maxProgressStep * initProgress.toInt())
        timer.configure(startTime = initialTime)

        _uiState.value = _uiState.value.copy(
            remainTime = initialTime,
            progress = initProgress,
            angle = initAngle,
        )

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

    override fun onCleared() {
        timerRepository.storeTimer(_uiState.value)

        super.onCleared()
    }
}