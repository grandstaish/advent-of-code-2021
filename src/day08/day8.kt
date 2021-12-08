package day08

import java.io.File

fun main() {
    val input = File("src/day08/input.txt").readLines()

    var total = 0

    for (line in input) {
        val (signal, output) = line.split(" | ")

        val index = buildIndex(signal)

        var curr = 0
        for (code in output.split(" ")) {
            curr = curr * 10 + index.indexOf(code.toSet())
        }

        total += curr
    }

    println(total)
}

private fun buildIndex(signal: String): Array<Set<Char>> {
    val codes = signal.split(" ").sortedBy { it.length }

    val index = arrayOfNulls<Set<Char>>(10)

    index[1] = codes[0].toSet()
    index[7] = codes[1].toSet()
    index[4] = codes[2].toSet()
    index[8] = codes[9].toSet()

    // '2', '3', and '5'
    val fiveLineCodes = setOf(codes[3].toSet(), codes[4].toSet(), codes[5].toSet())

    // '3' is the only 5-line number with both the rhs lines. So we can just find the first 5-line
    // code that contains both of the chars found in '1'.
    index[3] = fiveLineCodes.first { it.containsAll(index[1]!!) }

    // Isolate the top-left line, then use that to decide which of the two remaining codes corresponds to '5'
    val tl = (index[4]!! - index[3]!!).single()
    index[5] = fiveLineCodes.first { tl in it }

    // The last 5-line code must be '2'.
    index[2] = fiveLineCodes.first { it != index[3] && it != index[5] }

    // '0', '6', and '9'
    val sixLineCodes = setOf(codes[6].toSet(), codes[7].toSet(), codes[8].toSet())

    // Isolate the middle line, then use that to decide which of the three codes corresponds to '0'
    val mid = (index[4]!! - index[1]!! - tl).single()
    index[0] = sixLineCodes.first { mid !in it }

    // '9' is the only remaining 6-line number with both the rhs line. So we can just find the first 6-line
    // code that contains both of the chars found in '1'.
    index[9] = sixLineCodes.first { it != index[0] && it.containsAll(index[1]!!) }

    // The last 6-line code must be '6'.
    index[6] = sixLineCodes.first { it != index[0] && it != index[9] }

    return index.requireNoNulls()
}
