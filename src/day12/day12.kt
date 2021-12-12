package day12

import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val start: Node
    val parseTime = measureTimeMillis {
        start = parseInput(File("src/day12/input.txt"))
    }
    val result: Int
    val dfsTime = measureTimeMillis {
        result = dfs(start, false)
    }
    println("Parse time: ${parseTime}ms")
    println("Dfs time: ${dfsTime}ms")
    println("Result: $result")
}

private fun dfs(node: Node, hasMadeException: Boolean): Int {
    if (node.isEnd) return 1

    var exception = hasMadeException
    if (!node.isLarge && node.frequency > 0) {
        if (hasMadeException || node.isStart) return 0
        exception = true
    }

    node.frequency++

    var paths = 0
    for (child in node.children) {
        paths += dfs(child, exception)
    }

    node.frequency--

    return paths
}

private fun parseInput(file: File): Node {
    val nodes = mutableMapOf<String, Node>()
    for (line in file.readLines()) {
        val (s, e) = line.split("-")
        val sNode = nodes.getOrPut(s) {
            Node(value = s, isLarge = s[0].isUpperCase(), isEnd = s == "end", isStart = s == "start")
        }
        val eNode = nodes.getOrPut(e) {
            Node(value = e, isLarge = e[0].isUpperCase(), isEnd = e == "end", isStart = e == "start")
        }
        sNode.children.add(eNode)
        eNode.children.add(sNode)
    }
    return nodes.getValue("start")
}

private data class Node(
    val value: String,
    val isLarge: Boolean,
    val isEnd: Boolean,
    val isStart: Boolean
) {
    var frequency = 0
    val children = mutableListOf<Node>()
}
