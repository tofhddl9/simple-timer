package com.lgtm.simple_timer.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.graphics.RectF
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.roundToInt

// before start case
// ProgressBar 의 Dial 조절 : Progress에 따라 remainTime 변경 후 vm과 싱크

// after start case
// Vm의 Timer 값 변경 : remainTime에 따라 Progress 변경?

// 둘이 바라보는게 다르면 이상하지 않나... 싱크하기 어렵고.
// 서큘러 갱신이 생길 것 같다.

class CircleProgressBarTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleInt: Int = 0,
    private val progressBarConfig: ProgressBarConfig = DefaultProgressBarConfig()
) : View(context, attrs, defStyleInt), ProgressBarConfig by progressBarConfig {

    var remainTime: Long = 0
        set(value) {
            field = value
            // 여기서 서큘러 갱신 존재. 주석처리하면 타이머 시작시 프로그레스 갱신은 안됨 리팩토링 하자.
            // progressStep = progressBarConfig.timerTickInfo.getRemainStep(value).toFloat()
            invalidate()
        }

    private val rect = RectF()

    private var previousTouchedX: Float = 0f
    private var previousTouchedY: Float = 0f

    private val touchDirectionCalculator = TouchDirectionCalculator()

    private var progressStep: Float = 0.0f
        set(value) {
            if (value >= 0 && value <= progressBarConfig.maxProgressStep) {
                field = value
                adjustRemainTime(value.roundToInt())
            }
        }

    private val angle: Float
        get() {
            val gaugeTick = progressStep.roundToInt()
            return ceil(360f / progressBarConfig.maxProgressStep * gaugeTick)
        }

    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = progressBarConfig.progressBarColor
        strokeWidth = progressBarConfig.progressBarWidth
    }

    private val backgroundProgressPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = progressBarConfig.backgroundProgressBarColor
        strokeWidth = progressBarConfig.backgroundProgressBarWidth
    }

    private val remainTimePaint = Paint().apply {
        color = Color.LTGRAY
        textAlign = Paint.Align.CENTER
        textSize = 120f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private var timerTouchListener: TimerTouchListener? = null

    fun setTimerTouchListener(listener: TimerTouchListener) {
        timerTouchListener = listener
    }

    private fun adjustRemainTime(progressStep: Int) {
        val timerTickInfo = progressBarConfig.timerTickInfo
        Log.d("Doran", "step : ${progressStep}")
        remainTime = timerTickInfo.getRemainTime(progressStep)
        Log.d("Doran", "remainTime : ${remainTime}")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                previousTouchedX = event.x
                previousTouchedY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - previousTouchedX
                val dy = event.y - previousTouchedY
                previousTouchedX = event.x
                previousTouchedY = event.y

                onDialTouched(event.x, event.y, dx, dy)
            }
        }
        return true
    }

    private fun onDialTouched(touchedX: Float, touchedY: Float, dx: Float, dy: Float) {
        adjustProgress(touchedX, touchedY, dx, dy)
        timerTouchListener?.onDialChanged(remainTime)
        invalidate()
    }

    // 이것도 이벤트로 전달.
    private fun adjustProgress(touchedX: Float, touchedY: Float, dx: Float, dy: Float) {
        val touchedQuadrantArea = getQuadrantAreaOfTouch(width, height, touchedX, touchedY)
        val dragDirection = touchDirectionCalculator.getDragDirection(dx, dy)
        val dragLength = (dx.absoluteValue + dy.absoluteValue) * progressBarConfig.dialSensitivity

        // TODO. refactor
        when (touchedQuadrantArea) {
            QuadrantArea.First -> {
                if (touchDirectionCalculator.isDirectBottomAndRight(dragDirection)) {
                    increaseProgress(dragLength)
                } else if (touchDirectionCalculator.isDirectTopAndLeft(dragDirection)) {
                    decreaseProgress(dragLength)
                }
            }
            QuadrantArea.Second -> {
                if (touchDirectionCalculator.isDirectTopAndRight(dragDirection)) {
                    increaseProgress(dragLength)
                } else if (touchDirectionCalculator.isDirectBottomAndLeft(dragDirection)) {
                    decreaseProgress(dragLength)
                }
            }
            QuadrantArea.Third -> {
                if (touchDirectionCalculator.isDirectTopAndLeft(dragDirection)) {
                    increaseProgress(dragLength)
                } else if (touchDirectionCalculator.isDirectBottomAndRight(dragDirection)) {
                    decreaseProgress(dragLength)
                }
            }
            QuadrantArea.Fourth -> {
                if (touchDirectionCalculator.isDirectBottomAndLeft(dragDirection)) {
                    increaseProgress(dragLength)
                } else if (touchDirectionCalculator.isDirectTopAndRight(dragDirection)) {
                    decreaseProgress(dragLength)
                }
            }
        }
    }

    private fun increaseProgress(dragLength: Float) {
        progressStep += dragLength
    }

    private fun decreaseProgress(dragLength: Float) {
        progressStep -= dragLength
    }

    private fun getQuadrantAreaOfTouch(w: Int, h: Int, touchedX: Float, touchedY: Float): QuadrantArea {
        val centerX = w / 2
        val centerY = h / 2

        return if (touchedX < centerX) {
            if (touchedY < centerY) {
                QuadrantArea.Second
            } else {
                QuadrantArea.Third
            }
        } else {
            if (touchedY < centerY) {
                QuadrantArea.First
            } else {
                QuadrantArea.Fourth
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupBound(w, h)
    }

    private fun setupBound(w: Int, h: Int) {
        val strokeWidth = progressBarConfig.backgroundProgressBarWidth
        val newRect = RectF(
            paddingLeft.toFloat() + strokeWidth,
            paddingTop.toFloat() + strokeWidth,
            (w - paddingRight).toFloat() - strokeWidth,
            (h - paddingBottom).toFloat() - strokeWidth
        )

        rect.set(newRect)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.run {
            drawBackgroundProgressBar()
            drawProgressBar()
            drawRemainTime()
        }
    }

    private fun Canvas.drawBackgroundProgressBar() {
        drawArc(rect, 0f, 360f, false, backgroundProgressPaint)
    }

    private fun Canvas.drawProgressBar() {
        drawArc(rect, -90f, angle, false, progressPaint)
    }

    private fun Canvas.drawRemainTime() {
        val xPos = width / 2f
        val yPos = (height / 2f - (remainTimePaint.descent() + remainTimePaint.ascent()) / 2)

        val text = remainTime.toTimerFormat()
        Log.d("Doran", "remainTime in String: ${text}")
        drawText(text, xPos, yPos, remainTimePaint)
    }

}

enum class QuadrantArea {
    First, Second, Third, Fourth
}

data class TickInfo(
    val from: Int,
    val to: Int,
    val interval: Int
)

data class TimerTickInfo(
    val tickInfoList: List<TickInfo>
) {
    fun getTotalTimerTick(): Int {
        var total = 0
        tickInfoList.forEach {
            total += (it.to - it.from) / it.interval
        }
        return total
    }

    fun getRemainTime(progressStep: Int): Long {
        var remainTime = 0L
        var targetStep = progressStep
        tickInfoList.forEach {
            val totalStep = (it.to - it.from) / it.interval
            if (totalStep < targetStep) {
                targetStep -= totalStep
            } else {
                remainTime = (it.from + it.interval * targetStep).toLong()
                return remainTime.toMs()
            }
        }

        return remainTime.toMs()
    }

    fun getRemainStep(remainTime: Long): Int {
        var remainStep = 0
        var targetRemainTime = remainTime
        tickInfoList.forEach {
            val totalTime = it.to - it.from
            if (totalTime < targetRemainTime) {
                targetRemainTime -= totalTime
                remainStep += totalTime / it.interval
            } else {
                remainStep += ceil(1.0f * targetRemainTime / it.interval).toInt()
                return remainStep
            }
        }

        return remainStep
    }
}

fun interface TimerTouchListener {
    fun onDialChanged(remainTime: Long)
}

fun Long.toMs() = this * 1_000L

fun Long.toTimerFormat(): String {
    val remainSec = this / 1_000L
    val minute = remainSec / 60
    val sec = remainSec % 60

    return "${minute.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}"
}