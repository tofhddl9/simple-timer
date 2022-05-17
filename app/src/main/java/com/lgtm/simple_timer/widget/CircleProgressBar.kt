package com.lgtm.simple_timer.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.graphics.RectF
import android.util.Log


class CircleProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleInt: Int = 0,
    private val progressBarConfig: ProgressBarConfig = DefaultProgressBarConfig()
) : View(context, attrs, defStyleInt), ProgressBarConfig by progressBarConfig {

    private var dx: Float = 0f
    private var dy: Float = 0f

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    private val rect = RectF()

    private val touchDirectionCalculator = TouchDirectionCalculator()

    var mWidth = 0
    var mHeight = 0

    var gauge = 50

    private val progressPaint = Paint().apply {
        isAntiAlias = true
        color = progressBarConfig.progressBarColor
        style = Paint.Style.STROKE
        strokeWidth = progressBarConfig.progressBarWidth
    }

    private val backgroundProgressPaint = Paint().apply {
        isAntiAlias = true
        color = progressBarConfig.backgroundProgressBarColor
        style = Paint.Style.STROKE
        strokeWidth = progressBarConfig.backgroundProgressBarWidth
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                dx = event.x - previousX
                dy = event.y - previousY

                previousX = event.x
                previousY = event.y
            }
        }
        control(event.x, event.y, dx, dy)
        Log.d("Doran", "x : ${event.x} y : ${event.y} || width : ${mWidth}, Height : ${mHeight}")
        return true
    }

    private fun control(touchedX: Float, touchedY: Float, dx: Float, dy: Float) {
        val touchedQuadrantArea = getQuadrantArea(mWidth, mHeight, touchedX, touchedY)
        val dragDirection = touchDirectionCalculator.getDragDirection(dx, dy)

        // TODO. refactor
        when (touchedQuadrantArea) {
            QuadrantArea.First -> {
                if (touchDirectionCalculator.isDirectBottomAndRight(dragDirection)) {
                    increaseProgress()
                } else if (touchDirectionCalculator.isDirectTopAndLeft(dragDirection)) {
                    decreaseProgress()
                }
            }
            QuadrantArea.Second -> {
                if (touchDirectionCalculator.isDirectTopAndRight(dragDirection)) {
                    increaseProgress()
                } else if (touchDirectionCalculator.isDirectBottomAndLeft(dragDirection)) {
                    decreaseProgress()
                }
            }
            QuadrantArea.Third -> {
                if (touchDirectionCalculator.isDirectTopAndLeft(dragDirection)) {
                    increaseProgress()
                } else if (touchDirectionCalculator.isDirectBottomAndRight(dragDirection)) {
                    decreaseProgress()
                }
            }
            QuadrantArea.Fourth -> {
                if (touchDirectionCalculator.isDirectBottomAndLeft(dragDirection)) {
                    increaseProgress()
                } else if (touchDirectionCalculator.isDirectTopAndRight(dragDirection)) {
                    decreaseProgress()
                }
            }
        }

        Log.d("Doran", "${gauge}")
        invalidate()
    }

    private fun increaseProgress() {
        gauge++
    }

    private fun decreaseProgress() {
        gauge--
    }

    private fun getQuadrantArea(w: Int, h: Int, touchedX: Float, touchedY: Float): QuadrantArea {
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
        val newRect = RectF(
            paddingLeft.toFloat() + 30f,
            paddingTop.toFloat() + 30f ,
            (w - paddingRight).toFloat() - 30f,
            (h - paddingBottom).toFloat() - 30f
        )

        rect.set(newRect)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackgroundProgressBar(canvas)

        drawProgressBar(canvas)
    }

    private fun drawBackgroundProgressBar(canvas: Canvas) {
        canvas.drawArc(rect, 0f, 360f, false, backgroundProgressPaint)
    }

    private fun drawProgressBar(canvas: Canvas) {
        canvas.drawArc(rect, -90f, gauge.toFloat(), false, progressPaint)
    }

}

enum class QuadrantArea {
    First, Second, Third, Fourth
}

interface MotionEventCallback {
    fun onTouchStart()
    fun onTouchMove(dx: Float, dy: Float)
    fun onTouchRelease()
}
