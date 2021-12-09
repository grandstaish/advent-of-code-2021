package day09

import java.io.File
import java.util.*

fun main() {
    val input = File("src/day09/input.txt").readLines()
    val grid = input.map { line -> line.toCharArray().map { it - '0' } }

    val basins = PriorityQueue<Int>(Collections.reverseOrder())

    val visited = Array(grid.size) { BooleanArray(grid[0].size) }

    for (i in grid.indices) {
        for (j in grid[0].indices) {
            val basin = grid.countBasinAtIndex(i, j, visited)
            if (basin != 0) {
                basins.add(basin)
            }
        }
    }

    println(basins.poll() * basins.poll() * basins.poll())
}

private fun List<List<Int>>.countBasinAtIndex(i: Int, j: Int, visited: Array<BooleanArray>): Int {
    if (i < 0 || i >= size) return 0
    if (j < 0 || j >= this[0].size) return 0

    if (visited[i][j]) return 0
    visited[i][j] = true

    if (this[i][j] == 9) return 0

    var count = 1
    for ((rowOffset, colOffset) in DirectionOffsets) {
        count += countBasinAtIndex(i + rowOffset, j + colOffset, visited)
    }

    return count
}

private val DirectionOffsets = arrayOf(
    intArrayOf(-1, 0), // Left
    intArrayOf(0, -1), // Top
    intArrayOf(1, 0),  // Right
    intArrayOf(0, 1)   // Bottom
)
