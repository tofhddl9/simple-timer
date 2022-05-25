package com.lgtm.simple_timer.page.timer.dialtimer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.lgtm.simple_timer.R
import com.lgtm.simple_timer.page.timer.DialTouchInfo
import kotlin.math.ceil

class CircleProgressBarTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleInt: Int = 0,
    private val progressBarConfig: ProgressBarConfig = DefaultProgressBarConfig()
) : View(context, attrs, defStyleInt), ProgressBarConfig by progressBarConfig {

    // var remainTime: Long = 0

    private var progressStep: Int = 0

    private val rect = RectF()

    private val rect2 = RectF()

    private var angle: Float = 0f
//        get() {
//            val gaugeTick = progressStep
//            return ceil(360f / progressBarConfig.maxProgressStep * gaugeTick)
//        }

    private val progressPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, progressBarConfig.progressBarColor)
        strokeWidth = progressBarConfig.progressBarWidth
    }

    private val backgroundProgressPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, progressBarConfig.backgroundProgressBarColor)
        strokeWidth = progressBarConfig.backgroundProgressBarWidth
    }

//    private val remainTimePaint = Paint().apply {
//        color = Color.LTGRAY
//        textAlign = Paint.Align.CENTER
//        textSize = 120f
//        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
//    }

    private var timerTouchListener: TimerTouchListener? = null
    fun setTimerTouchListener(listener: TimerTouchListener) {
        timerTouchListener = listener
    }

    fun updateTimer(angle: Float? = null, progressStep: Int? = null) {
        angle?.let {
            this.angle = it
        }

        progressStep?.let {
            if (it >= 0 && it <= progressBarConfig.maxProgressStep) {
                this.progressStep = it
            }
        }

        invalidate()
    }

    private var previousTouchedX: Float = 0f
    private var previousTouchedY: Float = 0f

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
        val dialTouchInfo = DialTouchInfo(width, height, touchedX, touchedY, dx, dy)
        timerTouchListener?.onDialTouched(dialTouchInfo)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupBound(w, h)
    }

    private fun setupBound(w: Int, h: Int) {
        val strokeWidth = progressBarConfig.backgroundProgressBarWidth * 1.5f
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
            //drawRemainTime()
        }
    }

    private fun Canvas.drawBackgroundProgressBar() {
        drawArc(rect, 0f, 360f, false, backgroundProgressPaint)
    }

    private fun Canvas.drawProgressBar() {
        drawArc(rect, -90f, angle, false, progressPaint)
    }

//    private fun Canvas.drawRemainTime() {
//        val xPos = width / 2f
//        val yPos = (height / 2f - (remainTimePaint.descent() + remainTimePaint.ascent()) / 2)
//
//        val text = remainTime.toTimerFormat()
//        drawText(text, xPos, yPos, remainTimePaint)
//    }

}

fun interface TimerTouchListener {
    fun onDialTouched(dialTouchInfo: DialTouchInfo)
}

//private fun Long.toTimerFormat(): String {
//    val remainSec = this / 1_000L
//    val minute = remainSec / 60
//    val sec = remainSec % 60
//
//    return "${minute.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}"
//}