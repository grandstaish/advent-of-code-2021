package day04

import java.io.File

fun main() {
    val input = File("src/day04/input.txt").readText().split("\n\n")

    val guesses = input[0].split(",").map { it.toInt() }
    val boards = input.subList(1, input.size).map { Board(it) }

    val completed = BooleanArray(boards.size)
    var lastScore = 0

    for (guess in guesses) {
        for ((i, board) in boards.withIndex()) {
            if (!completed[i] && board.guess(guess)) {
                completed[i] = true
                lastScore = board.remainingSum() * guess
            }
        }
    }

    println(lastScore)
}

private class Board(data: String) {
    private val indexMap = Array<Pair<Int, Int>?>(100) { null }
    private val rowCounts = IntArray(5)
    private val colCounts = IntArray(5)
    private var unmarkedSum = 0

    init {
        val rows = data.split("\n")
        for (row in 0..4) {
            val nums = rows[row].split(" ").filter { it.isNotEmpty() }
            for (col in 0..4) {
                val num = nums[col].toInt()
                indexMap[num] = Pair(row, col)
                unmarkedSum += num
            }
        }
    }

    fun guess(value: Int): Boolean {
        val (row, col) = indexMap[value] ?: return false
        indexMap[value] = null

        unmarkedSum -= value

        rowCounts[row]++
        colCounts[col]++

        return rowCounts[row] == 5 || colCounts[col] == 5
    }

    fun remainingSum() = unmarkedSum
}
