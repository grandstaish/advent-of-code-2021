package day05

import java.io.File

fun main() {
    val lines = File("src/day05/input.txt").readLines()

    val mem = mutableMapOf<Pair<Int, Int>, Int>()

    for (line in lines) {
        val (start, end) = line.split(" -> ")
        val (sx, sy) = start.split(",").map { it.toInt() }
        val (ex, ey) = end.split(",").map { it.toInt() }

        val dx = ex - sx
        val dy = ey - sy

        var cx = sx
        var cy = sy

        while (cx != ex || cy != ey) {
            mem[cx to cy] = mem.getOrDefault(cx to cy, 0) + 1
            if (dx > 0) cx++
            if (dx < 0) cx--
            if (dy > 0) cy++
            if (dy < 0) cy--
        }

        mem[cx to cy] = mem.getOrDefault(cx to cy, 0) + 1
    }

    println(mem.count { it.value > 1 })
}
