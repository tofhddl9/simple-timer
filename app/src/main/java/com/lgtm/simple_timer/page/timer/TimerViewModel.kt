package com.lgtm.simple_timer.page.timer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class TimerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    // n ms 마다 틱을 알려주는 타이머를 갖는게 좋겠다.
    fun onEvent(event: TimerEvent) {
        when (event) {
            TimerEvent.TouchStart -> {

            }
            TimerEvent.TouchMove -> {

            }
            TimerEvent.TouchRelease -> {

            }
            TimerEvent.ClickTimer -> {
                // 상태에 따라서 다른 동작
            }
        }
    }

}