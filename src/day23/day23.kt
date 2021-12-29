package day23

import java.io.File
import java.lang.IllegalStateException
import java.util.*
import kotlin.math.abs

fun main() {
    val grid = File("src/day23/input.txt").readLines().map { line -> line.map { it } }
    println(rearrange(grid))
}

private fun rearrange(initialGrid: List<List<Char>>): Int {
    val costs = mutableMapOf<List<List<Char>>, Int>()

    val q = PriorityQueue<State>()
    q.add(State(initialGrid, 0))

    while (q.isNotEmpty()) {
        val state = q.poll()
        val grid = state.grid

        if (grid.checkWin()) return state.energy

        fun maybeQueue(move: State) {
            val prevCost = costs[move.grid]
            if (prevCost == null || move.energy < prevCost) {
                costs[move.grid] = move.energy
                q += move
            }
        }

        for (col in 1..11) {
            for (move in state.possibleMoves(1, col)) {
                maybeQueue(move)
            }
        }

        for (row in 2..5) {
            for (amphipod in 'A'..'D') {
                for (move in state.possibleMoves(row, amphipod.burrowCol)) {
                    maybeQueue(move)
                }
            }
        }
    }

    throw IllegalStateException("Could not rearrange")
}

private fun State.possibleMoves(row: Int, col: Int): List<State> {
    if (grid[row][col] == '.') {
        // This is an empty space. No moves from here.
        return emptyList()
    }

    return when (row) {
        in 2..5 -> {
            // Burrow indices. Look at possible moves from here.

            for (i in row-1 downTo 2) {
                if (grid[i][col] != '.') {
                    // Trapped by another amphipod. No possible moves from here.
                    return emptyList()
                }
            }

            if (grid[row][col].burrowCol == col) {
                var trapping = false
                for (i in row+1..5) {
                    if (grid[i][col].burrowCol != col) {
                        trapping = true
                    }
                }
                if (!trapping) {
                    // Amphipod already home and not trapping any others. No moves required.
                    return emptyList()
                }
            }

            val moves = mutableListOf<State>()

            // Right moves
            var curr = col
            while (grid[1][curr] == '.') {
                if (canStop(curr)) {
                    moves += swap(row, col, 1, curr)
                }
                curr++
            }

            // Left moves
            curr = col
            while (grid[1][curr] == '.') {
                if (canStop(curr)) {
                    moves += swap(row, col, 1, curr)
                }
                curr--
            }

            moves
        }
        1 -> {
            val targetCol = grid[row][col].burrowCol

            for (i in 2..5) {
                if (grid[i][targetCol] in 'A'..'D' && grid[i][targetCol] != grid[row][col]) {
                    // Can't go home yet because there's another amphipod in the burrow that doesn't belong.
                    return emptyList()
                }
            }

            var curr = col
            while (curr != targetCol) {
                if (curr > targetCol) curr-- else curr++
                if (grid[row][curr] != '.') {
                    // Cannot go home from here because something's blocking the way.
                    return emptyList()
                }
            }

            var i = 2
            while (grid[i + 1][targetCol] == '.') {
                i++
            }

            // Send em home!
            listOf(swap(row, col, i, targetCol))
        }
        else -> {
            throw IllegalArgumentException()
        }
    }
}

private fun State.swap(row1: Int, col1: Int, row2: Int, col2: Int): State {
    check(grid[row1][col1] in 'A'..'D')
    check(grid[row2][col2] == '.')

    val next = List(grid.size) { row ->
        when (row) {
            row1 -> {
                List(grid[row].size) { col ->
                    when (col) {
                        col1 -> grid[row2][col2]
                        else -> grid[row][col]
                    }
                }
            }
            row2 -> {
                List(grid[row].size) { col ->
                    when (col) {
                        col2 -> grid[row1][col1]
                        else -> grid[row][col]
                    }
                }
            }
            else -> {
                grid[row]
            }
        }
    }
    val dX = abs(col2 - col1)
    val dY = abs(row2 - row1)
    val cost = (dX + dY) * grid[row1][col1].moveEnergyCost

    return State(next, energy + cost)
}

private fun canStop(col: Int): Boolean = col != 3 && col != 5 && col != 7 && col != 9

private fun List<List<Char>>.checkWin(): Boolean {
    for (row in 2..5) {
        for (amphipod in 'A'..'D') {
            if (this[row][amphipod.burrowCol] != amphipod) {
                return false
            }
        }
    }
    return true
}

private val Char.burrowCol get() = when (this) {
    'A' -> 3
    'B' -> 5
    'C' -> 7
    'D' -> 9
    else -> throw IllegalArgumentException("Tried to get burrow col for $this")
}

private val Char.moveEnergyCost get() = when (this) {
    'A' -> 1
    'B' -> 10
    'C' -> 100
    'D' -> 1000
    else -> throw IllegalArgumentException()
}

private class State(val grid: List<List<Char>>, val energy: Int): Comparable<State> {
    override fun compareTo(other: State): Int {
        return energy.compareTo(other.energy)
    }
}
