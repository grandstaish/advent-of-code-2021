package day18

import java.io.File

fun main() {
    val input = parse(File("src/day18/input.txt"))

    var max = Int.MIN_VALUE
    for (i in input.indices) {
        for (j in input.indices) {
            if (i == j) continue
            max = maxOf(max, (input[i] + input[j]).magnitude())
        }
    }

    println(max)
}

private operator fun Node.plus(other: Node) = generateSequence<Node>(PairNum(deepCopy(), other.deepCopy())) {
    if (tryExplode(it) || trySplit(it)) it else null
}.last()

private fun Node.deepCopy(): Node = when (this) {
    is PairNum -> PairNum(left.deepCopy(), right.deepCopy())
    is LiteralNum -> LiteralNum(value)
}

private fun Node.magnitude(): Int = when (this) {
    is PairNum -> 3 * left.magnitude() + 2 * right.magnitude()
    is LiteralNum -> value
}

private fun dfs(root: Node, depth: Int = 0, block: Node.(Int) -> Boolean): Boolean {
    if (root is PairNum) {
        if (dfs(root.left, depth + 1, block)) return true
        if (dfs(root.right, depth + 1, block)) return true
    }
    return block(root, depth)
}

private fun trySplit(root: Node): Boolean = dfs(root) {
    if (this !is LiteralNum || value < 10) return@dfs false
    val p = parent!!
    val n = PairNum(LiteralNum(value / 2), LiteralNum(value - value / 2))
    if (p.left == this) p.left = n else p.right = n
    true
}

private fun tryExplode(root: Node): Boolean = dfs(root) { depth ->
    if (this !is PairNum || depth < 4) return@dfs false
    val parents = mutableSetOf(this)
    var p: PairNum? = parent!!
    while (p != null) {
        parents += p
        p = p.parent
    }
    addInDirection((left as LiteralNum).value, true, parents, mutableSetOf(left))
    addInDirection((right as LiteralNum).value, false, parents, mutableSetOf(right))
    p = parent!!
    if (p.left == this) p.left = LiteralNum(0) else p.right = LiteralNum(0)
    true
}

private fun Node.addInDirection(n: Int, goLeft: Boolean, parents: Set<PairNum>, visited: MutableSet<Node>): Boolean {
    if (this in visited) return false
    visited.add(this)

    return when (this) {
        is LiteralNum -> {
            value += n
            true
        }
        is PairNum -> {
            if (goLeft) {
                (this !in parents && right.addInDirection(n, goLeft, parents, visited)) ||
                        (left.addInDirection(n, goLeft, parents, visited)) ||
                        (parent?.addInDirection(n, goLeft, parents, visited) == true)
            } else {
                (this !in parents && left.addInDirection(n, goLeft, parents, visited)) ||
                        (right.addInDirection(n, goLeft, parents, visited)) ||
                        (parent?.addInDirection(n, goLeft, parents, visited) == true)
            }
        }
    }
}

private fun parse(input: File): List<Node> = input.readLines().map { it.toNode() }

private fun String.toNode(): Node {
    if (length == 1) return LiteralNum(toInt())

    var middle = -1
    var depth = 0
    for ((i, c) in withIndex()) {
        when (c) {
            '[' -> depth++
            ']' -> depth--
            ',' -> {
                if (depth == 1) {
                    middle = i
                    break
                }
            }
        }
    }

    return PairNum(substring(1, middle).toNode(), substring(middle + 1, length - 1).toNode())
}

private sealed class Node {
    var parent: PairNum? = null
}

private class LiteralNum(var value: Int) : Node() {
    override fun toString() = "$value"
}

private class PairNum(left: Node, right: Node) : Node() {
    var left: Node = left
        set(value) {
            field = value
            value.parent = this
        }

    var right: Node = right
        set(value) {
            field = value
            value.parent = this
        }

    init {
        left.parent = this
        right.parent = this
    }

    override fun toString(): String {
        return "[$left,$right]"
    }
}
