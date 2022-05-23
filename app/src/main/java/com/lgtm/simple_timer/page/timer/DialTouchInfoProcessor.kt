package com.lgtm.simple_timer.page.timer

import com.lgtm.simple_timer.widget.DialTickInfo
import com.lgtm.simple_timer.widget.QuadrantArea
import kotlin.math.absoluteValue

// dialTouchInfo 를 받아서 tickInfo를 바탕으로 다음을 계산
// 1. 변화의 방향(증가 또는 감소)
// 2. 얼만큼 변화시킬지
// 3. progress 로 전달?
class DialTouchInfoProcessor(
    private val dialTickInfo: DialTickInfo,
    // private val dialConfiguration:
    private val touchDirectionCalculator: TouchDirectionCalculator = TouchDirectionCalculator(),
) {

    fun process(dialTouchInfo: DialTouchInfo): Float {
        val touchedQuadrantArea = getQuadrantAreaOfTouch(
            dialTouchInfo.width,
            dialTouchInfo.height,
            dialTouchInfo.touchedX,
            dialTouchInfo.touchedY
        )
        val dragDirection = touchDirectionCalculator.getDragDirection(
            dialTouchInfo.dx,
            dialTouchInfo.dy
        )
        val dragLength = (dialTouchInfo.dx.absoluteValue + dialTouchInfo.dy.absoluteValue) * 0.04f //progressBarConfig.dialSensitivity

        when (touchedQuadrantArea) {
            QuadrantArea.First -> {
                if (touchDirectionCalculator.isDirectBottomAndRight(dragDirection)) {
                    return dragLength
                } else if (touchDirectionCalculator.isDirectTopAndLeft(dragDirection)) {
                    return -dragLength
                }
            }
            QuadrantArea.Second -> {
                if (touchDirectionCalculator.isDirectTopAndRight(dragDirection)) {
                    return dragLength
                } else if (touchDirectionCalculator.isDirectBottomAndLeft(dragDirection)) {
                    return -dragLength
                }
            }
            QuadrantArea.Third -> {
                if (touchDirectionCalculator.isDirectTopAndLeft(dragDirection)) {
                    return dragLength
                } else if (touchDirectionCalculator.isDirectBottomAndRight(dragDirection)) {
                    return -dragLength
                }
            }
            QuadrantArea.Fourth -> {
                if (touchDirectionCalculator.isDirectBottomAndLeft(dragDirection)) {
                    return dragLength
                } else if (touchDirectionCalculator.isDirectTopAndRight(dragDirection)) {
                    return -dragLength
                }
            }
        }

        return dragLength
    }
//
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

}

class TouchDirectionCalculator {

    fun getDragDirection(dx: Float, dy: Float): Int {
        var direction = None
        direction = if (dx > 0) {
            direction or Right
        } else {
            direction or Left
        }
        direction = if (dy > 0) {
            direction or Bottom
        } else {
            direction or Top
        }

        return direction
    }

    fun isDirectTopAndRight(direction: Int): Boolean {
        return (direction and Top) > 0 && (direction and Right) > 0
    }

    fun isDirectTopAndLeft(direction: Int): Boolean {
        return (direction and Top) > 0 && (direction and Left) > 0
    }

    fun isDirectBottomAndRight(direction: Int): Boolean {
        return (direction and Bottom) > 0 && (direction and Right) > 0
    }

    fun isDirectBottomAndLeft(direction: Int): Boolean {
        return (direction and Bottom) > 0 && (direction and Left) > 0
    }

    companion object {
        const val None = 0
        const val Top = 1 shl 1
        const val Left = 1 shl 2
        const val Bottom = 1 shl 3
        const val Right = 1 shl 4
    }
}