package com.lgtm.simple_timer.widget

//class TouchDirectionCalculator {
//
//    fun getDragDirection(dx: Float, dy: Float): Int {
//        var direction = None
//        direction = if (dx > 0) {
//            direction or Right
//        } else {
//            direction or Left
//        }
//        direction = if (dy > 0) {
//            direction or Bottom
//        } else {
//            direction or Top
//        }
//
//        return direction
//    }
//
//    fun isDirectTopAndRight(direction: Int): Boolean {
//        return (direction and Top) > 0 && (direction and Right) > 0
//    }
//
//    fun isDirectTopAndLeft(direction: Int): Boolean {
//        return (direction and Top) > 0 && (direction and Left) > 0
//    }
//
//    fun isDirectBottomAndRight(direction: Int): Boolean {
//        return (direction and Bottom) > 0 && (direction and Right) > 0
//    }
//
//    fun isDirectBottomAndLeft(direction: Int): Boolean {
//        return (direction and Bottom) > 0 && (direction and Left) > 0
//    }
//
//    companion object {
//        const val None = 0
//        const val Top = 1 shl 1
//        const val Left = 1 shl 2
//        const val Bottom = 1 shl 3
//        const val Right = 1 shl 4
//    }
//}