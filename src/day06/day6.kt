package day06

import java.io.File

fun main() {
    val input = File("src/day06/input.txt").readText().split(",")

    val fishies = LongArray(9)

    for (fish in input) {
        fishies[fish.toInt()]++
    }

    for (day in 0 until 256) {
        val babies = fishies[0]
        for (i in 0 until 8) {
            fishies[i] = fishies[i + 1]
        }
        fishies[6] += babies
        fishies[8] = babies
    }

    println(fishies.sum())
}
