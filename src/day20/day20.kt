package day20

import java.io.File

fun main() {
    val (enhancementInput, imageInput) = File("src/day20/input.txt").readText().split("\n\n")

    var image = parseImage(imageInput)
    val enhancement = enhancementInput.replace("\n", "")
    repeat(50) { round ->
        image = enhance(round, image, enhancement)
    }

    println(image.size)
}

private fun enhance(round: Int, prev: Set<Pair<Int, Int>>, enhancement: String): Set<Pair<Int, Int>> {
    val isEven = round % 2 == 0

    val next = mutableSetOf<Pair<Int, Int>>()

    var minCol = Int.MAX_VALUE
    var minRow = Int.MAX_VALUE
    var maxCol = Int.MIN_VALUE
    var maxRow = Int.MIN_VALUE

    for ((row, col) in prev) {
        minCol = minOf(minCol, col)
        minRow = minOf(minRow, row)
        maxCol = maxOf(maxCol, col)
        maxRow = maxOf(maxRow, row)
    }

    for (row in (minRow - 1)..(maxRow + 1)) {
        for (col in (minCol - 1)..(maxCol + 1)) {
            var shift = 8
            var index = 0
            for (rowOffset in -1..1) {
                for (colOffset in -1..1) {
                    val inPreviousImage = row + rowOffset to col + colOffset in prev
                    if (enhancement[0] == '.' || isEven) {
                        // Previous image included all #'s, so the index is derived from pixels in the prev image
                        if (inPreviousImage) {
                            index += (1 shl shift)
                        }
                    } else {
                        // Previous image excluded all #'s, so the index is derived from pixels *not* in the prev image
                        if (!inPreviousImage) {
                            index += (1 shl shift)
                        }
                    }
                    shift--
                }
            }

            if (enhancement[0] == '.' || !isEven) {
                // Add only # to the next image
                if (enhancement[index] == '#') {
                    next += row to col
                }
            } else {
                // Add only .'s to the next image
                if (enhancement[index] != '#') {
                    next += row to col
                }
            }
        }
    }

    return next
}

private fun parseImage(input: String): Set<Pair<Int, Int>> {
    val image = mutableSetOf<Pair<Int, Int>>()
    for ((row, line) in input.lines().withIndex()) {
        for ((col, c) in line.withIndex()) {
            if (c == '#') {
                image += row to col
            }
        }
    }
    return image
}
