package com.lgtm.simple_timer.page.timer.dialtimer

import com.lgtm.simple_timer.page.timer.DialTouchInfo
import com.lgtm.simple_timer.utils.toMs
import kotlin.math.absoluteValue
import kotlin.math.ceil

class CircleDialProgressCalculator(
    private val dialTickInfo: DialTickInfo,
    private val touchDirectionCalculator: TouchDirectionCalculator = TouchDirectionCalculator(),
) {

    // How to refactor
    fun calculateProgressStepByTouch(dialTouchInfo: DialTouchInfo): Float {
        val dialBoard = CircleDialBoard(dialTouchInfo.width, dialTouchInfo.height)

        val touchedQuadrantArea = dialBoard.getQuadrantArea(dialTouchInfo.touchedX, dialTouchInfo.touchedY)
        val dragDirection = touchDirectionCalculator.getDragDirection(dialTouchInfo.dx, dialTouchInfo.dy)
        val dragLength = (dialTouchInfo.dx.absoluteValue + dialTouchInfo.dy.absoluteValue) * 0.03f // TODO

        when (touchedQuadrantArea?.type) {
            QuadrantArea.Type.First -> {
                if (touchDirectionCalculator.isDirectBottomAndRight(dragDirection)) return dragLength
                else if (touchDirectionCalculator.isDirectTopAndLeft(dragDirection)) return -dragLength
            }
            QuadrantArea.Type.Second -> {
                if (touchDirectionCalculator.isDirectTopAndRight(dragDirection)) return dragLength
                else if (touchDirectionCalculator.isDirectBottomAndLeft(dragDirection)) return -dragLength
            }
            QuadrantArea.Type.Third -> {
                if (touchDirectionCalculator.isDirectTopAndLeft(dragDirection)) return dragLength
                else if (touchDirectionCalculator.isDirectBottomAndRight(dragDirection)) return -dragLength
            }
            QuadrantArea.Type.Fourth -> {
                if (touchDirectionCalculator.isDirectBottomAndLeft(dragDirection)) return dragLength
                else if (touchDirectionCalculator.isDirectTopAndRight(dragDirection)) return -dragLength
            }
        }

        return 0f
    }

    fun calculateRemainTimeDiff(progressStep: Float): Long {
        var remainTime = 0L
        var targetStep = progressStep.toInt()
        dialTickInfo.tickInfoList.forEach {
            val totalStep = ((it.to - it.from) / it.interval).toInt()
            if (totalStep < targetStep) {
                targetStep -= totalStep
            } else {
                remainTime = (it.from + it.interval * targetStep)
                return remainTime.toMs()
            }
        }

        return remainTime.toMs()
    }

    fun calculateRemainStepOfRemainTime(remainTime: Long): Int {
        var remainStep = 0
        var targetRemainTime = remainTime / 1_000
        dialTickInfo.tickInfoList.forEach {
            val totalTime = it.to - it.from
            if (totalTime < targetRemainTime) {
                targetRemainTime -= totalTime
                remainStep += (totalTime / it.interval).toInt()
            } else {
                remainStep += ceil(1.0f * targetRemainTime / it.interval).toInt()
                return remainStep
            }
        }

        return remainStep
    }

}
