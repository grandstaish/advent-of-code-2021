package day18

import java.io.File

fun main() {
    val input = parse(File("src/day18/input.txt"))

    var max = Int.MIN_VALUE

    for (i in input.indices) {
        for (j in input.indices) {
            if (i == j) continue
            val p = PairNum()
            p.left = input[i]
            p.right = input[j]
            max = maxOf(max, p.deepCopy().reduce().magnitude())
        }
    }

    println(max)
}

private fun Node.deepCopy(): Node = when (this) {
    is PairNum -> PairNum().also {
        it.left = left.deepCopy()
        it.right = right.deepCopy()
    }
    is LiteralNum -> LiteralNum(value)
}

private fun Node.magnitude(): Int = when (this) {
    is PairNum -> 3 * left.magnitude() + 2 * right.magnitude()
    is LiteralNum -> value
}

private fun Node.reduce(): Node {
    while (explodeIfNecessary(0) || splitIfNecessary()) {
        // Loop until no changes are made.
    }
    return this
}

private fun Node.splitIfNecessary(): Boolean {
    if (this is PairNum) {
        if (left.splitIfNecessary() || right.splitIfNecessary()) return true
    }
    if (this is LiteralNum && value >= 10) {
        val newNode = PairNum()
        newNode.left = LiteralNum(value / 2)
        newNode.right = LiteralNum(value - value / 2)
        if (parent!!.left == this) parent!!.left = newNode else parent!!.right = newNode
        return true
    }
    return false
}

private fun Node.explodeIfNecessary(depth: Int): Boolean {
    if (this is PairNum) {
        if (left.explodeIfNecessary(depth + 1) || right.explodeIfNecessary(depth + 1)) {
            return true
        }
        if (depth >= 4) {
            val parents = mutableSetOf<Node>(this)
            var p: PairNum? = parent
            while (p != null) {
                parents += p
                p = p.parent
            }
            addInDirection((left as LiteralNum).value, true, parents, mutableSetOf(left))
            addInDirection((right as LiteralNum).value, false, parents, mutableSetOf(right))
            if (parent!!.left == this) parent!!.left = LiteralNum(0) else parent!!.right = LiteralNum(0)
            return true
        }
    }
    return false
}

private fun Node.addInDirection(value: Int, goLeft: Boolean, parents: Set<Node>, visited: MutableSet<Node>): Boolean {
    if (this in visited) return false
    visited.add(this)

    return when (this) {
        is LiteralNum -> {
            this.value += value
            true
        }
        is PairNum -> {
            if (goLeft) {
                (this !in parents && right.addInDirection(value, goLeft, parents, visited)) ||
                        (left.addInDirection(value, goLeft, parents, visited)) ||
                        (parent?.addInDirection(value, goLeft, parents, visited) == true)
            } else {
                (this !in parents && left.addInDirection(value, goLeft, parents, visited)) ||
                        (right.addInDirection(value, goLeft, parents, visited)) ||
                        (parent?.addInDirection(value, goLeft, parents, visited) == true)
            }
        }
    }
}

private fun parse(input: File): List<Node> = input.readLines().map { parse(it) }

private fun parse(num: String): Node {
    var value = -1
    val curr = ArrayDeque<PairNum>()

    for (c in num) {
        if (c == '[') curr += PairNum()

        if (c == ',') {
            if (value >= 0) {
                curr.last().left = LiteralNum(value)
            } else {
                val popped = curr.removeLast()
                curr.last().left = popped
            }
        }

        if (c == ']') {
            if (value >= 0) {
                curr.last().right = LiteralNum(value)
            } else {
                val popped = curr.removeLast()
                curr.last().right = popped
            }
        }

        value = if (c in '0'..'9') (c - '0') else -1
    }

    return curr.first()
}

private sealed class Node {
    var parent: PairNum? = null
}

private class LiteralNum(var value: Int) : Node() {
    override fun toString() = "$value"
}

private class PairNum : Node() {
    var left: Node = LiteralNum(-1)
        set(value) {
            field = value
            value.parent = this
        }

    var right: Node = LiteralNum(-1)
        set(value) {
            field = value
            value.parent = this
        }

    override fun toString(): String {
        return "[$left,$right]"
    }
}
