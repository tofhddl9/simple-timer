package com.lgtm.simple_timer.page.timer.dialtimer

class CircleDialBoard(
    private val width: Int,
    private val height: Int,
) {

    private val quadrants = listOf(
        QuadrantArea(QuadrantArea.Type.First, intArrayOf(width / 2, width), intArrayOf(0, height / 2)),
        QuadrantArea(QuadrantArea.Type.Second, intArrayOf(0, width / 2), intArrayOf(0, height / 2)),
        QuadrantArea(QuadrantArea.Type.Third, intArrayOf(0, width / 2), intArrayOf(height / 2, height)),
        QuadrantArea(QuadrantArea.Type.Fourth, intArrayOf(width / 2, width), intArrayOf(height / 2, height)),
    )

    fun getQuadrantArea(x: Float, y: Float): QuadrantArea? {
        // return quadrants.filter { it.isIn(x, y) }.getOrNull(0)
        val centerX = width / 2
        val centerY = height / 2
        if (centerX < x && centerY > y) {
            return quadrants[0]
        } else if (centerX > x && centerY > y) {
            return quadrants[1]
        } else if (centerX > x && centerY < y) {
            return quadrants[2]
        } else if (centerX < x && centerY < y) {
            return quadrants[3]
        } else {
            return null
        }
    }



}

class QuadrantArea(
    val type: Type,
    private val xPoints: IntArray,
    private val yPoints: IntArray,
) {

    fun isIn(x: Float, y: Float): Boolean {
        return xPoints[0] <= x && x <= xPoints[1] && yPoints[0] <= y && y <= yPoints[1]
    }

    enum class Type {
        First, Second, Third, Fourth
    }

}