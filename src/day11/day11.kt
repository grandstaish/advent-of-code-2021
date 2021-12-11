package day11

import java.io.File

fun main() {
    val input = File("src/day11/input.txt").readLines()
        .map { line -> line.map { it - '0' }.toIntArray() }
        .toTypedArray()

    var step = 1
    while (performStep(input) != 100) {
        step++
    }

    println(step)
}

private fun performStep(input: Array<IntArray>): Int {
    var flashes = 0

    for (i in 0..9) {
        for (j in 0..9) {
            input[i][j]++
            flashes += maybeFlash(input, i, j)
        }
    }

    for (i in 0..9) {
        for (j in 0..9) {
            if (input[i][j] > 9) {
                input[i][j] = 0
            }
        }
    }

    return flashes
}

private fun maybeFlash(input: Array<IntArray>, i: Int, j: Int): Int {
    if (i < 0 || i >= 10 || j < 0 || j >= 10) return 0
    if (input[i][j] != 10) return 0
    input[i][j]++ // Bump to 11 so that it is marked as counted.

    var flashes = 1
    for (x in -1..1) {
        for (y in -1..1) {
            flashes += energize(input, i + x, j + y)
        }
    }

    return flashes
}

private fun energize(input: Array<IntArray>, i: Int, j: Int): Int {
    if (i < 0 || i >= 10 || j < 0 || j >= 10) return 0
    if (input[i][j] >= 10) return 0 // Do not bump past 10, otherwise it'll be marked as counted.
    input[i][j]++

    return maybeFlash(input, i, j)
}
