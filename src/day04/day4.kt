package day04

import java.io.File

fun main() {
    val input = File("src/day04/input.txt").readText().split("\n\n")

    val guesses = input[0].split(",").map { it.toInt() }
    val boards = input.subList(1, input.size).map { Board(it) }

    var lastScore = 0

    for (guess in guesses) {
        for (board in boards) {
            val unmarkedSum = board.guess(guess)
            if (unmarkedSum != null) {
                lastScore = unmarkedSum * guess
            }
        }
    }

    println(lastScore)
}

class Board(data: String) {
    private val grid: Array<Array<Int>>
    private val map = Array<Pair<Int, Int>?>(100) { null }
    private var unmarkedSum = 0
    var won = false
        private set

    init {
        val rows = data.split("\n")

        grid = Array(rows.size) { row ->
            val nums = rows[row].trim().split(Regex("\\s+"))
            Array(nums.size) { col ->
                val value = nums[col].toInt()
                map[value] = Pair(row, col)
                unmarkedSum += value
                value
            }
        }
    }

    fun guess(value: Int): Int? {
        if (won) return null

        val (row, col) = map[value] ?: return null
        unmarkedSum -= value
        map[value] = null

        grid[row][col] = Int.MIN_VALUE

        if (checkWin(row, col)) {
            won = true
            return unmarkedSum
        }

        return null
    }

    private fun checkWin(row: Int, col: Int): Boolean {
        var win = true

        for (i in grid.indices) {
            if (grid[i][col] != Int.MIN_VALUE) {
                win = false
                break
            }
        }
        if (win) {
            return true
        }

        win = true
        for (i in grid[row].indices) {
            if (grid[row][i] != Int.MIN_VALUE) {
                win = false
                break
            }
        }

        return win
    }
}
