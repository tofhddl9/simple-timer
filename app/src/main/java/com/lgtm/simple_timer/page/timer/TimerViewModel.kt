package com.lgtm.simple_timer.page.timer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lgtm.simple_timer.widget.DefaultProgressBarConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class TimerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    lateinit var timer : Timer
    lateinit var dialProgressCalculator: DialProgressCalculator

    init {
        initTimer()
        initDialProgressCalculator()
    }

    private fun initDialProgressCalculator() {
        val config = DefaultProgressBarConfig()
        val calculator = TouchDirectionCalculator()
        val dialTouchInfoProcessor = DialTouchInfoProcessor(config.dialTickInfo, calculator)

        dialProgressCalculator = DialProgressCalculator(dialTouchInfoProcessor)
    }

    private fun initTimer() {
        timer = Timer(startTime = 10_000L, period = 1_000L, scope = viewModelScope)

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

                    }
                }
            }
        }
    }

    fun onEvent(event: TimerEvent) {
        when (event) {
            is TimerEvent.TouchDial -> {
                // timer.configure(startTime = event.dialTouchInfo)
                //_uiState.value = _uiState.value.copy(remainTime = event.dialTouchInfo)
                // 터치 이벤트 계산해서 progress, remainTime을 계산
                onDialTouched(event.dialTouchInfo)
            }
            is TimerEvent.ClickStartOrPause -> {
                onClickTimerToggleButton()
            }
            is TimerEvent.ClickReset -> {

            }
        }
    }

    // remainTime 데이터의 소스는 타이머로부터 갖고오는게 가장 좋을 것 같다.
    private fun onDialTouched(dialTouchInfo: DialTouchInfo) {
        // 계산기는 터치된 정보를 바탕으로, 어느방향으로 얼만큼 이동해야 하는지를 계산하고 리턴.

        val progressChange = dialProgressCalculator.calculateProgressByTouch(dialTouchInfo)
        _uiState.value = _uiState.value.copy(
            remainTime = timer.remainTime,
            progress = _uiState.value.progress + progressChange.toInt()
        )
    }

    private fun onClickTimerToggleButton() {
        if (_uiState.value.state == TimerState.Running) {
            viewModelScope.launch {
                timer.pause()
            }
        }
        else {
            viewModelScope.launch {
                timer.remainTimeFlow()
                    .onEach {
                        Log.d("Doran", "remainTimeFlow : $it")
                        _uiState.value = _uiState.value.copy(remainTime = it)
                    }
                    .collect()
            }
            viewModelScope.launch {
                timer.start()
            }
        }
    }

}