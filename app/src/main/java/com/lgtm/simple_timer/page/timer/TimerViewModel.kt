package com.lgtm.simple_timer.page.timer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        initTimer()
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
            is TimerEvent.DialChanged -> {
                timer.configure(startTime = event.remainTime)
                _uiState.value = _uiState.value.copy(remainTime = event.remainTime)
            }
            is TimerEvent.ClickStartOrPause -> {
                onClickTimerToggleButton()
            }
            is TimerEvent.ClickReset -> {

            }
        }
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