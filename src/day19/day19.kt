package day19

import java.io.File
import kotlin.math.abs

fun main() {
    val results = parse(File("src/day19/input.txt"))

    // Calculate all known offsets for each scanner to each other scanner.
    val offsets = mutableMapOf<Pair<Int, Int>, Pair<Int3, Int>>()
    for (i in results.indices) {
        for (j in results.indices) {
            if (i == j) continue
            val offset = findOffsetOrNull(results[i], results[j])
            if (offset != null) {
                offsets[i to j] = offset
            }
        }
    }

    // A graph that we can use to get distances from the 0th scanner.
    val graph = buildOffsetGraph(offsets)

    // Use the graph to align all points with the 0th scanner, then add them all to a set to remove duplicates.
//    val beacons = mutableSetOf<Int3>()
//    for (i in results.indices) {
//        beacons += graph.translate(i, results[i])
//    }

    // Find the largest manhattan distance
    var max = 0
    for (s1 in results.indices) {
        for (s2 in results.indices) {
            val d1 = graph.offsetToTarget(s1)
            val d2 = graph.offsetToTarget(s2)
            max = maxOf(max, abs(d1.x - d2.x) + abs(d1.y - d2.y) + abs(d1.z - d2.z))
        }
    }

//    println(beacons.size)
    println(max)
}

private fun findOffsetOrNull(a: Set<Int3>, b: Set<Int3>): Pair<Int3, Int>? {
    for (p0 in a) {
        // How do you iterate through the 24 perms properly?
        for (variant in 0..47) {
            for (p in b) {
                val p1 = p.variant(variant)
                val dist = Int3(p0.x - p1.x, p0.y - p1.y, p0.z - p1.z)
                val offsetPoints = b.map { it.variant(variant) + dist }.toSet()
                if (offsetPoints.intersect(a).size >= 12) {
                    return dist to variant
                }
            }
        }
    }

    return null
}

private fun parse(file: File): List<Set<Int3>> {
    return file.readText()
        .split("\n\n")
        .map { scannerBlob ->
            scannerBlob.lines().drop(1)
                .map { it.split(',') }
                .map { (x, y, z) -> Int3(x.toInt(), y.toInt(), z.toInt()) }
                .toSet()
        }
}

private fun buildOffsetGraph(offsets: Map<Pair<Int, Int>, Pair<Int3, Int>>): Node {
    val nodes = mutableMapOf<Int, Node>()

    for ((k, v) in offsets.entries) {
        val (from, to) = k
        val (vector, variant) = v
        if (nodes[from] == null) nodes[from] = Node(from)
        if (nodes[to] == null) nodes[to] = Node(to)
        nodes[from]!!.addIfMissing(nodes[to]!!, vector, variant)
    }

    return nodes[0]!!
}

private data class Node(private val value: Int) {
    private val children = mutableListOf<Triple<Node, Int3, Int>>()

    fun addIfMissing(node: Node, vector: Int3, variant: Int) {
        val existing = children.find { (n, _) -> n.value == node.value }
        if (existing == null) {
            children += Triple(node, vector, variant)
        }
    }

//    fun translate(target: Int, points: Set<Int3>) = translate(target, points, mutableSetOf())!!
//
//    private fun translate(target: Int, points: Set<Int3>, visited: MutableSet<Int>): Set<Int3>? {
//        if (value == target) return points
//        if (value in visited) return null
//        visited.add(value)
//
//        for ((c, vec, variant) in children) {
//            val ans = c.translate(target, points, visited)
//            if (ans != null) return ans.map { it.variant(variant) + vec }.toSet()
//        }
//
//        return null
//    }

    fun offsetToTarget(target: Int): Int3 = offsetToTarget(target, mutableSetOf())!!

    private fun offsetToTarget(target: Int, visited: MutableSet<Int>): Int3? {
        if (value == target) return Int3(0, 0, 0)
        if (value in visited) return null
        visited.add(value)

        for ((c, vec, variant) in children) {
            val ans = c.offsetToTarget(target, visited)
            if (ans != null) return ans.variant(variant) + vec
        }

        return null
    }
}

private data class Int3(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Int3) = Int3(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Int3) = Int3(x - other.x, y - other.y, z - other.z)

    fun variant(variant: Int) = when (variant) {
        0 -> Int3(x, y, z)
        1 -> Int3(-x, y, z)
        2 -> Int3(-x, -y, z)
        3 -> Int3(-x, -y, -z)
        4 -> Int3(x, -y, z)
        5 -> Int3(x, -y, -z)
        6 -> Int3(x, y, -z)
        7 -> Int3(-x, y, -z)

        8 -> Int3(y, x, z)
        9 -> Int3(-y, x, z)
        10 -> Int3(-y, -x, z)
        11 -> Int3(-y, -x, -z)
        12 -> Int3(y, -x, z)
        13 -> Int3(y, -x, -z)
        14 -> Int3(y, x, -z)
        15 -> Int3(-y, x, -z)

        16 -> Int3(z, y, x)
        17 -> Int3(-z, y, x)
        18 -> Int3(-z, -y, x)
        19 -> Int3(-z, -y, -x)
        20 -> Int3(z, -y, x)
        21 -> Int3(z, -y, -x)
        22 -> Int3(z, y, -x)
        23 -> Int3(-z, y, -x)

        24 -> Int3(x, z, y)
        25 -> Int3(-x, z, y)
        26 -> Int3(-x, -z, y)
        27 -> Int3(-x, -z, -y)
        28 -> Int3(x, -z, y)
        29 -> Int3(x, -z, -y)
        30 -> Int3(x, z, -y)
        31 -> Int3(-x, z, -y)

        32 -> Int3(y, z, x)
        33 -> Int3(-y, z, x)
        34 -> Int3(-y, -z, x)
        35 -> Int3(-y, -z, -x)
        36 -> Int3(y, -z, x)
        37 -> Int3(y, -z, -x)
        38 -> Int3(y, z, -x)
        39 -> Int3(-y, z, -x)

        40 -> Int3(z, x, y)
        41 -> Int3(-z, x, y)
        42 -> Int3(-z, -x, y)
        43 -> Int3(-z, -x, -y)
        44 -> Int3(z, -x, y)
        45 -> Int3(z, -x, -y)
        46 -> Int3(z, x, -y)
        47 -> Int3(-z, x, -y)

        else -> throw IllegalArgumentException()
    }

    override fun toString() = "($x,$y,$z)"
}
