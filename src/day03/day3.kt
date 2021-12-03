@file:Suppress("LocalVariableName")

import java.io.File

fun main() {
    val report = File("src/day03/input.txt").readLines()

    val tree = Tree()

    for (line in report) {
        tree.add(line)
    }

    val o2GeneratorRating = tree.findRating(RatingStrategy.O2Generator)
    val co2ScrubberRating = tree.findRating(RatingStrategy.CO2Scrubber)

    println(o2GeneratorRating * co2ScrubberRating)
}

class Tree {
    private val root = Node()

    fun add(line: String) {
        var curr = root
        for (c in line) {
            curr = if (c == '1') {
                if (curr.one == null) {
                    val n = Node()
                    curr.one = n
                    n
                } else {
                    curr.one!!
                }
            } else {
                if (curr.zero == null) {
                    val n = Node()
                    curr.zero = n
                    n
                } else {
                    curr.zero!!
                }
            }
            curr.frequency++
        }
    }

    fun findRating(strategy: RatingStrategy): Int{
        val stack = ArrayDeque<Int>()

        var curr = root
        while (curr.one != null || curr.zero != null) {
            curr = if (nextNodeIsOne(strategy, curr)) {
                stack.add(1)
                curr.one!!
            } else {
                stack.add(0)
                curr.zero!!
            }
        }

        var rating = 0
        var shift = 0
        while (stack.isNotEmpty()) {
            rating = rating or (stack.removeLast() shl shift)
            shift++
        }

        return rating
    }

    private fun nextNodeIsOne(strategy: RatingStrategy, node: Node): Boolean {
        val one = node.one ?: return false
        val zero = node.zero ?: return true

        return when (strategy) {
            RatingStrategy.O2Generator -> one.frequency >= zero.frequency
            RatingStrategy.CO2Scrubber -> one.frequency < zero.frequency
        }
    }
}

enum class RatingStrategy {
    O2Generator,
    CO2Scrubber
}

class Node {
    var frequency = 0
    var one: Node? = null
    var zero: Node? = null
}

//private fun part1(report: List<String>) {
//    val N = report[0].length
//    val mem = IntArray(N)
//
//    for (line in report) {
//        for (i in 0 until N) {
//            if (line[i] == '1') {
//                mem[i]++
//            } else {
//                mem[i]--
//            }
//        }
//    }
//
//    var gamma = 0
//    var shift = N - 1
//    for (i in 0 until N) {
//        if (mem[i] > 0) {
//            gamma = gamma or (1 shl shift)
//        }
//        shift--
//    }
//
//    val mask = 2.0.pow(N).toInt()  - 1
//    println(gamma * (gamma xor mask))
//}
