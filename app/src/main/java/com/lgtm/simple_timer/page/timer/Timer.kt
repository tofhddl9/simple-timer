package com.lgtm.simple_timer.page.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Timer(
    private val startTime: Long = 30_000,
    private val period: Long = 1_000,
    private val scope: CoroutineScope = CoroutineScope(Job()),
) {

    private val operationChannel = Channel<Operation>(Channel.UNLIMITED)

    private var job: Job? = null

    private var remainTime = startTime

    // 이렇게 하거나 onStart, onPause 등을 제공하던가
    private val _statusFlow = MutableStateFlow<TimerState>(TimerState.Init)
    val statusFlow = _statusFlow.asStateFlow()

    fun remainTimeFlow() = flow {
        operationChannel.consumeAsFlow().collect {
            when (it) {
                Operation.START -> {
                    job?.cancel()
                    job = scope.launch { startTimer(period) }
                    _statusFlow.value = TimerState.Running
                }
                Operation.PAUSE -> {
                    job?.cancel()
                    _statusFlow.value = TimerState.Paused
                }
                Operation.RESET -> {
                    job?.cancel()
                    remainTime = startTime
                    _statusFlow.value = TimerState.Init
                }
                Operation.FINISH -> {
                    job?.cancel()
                    remainTime = startTime
                    _statusFlow.value = TimerState.Finished
                }
                Operation.EMIT -> {
                    emit(remainTime)
                }
            }
        }
    }

    private suspend fun startTimer(delay: Long) = withContext(Dispatchers.IO) {
        while (remainTime > 0) {
            delay(delay)
            remainTime -= delay
            operationChannel.send(Operation.EMIT)
        }
        operationChannel.send(Operation.FINISH)
    }

    suspend fun start() {
        operationChannel.send(Operation.START)
    }

    suspend fun pause() {
        operationChannel.send(Operation.PAUSE)
    }

    suspend fun reset() {
        operationChannel.send(Operation.RESET)
    }

}

private enum class Operation {
    START,
    PAUSE,
    RESET,
    EMIT,
    FINISH
}