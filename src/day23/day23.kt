package day23

import java.io.File
import kotlin.collections.ArrayDeque
import kotlin.math.abs

fun main() {
    rearrange(parse(File("src/day23/input.txt")))
}

private fun parse(file: File): State {
    val lines = file.readLines()
    val hallway = List(11) { null }
    val burrows = List(4) { col -> List(4) { row -> lines[row + 2][col * 2 + 3] } }
    return State(burrows, hallway)
}

private fun rearrange(initialState: State) {
    val costs = mutableMapOf<State, Int>()

    val q = ArrayDeque<State>()
    q.add(initialState)

    var minState: State? = null
    var min = Int.MAX_VALUE

    while (q.isNotEmpty()) {
        val state = q.removeFirst()
        val cost = costs.getOrDefault(state, 0)
        if (state.checkWin()) {
            min = minOf(min, cost)
            minState = state
            continue
        }

        fun maybeQueue(move: State, energy: Int) {
            val prevCost = costs.getOrDefault(move, Int.MAX_VALUE)
            if (energy < prevCost) {
                costs[move] = energy
                q += move
            }
        }

        for ((next, energy) in state.movesFromHallway()) {
            maybeQueue(next, cost + energy)
        }
        for ((next, energy) in state.movesFromBurrows()) {
            maybeQueue(next, cost + energy)
        }
    }

    printSteps(minState!!, costs)
}

private fun State.movesFromHallway(): List<Pair<State, Int>> {
    val result = mutableListOf<Pair<State, Int>>()

    for (hallwayIndex in hallway.indices) {
        if (hallway[hallwayIndex] in 'A'..'D') {
            val burrowIndex = hallway[hallwayIndex]!! - 'A'

            // An amphipod can only return to its burrow if its empty or has no misplaced amphipods.
            if (burrows[burrowIndex].any { it != hallway[hallwayIndex] && it != null }) continue

            // Ensure the hallway path is clear so that this fella can make it home.
            if ((hallwayIndex towards burrowIndex * 2 + 2).any { hallway[it] != null }) continue

            // Check how deep they need to swim into the burrow. Depth can never be -1 because of the previous checks.
            result += swap(hallwayIndex, burrowIndex, depth = burrows[burrowIndex].lastIndexOf(null))
        }
    }

    return result
}

private fun State.movesFromBurrows(): List<Pair<State, Int>> {
    val result = mutableListOf<Pair<State, Int>>()

    for (burrowIndex in burrows.indices) {
        // Check if the burrow is empty or complete. If so, skip.
        if (burrows[burrowIndex].all { it == null || it == 'A' + burrowIndex }) continue

        // Find the depth of the first amphipod. This can never be -1 because of the previous checks.
        val depth = burrows[burrowIndex].lastIndexOf(null) + 1

        // Move to hallway positions to the left of the current burrow.
        for (h in burrowIndex * 2 + 2 downTo 0) {
            if (h == 2 || h == 4 || h == 6 || h == 8) continue
            if (hallway[h] != null) break
            result += swap(h, burrowIndex, depth)
        }

        // Move to hallway positions to the right of the current burrow.
        for (h in burrowIndex * 2 + 2 until 11) {
            if (h == 2 || h == 4 || h == 6 || h == 8) continue
            if (hallway[h] != null) break
            result += swap(h, burrowIndex, depth)
        }
    }

    return result
}

private fun State.swap(hallwayIndex: Int, burrowIndex: Int, depth: Int): Pair<State, Int> {
    val nextBurrows = burrows.toMutableList().let { burrows ->
        burrows[burrowIndex] = burrows[burrowIndex].toMutableList().let { burrow ->
            burrow[depth] = hallway[hallwayIndex]
            burrow
        }
        burrows
    }

    val nextHallway = hallway.toMutableList().let { hallway ->
        hallway[hallwayIndex] = burrows[burrowIndex][depth]
        hallway
    }

    val amphipod = hallway[hallwayIndex] ?: burrows[burrowIndex][depth]!!
    val distance = abs(hallwayIndex - (burrowIndex * 2 + 2)) + depth + 1

    val next = State(nextBurrows, nextHallway)
    next.previous = this

    return next to distance * Cost[amphipod - 'A']
}

private fun State.checkWin(): Boolean {
    for (expected in 'A'..'D') {
        for (actual in burrows[expected - 'A']) {
            if (actual != expected) return false
        }
    }
    return true
}

private fun printSteps(state: State, costs: Map<State, Int>) {
    var curr: State? = state

    val steps = mutableListOf<State>()
    while (curr != null) {
        steps += curr
        curr = curr.previous
    }

    var previousCost = 0
    for ((i, step) in steps.reversed().withIndex()) {
        val totalCost = costs[step] ?: 0
        val cost = totalCost - previousCost
        previousCost = totalCost

        println("Step $i (cost: $cost, total: $totalCost):")
        println("#############")
        println(step.hallway.map { it ?: '.' }.joinToString(prefix = "#", postfix = "#", separator = ""))
        for (depth in 0..3) {
            println(List(4) { burrow -> step.burrows[burrow][depth] ?: '.' }.joinToString(
                prefix = if (depth == 0) "###" else "  #",
                postfix = if (depth == 0) "###" else "#  ",
                separator = "#"
            ))
        }
        println("  #########  ")
        if (i != steps.lastIndex) {
            println()
        }
    }
}

private infix fun Int.towards(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this + step, to, step)
}

private data class State(val burrows: List<List<Char?>>, val hallway: List<Char?>) {
    var previous: State? = null
}

private val Cost = intArrayOf(1, 10, 100, 1000)
