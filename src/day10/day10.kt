package day10

import java.io.File

fun main() {
    val input = File("src/day10/input.txt").readLines()

    val stack = ArrayDeque<Char>()
    val scores = mutableListOf<Long>()

    for (line in input) {
        for (c in line) {
            when (c) {
                '(', '[', '{', '<' -> stack.add(c)
                else -> {
                    val last = stack.removeLast()
                    if (last.closingChar != c) {
                        stack.clear()
                        break
                    }
                }
            }
        }
        if (stack.isNotEmpty()) {
            var score = 0L
            while (stack.isNotEmpty()) {
                score = score * 5 + stack.removeLast().closingChar.score
            }
            scores += score
        }
    }

    println(scores.sorted()[scores.size / 2])
}

private val Char.closingChar get(): Char = when (this) {
    '(' -> ')'
    '[' -> ']'
    '{' -> '}'
    '<' -> '>'
    else -> throw IllegalArgumentException("Invalid char $this.")
}

private val Char.score get(): Int = when (this) {
    ')' -> 1
    ']' -> 2
    '}' -> 3
    '>' -> 4
    else -> throw IllegalArgumentException("Invalid char $this.")
}
