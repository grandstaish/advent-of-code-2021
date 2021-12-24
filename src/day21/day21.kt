package day21

import java.io.File

fun main() {
    val initialPositions = File("src/day21/input.txt").readLines()
        .map { it.split(":") }
        .map { (_, position) -> position.trim().toInt() }

    val N = initialPositions.size
    val initialPoints = List(N) { 0 }

    val mem = mutableMapOf<GameState, LongArray>()

    fun turn(state: GameState): LongArray {
        val (positions, points, playerIndex) = state

        val winner = points.indexOfFirst { it >= 21 }
        if (winner != -1) {
            val result = LongArray(N) { 0 }
            result[winner] = 1
            return result
        }
        if (mem[state] != null) {
            return mem[state]!!
        }

        val totalWins = LongArray(N)
        for (i in 1..3) {
            for (j in 1..3) {
                for (k in 1..3) {
                    val nextPositions = positions.toMutableList()
                    val nextPoints = points.toMutableList()
                    val nextPlayer = (playerIndex + 1) % N

                    nextPositions[playerIndex] = (positions[playerIndex] + i + j + k - 1) % 10 + 1
                    nextPoints[playerIndex] = points[playerIndex] + nextPositions[playerIndex]

                    val wins = turn(GameState(nextPositions, nextPoints, nextPlayer))
                    for (w in wins.indices) {
                        totalWins[w] += wins[w]
                    }
                }
            }
        }

        mem[state] = totalWins
        return mem[state]!!
    }

    println(turn(GameState(initialPositions, initialPoints, 0)).maxOrNull()!!)
}

private data class GameState(
    val positions: List<Int>,
    val points: List<Int>,
    val playerIndex: Int
)
