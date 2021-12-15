package day15

import java.io.File
import java.util.*

fun main() {
    val input = File("src/day15/input.txt").readLines().map { s -> s.map { c -> c - '0' } }

    val n = input.size * 5

    val distances = Array(n) { IntArray(n) { Int.MAX_VALUE } }
    val visited = Array(n) { BooleanArray(n) { false } }
    val q = PriorityQueue<Pair<Int, Int>> { (row1, col1), (row2, col2) ->
        distances[row1][col1].compareTo(distances[row2][col2])
    }

    distances[0][0] = 0
    q.add(0 to 0)

    while (q.isNotEmpty()) {
        val (row, col) = q.poll()

        for (dir in 0 until 4) {
            val nextRow = row + RowOffset[dir]
            val nextCol = col + ColOffset[dir]
            if (nextRow >= 0 && nextCol >= 0 && nextRow < n && nextCol < n && !visited[nextRow][nextCol]) {
                val nextDistance = distances[row][col] + input.getValue(nextRow, nextCol)
                if (nextDistance < distances[nextRow][nextCol]) {
                    distances[nextRow][nextCol] = nextDistance
                    q.add(nextRow to nextCol)
                }
            }
        }

        visited[row][col] = true
    }

    println(distances[n-1][n-1])
}

private fun List<List<Int>>.getValue(row: Int, col: Int): Int {
    val offset = (row / size) + (col / size)
    return (this[row % size][col % size] + offset - 1) % 9 + 1
}

private val RowOffset = intArrayOf(-1, 1, 0, 0)
private val ColOffset = intArrayOf(0, 0, -1, 1)
