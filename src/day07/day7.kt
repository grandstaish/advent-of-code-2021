package day07

import java.io.File
import kotlin.math.abs

fun main() {
    val input = File("src/day07/input.txt").readText().split(",").map { it.toInt() }

    var minPos = Int.MAX_VALUE
    var maxPos = Int.MIN_VALUE
    for (v in input) {
        minPos = minOf(minPos, v)
        maxPos = maxOf(maxPos, v)
    }

    val mem = IntArray(maxPos - minPos + 1) { -1 }
    var minFuel = Int.MAX_VALUE
    for (pos in minPos..maxPos) {
        minFuel = minOf(input.sumOf { calculateFuelCost(mem, abs(it - pos)) }, minFuel)
    }

    println(minFuel)
}

private fun calculateFuelCost(mem: IntArray, dist: Int): Int {
    if (dist == 0) return 0
    if (dist == 1) return 1
    if (mem[dist] != -1) return mem[dist]
    mem[dist] = calculateFuelCost(mem, dist - 1) + dist
    return mem[dist]
}
