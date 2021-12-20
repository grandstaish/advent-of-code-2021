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
        //println(format())
    }
    return this
}

private fun Node.splitIfNecessary(): Boolean {
    if (this is PairNum) {
        if (left.splitIfNecessary() || right.splitIfNecessary()) return true
    }
    if (this is LiteralNum && value >= 10) {
        //println("Splitting $this")
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
            //println("Exploding $this")
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

//private fun Node.format(): String {
//    return formatInternal().lines.joinToString(separator = "\n")
//}
//
//private fun max(a: Int, b: Int, c: Int): Int {
//    return maxOf(maxOf(a, b), c)
//}
//
//private fun String.center(width: Int, fillChar: Char = ' ') : String {
//    var s = this
//    var left = true
//    while (s.length < width) {
//        if (left) {
//            s = fillChar + s
//        } else {
//            s += fillChar
//        }
//        left = !left
//    }
//    return s
//}
//
//private fun Node.formatInternal() : FormatParts {
//    var label = (this as? LiteralNum)?.value?.toString() ?: "."
//    val (leftLines, leftPos, leftWidth) = (this as? PairNum)?.left?.formatInternal() ?: FormatParts(mutableListOf(), 0, 0)
//    val (rightLines, rightPos, rightWidth) = (this as? PairNum)?.right?.formatInternal() ?: FormatParts(mutableListOf(), 0, 0)
//
//    // Ensure left and right lines are the same size by appending whitespace.
//    while (leftLines.size < rightLines.size) {
//        leftLines.add(" ".repeat(leftWidth))
//    }
//    while (rightLines.size < leftLines.size) {
//        rightLines.add(" ".repeat(rightWidth))
//    }
//
//    val middle = max(rightPos + leftWidth - leftPos + 1, label.length, 2)
//    val pos = leftPos + (middle / 2)
//    val width = leftPos + middle + rightWidth - rightPos
//
//    val lines = mutableListOf<String>()
//
//    // Pad the label with '.'s to match middle length
//    if ((middle - label.length) % 2 == 1
//        && parent != null
//        && this != (parent as PairNum).left
//        && label.length < middle) {
//        label += '.'
//    }
//    label = label.center(middle, '.')
//    if (label[0] == '.') {
//        label = ' ' + label.substring(1)
//    }
//    if (label[label.length - 1] == '.') {
//        label = label.substring(0, label.length - 1) + ' '
//    }
//
//    // Add the value
//    var valueLine = ""
//    valueLine += " ".repeat(leftPos)
//    valueLine += label
//    valueLine += " ".repeat(rightWidth - rightPos)
//    lines.add(valueLine)
//
//    // Add arrows when children exist.
//    var slashLine = ""
//    slashLine += " ".repeat(leftPos)
//    slashLine += if (this is PairNum) "/" else " "
//    slashLine += " ".repeat(middle - 2)
//    slashLine += if (this is PairNum) "\\" else " "
//    slashLine += " ".repeat(rightWidth - rightPos)
//    lines.add(slashLine)
//
//    // Add rest of the lines from the left and right children
//    (0 until leftLines.size).mapTo(lines) {
//        var result = leftLines[it]
//        result += " ".repeat(width - leftWidth - rightWidth)
//        result += rightLines[it]
//        result
//    }
//
//    return FormatParts(lines, pos, width)
//}
//
//private data class FormatParts(val lines: MutableList<String>, val pos: Int, val width: Int)
