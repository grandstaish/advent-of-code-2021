package day05

import java.io.File
import kotlin.math.sign

fun main() {
    val lines = File("src/day05/input.txt").readLines()

    val mem = mutableMapOf<Pair<Int, Int>, Int>()

    for (line in lines) {
        val (start, end) = line.split(" -> ")
        val (sx, sy) = start.split(",").map { it.toInt() }
        val (ex, ey) = end.split(",").map { it.toInt() }

        val dx = (ex - sx).sign
        val dy = (ey - sy).sign

        var cx = sx
        var cy = sy

        while (cx != ex || cy != ey) {
            mem[cx to cy] = mem.getOrDefault(cx to cy, 0) + 1
            cx += dx
            cy += dy
        }

        mem[cx to cy] = mem.getOrDefault(cx to cy, 0) + 1
    }

    println(mem.count { it.value > 1 })
}
