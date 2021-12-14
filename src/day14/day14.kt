package day14

import java.io.File

fun main() {
    val (templateText, rulesText) = File("src/day14/input.txt").readText().split("\n\n")

    val rules = parseRules(rulesText)

    val counts = LongArray(26)
    counts[templateText[0] - 'A']++

    var map = mutableMapOf<Pair<Char, Char>, Long>()
    for (i in 1 until templateText.length) {
        val key = templateText[i-1] to templateText[i]
        map[key] = map.getOrDefault(key, 0) + 1
        counts[templateText[i] - 'A']++
    }

    for (i in 0..39) {
        val next = mutableMapOf<Pair<Char, Char>, Long>()
        for ((key, amount) in map) {
            val c = rules[key]
            if (c != null) {
                counts[c - 'A'] += amount
                next[key.first to c] = next.getOrDefault(key.first to c, 0) + amount
                next[c to key.second] = next.getOrDefault(c to key.second, 0) + amount
            }
        }
        map = next
    }

    val max = counts.maxOrNull()!!
    val min = counts.filter { it != 0L }.minOrNull()!!
    println(max - min)
}

private fun parseRules(rules: String): Map<Pair<Char, Char>, Char> {
    return rules.lines()
        .map { it.split(" -> ") }
        .associate { (it[0][0] to it[0][1]) to it[1][0] }
}
