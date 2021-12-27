package day22

import java.io.File

fun main() {
    val steps = parseSteps(File("src/day22/input.txt"))

    var processed = listOf<Step>()
    for (step in steps) {
        val nextProcessed = mutableListOf<Step>()
        for (p in processed) {
            // Break apart the previous cuboid removing a space to make room for the new cuboid.
            nextProcessed += p.breakIntoSegmentsRemoving(step)
        }
        if (step.on) {
            nextProcessed += step
        }
        processed = nextProcessed
    }

    var count = 0L
    for (p in processed) {
        count += p.size
    }

    println(count)
}

private fun parseSteps(file: File): List<Step> {
    fun parseRange(input: String): IntRange {
        val (start, end) = input.split("=")[1].split("..")
        return start.toInt()..end.toInt()
    }
    return file.readLines().map {
        val (on, ranges) = it.split(" ")
        val (x, y, z) = ranges.split(",")
        Step(on == "on", parseRange(x), parseRange(y), parseRange(z))
    }
}

private data class Step(
    val on: Boolean,
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange
) {
    val size get() = 1L * xRange.size * yRange.size * zRange.size

    fun breakIntoSegmentsRemoving(toRemove: Step): List<Step> {
        val intersection = intersect(toRemove)
        return if (intersection != null) {
            listOf(
                leftSegment(intersection),
                rightSegment(intersection),
                topSegment(intersection),
                bottomSegment(intersection),
                frontSegment(intersection),
                behindSegment(intersection)
            ).filter { !it.isEmpty() }
        } else {
            listOf(this)
        }
    }

    // Gets the entire slice left of the intersection.
    private fun leftSegment(intersection: Step) = Step(
        on,
        xRange.first until intersection.xRange.first,
        yRange,
        zRange
    )

    // Gets the entire slice right of the intersection.
    private fun rightSegment(intersection: Step) = Step(
        on,
        intersection.xRange.last+1..xRange.last,
        yRange,
        zRange
    )

    // Gets the remaining slice above the intersection.
    private fun topSegment(intersection: Step) = Step(
        on,
        intersection.xRange,
        yRange.first until intersection.yRange.first,
        zRange
    )

    // Gets the remaining slice below the intersection.
    private fun bottomSegment(intersection: Step) = Step(
        on,
        intersection.xRange,
        intersection.yRange.last+1..yRange.last,
        zRange
    )

    // Gets the remaining slice in front of the intersection.
    private fun frontSegment(intersection: Step) = Step(
        on,
        intersection.xRange,
        intersection.yRange,
        zRange.first until intersection.zRange.first
    )

    // Gets the remaining slice behind the intersection.
    private fun behindSegment(intersection: Step) = Step(
        on,
        intersection.xRange,
        intersection.yRange,
        intersection.zRange.last+1..zRange.last
    )

    private fun intersect(other: Step): Step? {
        if (xRange.first > other.xRange.last || xRange.last < other.xRange.first) return null
        if (yRange.first > other.yRange.last || yRange.last < other.yRange.first) return null
        if (zRange.first > other.zRange.last || zRange.last < other.zRange.first) return null

        val xStart = maxOf(xRange.first, other.xRange.first)
        val xEnd = minOf(xRange.last, other.xRange.last)

        val yStart = maxOf(yRange.first, other.yRange.first)
        val yEnd = minOf(yRange.last, other.yRange.last)

        val zStart = maxOf(zRange.first, other.zRange.first)
        val zEnd = minOf(zRange.last, other.zRange.last)

        return Step(on, xStart..xEnd, yStart..yEnd, zStart..zEnd)
    }

    private fun isEmpty() = xRange.isEmpty() || yRange.isEmpty() || zRange.isEmpty()

    private val IntRange.size get() = last - first + 1
}
