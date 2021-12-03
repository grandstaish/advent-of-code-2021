import RatingStrategy.CO2Scrubber
import RatingStrategy.O2Generator
import java.io.File

fun main() {
    val report = File("src/day03/input.txt").readLines()

    val tree = Tree()
    tree.parse(report)

    println(tree.findRating(O2Generator) * tree.findRating(CO2Scrubber))
}

class Tree {
    private val root = Node(-1)

    fun parse(report: List<String>) {
        for (line in report) {
            var curr = root
            for (c in line) {
                curr = curr.put(c - '0')
            }
        }
    }

    fun findRating(strategy: RatingStrategy): Int{
        var rating = 0
        var curr = strategy.next(root)

        while (curr != null) {
            rating = curr.value or (rating shl 1)
            curr = strategy.next(curr)
        }

        return rating
    }
}

enum class RatingStrategy {
    O2Generator,
    CO2Scrubber;

    fun next(node: Node): Node? {
        val one = node[1] ?: return node[0]
        val zero = node[0] ?: return node[1]

        return when (this) {
            O2Generator -> if (one.frequency >= zero.frequency) one else zero
            CO2Scrubber -> if (one.frequency < zero.frequency) one else zero
        }
    }
}

class Node(val value: Int) {
    private val children = Array<Node?>(2) { null }
    var frequency = 0
        private set

    operator fun get(value: Int) = children[value]

    fun put(value: Int): Node {
        val child = children[value] ?: Node(value).also {
            children[value] = it
        }
        child.frequency++
        return child
    }
}
