package com.lgtm.simple_timer.page.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Timer(
    private var startTime: Long = 0L,
    private var period: Long = 1_000,
    var scope: CoroutineScope = CoroutineScope(Job()),
) {

    private val operationChannel = Channel<Operation>(Channel.UNLIMITED)

    private var job: Job? = null

    private var _remainTime = startTime
    val remainTime = _remainTime

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
                    _remainTime = startTime
                    _statusFlow.value = TimerState.Init
                }
                Operation.FINISH -> {
                    job?.cancel()
                    _remainTime = startTime
                    _statusFlow.value = TimerState.Finished
                }
                Operation.EMIT -> {
                    emit(_remainTime)
                }
            }
        }
    }

    private suspend fun startTimer(delay: Long) = withContext(Dispatchers.IO) {
        while (_remainTime > 0) {
            delay(delay)
            _remainTime -= delay
            operationChannel.send(Operation.EMIT)
        }
        _remainTime = _remainTime.coerceAtLeast(0)
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

    fun configure(startTime: Long? = null, period: Long? = null) {
        if (_statusFlow.value != TimerState.Running) {
            startTime?.let {
                this.startTime = it
                this._remainTime = it
            }
            period?.let {
                this.period = it
            }
        }
    }
}

private enum class Operation {
    START,
    PAUSE,
    RESET,
    EMIT,
    FINISH
}