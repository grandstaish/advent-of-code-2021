package day17

import java.io.File

fun main() {
    val target = parse(File("src/day17/input.txt"))

    val possibleVelocities = mutableSetOf<Pair<Int, Int>>()

    val maxXVel = target.xRange.last
    val maxYVel = -target.yRange.first
    val minYVel = target.yRange.first

    for (xVel in 1..maxXVel) {
        for (yVel in minYVel..maxYVel) {
            if (simulate(xVel, yVel, target)) {
                possibleVelocities += xVel to yVel
            }
        }
    }

    println(possibleVelocities.size)
}

private fun simulate(startXVelocity: Int, startYVelocity: Int, target: Rect): Boolean {
    var xVelocity = startXVelocity
    var yVelocity = startYVelocity

    var pos = Point(0, 0)

    while (pos.x < target.xRange.last && pos.y > target.yRange.first) {
        pos = pos.copy(x = pos.x + xVelocity, y = pos.y + yVelocity)

        if (pos.x in target.xRange && pos.y in target.yRange) {
            return true
        }

        if (xVelocity != 0) {
            xVelocity += if (xVelocity > 0) -1 else 1
        }
        yVelocity -= 1
    }

    return false
}

private fun parse(input: File): Rect {
    val (xRange, yRange) = input.readText()
        .substring(15) // Remove 'target area: x='
        .split(", y=") // Remove ', y=' and split in two
        .map { it.split("..") } // Remove each '..'
        .map { (start, end) -> start.toInt()..end.toInt() } // Convert each into an IntRange

    return Rect(xRange, yRange)
}

private data class Point(val x: Int, val y: Int)
private data class Rect(val xRange: IntRange, val yRange: IntRange)
