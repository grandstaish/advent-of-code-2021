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
    val codes = signal.split(" ").sortedBy { it.length }.map { it.toSet() }

    val index = Array(10) { emptySet<Char>() }

    index[1] = codes[0]
    index[7] = codes[1]
    index[4] = codes[2]
    index[8] = codes[9]

    for (code in listOf(codes[3], codes[4], codes[5])) {
        val digit = when {
            code.containsAll(index[1]) -> 3
            code.containsAll(index[4] - index[1]) -> 5
            else -> 2
        }
        index[digit] = code
    }

    for (code in listOf(codes[6], codes[7], codes[8])) {
        val digit = when {
            code.containsAll(index[4]) -> 9
            code.containsAll(index[5]) -> 6
            else -> 0
        }
        index[digit] = code
    }

    return index
}
