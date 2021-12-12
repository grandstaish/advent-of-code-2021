package day12

import java.io.File

fun main() {
    val start = parseInput(File("src/day12/input.txt").readLines())
    println(dfs(start, emptySet(), false))
}

private fun dfs(node: Node, visited: Set<String>, hasMadeException: Boolean): Int {
    if (node.value == "end") return 1

    var exception = hasMadeException
    if (visited.contains(node.value)) {
        if (hasMadeException || node.value == "start") return 0
        exception = true
    }

    var paths = 0
    val nextVisited = if (node.isLarge) visited else visited + node.value
    for (child in node.children) {
        paths += dfs(child, nextVisited, exception)
    }

    return paths
}

private fun parseInput(input: List<String>): Node {
    val nodes = mutableMapOf<String, Node>()
    for (line in input) {
        val (s, e) = line.split("-")
        val sNode = nodes.getOrPut(s) {
            Node(value = s, isLarge = s[0].isUpperCase())
        }
        val eNode = nodes.getOrPut(e) {
            Node(value = e, isLarge = e[0].isUpperCase())
        }
        sNode.children.add(eNode)
        eNode.children.add(sNode)
    }
    return nodes.getValue("start")
}

private data class Node(
    val value: String,
    val isLarge: Boolean
) {
    val children = mutableListOf<Node>()
}
