package day11

import java.io.File

fun main() {
    val input = File("src/day11/input.txt").readLines()
        .map { line -> line.map { it - '0' }.toIntArray() }
        .toTypedArray()

    val total = input.size * input[0].size

    var step = 1
    while (performStep(input) != total) {
        step++
    }

    println(step)
}

private fun performStep(input: Array<IntArray>): Int {
    var flashes = 0

    for (i in input.indices) {
        for (j in input[0].indices) {
            input[i][j]++
        }
    }

    for (i in input.indices) {
        for (j in input[0].indices) {
            flashes += maybeFlash(input, i, j)
        }
    }

    for (i in input.indices) {
        for (j in input[0].indices) {
            if (input[i][j] > 9) {
                input[i][j] = 0
            }
        }
    }

    return flashes
}

private fun maybeFlash(input: Array<IntArray>, i: Int, j: Int): Int {
    if (i < 0 || i >= input.size) return 0
    if (j < 0 || j >= input[0].size) return 0
    if (input[i][j] != 10) return 0
    input[i][j]++ // Bump to 11 so that it is marked as counted.

    var flashes = 1

    for ((iOffset, jOffset) in Directions) {
        flashes += energize(input, i + iOffset, j + jOffset)
    }

    return flashes
}

private fun energize(input: Array<IntArray>, i: Int, j: Int): Int {
    if (i < 0 || i >= input.size) return 0
    if (j < 0 || j >= input[0].size) return 0
    if (input[i][j] >= 10) return 0 // Do not bump past 10, otherwise it'll be marked as counted.
    input[i][j]++
    return maybeFlash(input, i, j)
}

private val Directions = arrayOf(
    intArrayOf(-1, -1), // tl
    intArrayOf(0, -1),  // t
    intArrayOf(1, -1),  // tr
    intArrayOf(-1, 0),  // l
    intArrayOf(1, 0),   // r
    intArrayOf(-1, 1),  // bl
    intArrayOf(0, 1),   // b
    intArrayOf(1, 1)    // br
)
