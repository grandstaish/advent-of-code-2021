package day12

import java.io.File

fun main() {
    val start = parseInput(File("src/day12/input.txt").readLines())
    val paths = dfs(start, emptySet(), false)
    println(paths.size)
}

private fun dfs(node: Node, visited: Set<String>, hasMadeException: Boolean): Set<String> {
    if (node.value == "end") return setOf("end")
    if (visited.contains(node.value)) return emptySet()

    val paths = mutableSetOf<String>()
    for (child in node.children) {
        paths += if (!hasMadeException && !node.isLarge && node.value != "start") {
            dfs(child, visited, true) + dfs(child, visited + node.value, false)
        } else {
            dfs(child, if (node.isLarge) visited else visited + node.value, hasMadeException)
        }
    }

    return paths.map { node.value + it }.toSet()
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
