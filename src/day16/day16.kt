package day16

import java.io.File

fun main() {
    println(parse(File("src/day16/input.txt")).compute())
}

private fun parse(input: File): BitsTransmission {
    val bits = input.readText().toBinaryString()

    var current = 0

    fun read(length: Int): String {
        val result = bits.substring(current, current + length)
        current += length
        return result
    }

    fun readBitsTransmission(): BitsTransmission {
        val version = read(3).toInt(radix = 2)
        val typeId = read(3).toInt(radix = 2)
        return when (typeId) {
            4 -> {
                val sb = StringBuilder()
                do {
                    val c = read(1)
                    sb.append(read(4))
                } while (c == "1")
                Literal(version, value = sb.toString().toLong(radix = 2))
            }
            else -> {
                val children = mutableListOf<BitsTransmission>()
                if (read(1) == "0") {
                    val childLength = read(15).toInt(radix = 2)
                    val end = current + childLength
                    while (current < end) {
                        children += readBitsTransmission()
                    }
                } else {
                    val childCount = read(11).toInt(radix = 2)
                    repeat(childCount) {
                        children += readBitsTransmission()
                    }
                }
                Operation(version, typeId.toOperator(), children)
            }
        }
    }

    return readBitsTransmission()
}

private fun BitsTransmission.compute(): Long = when (this) {
    is Literal -> value
    is Operation -> {
        when (operator) {
            Operator.Sum -> children.sumOf { it.compute() }
            Operator.Product -> children.fold(1L) { acc, it -> acc * it.compute() }
            Operator.Min -> children.minOf { it.compute() }
            Operator.Max -> children.maxOf { it.compute() }
            Operator.GreaterThan -> if (children[0].compute() > children[1].compute()) 1 else 0
            Operator.LessThan -> if (children[0].compute() < children[1].compute()) 1 else 0
            Operator.EqualTo -> if (children[0].compute() == children[1].compute()) 1 else 0
        }
    }
}

private fun String.toBinaryString(): String {
    val sb = StringBuilder()
    for (i in indices) {
        sb.append(this[i].toBinaryString())
    }
    return sb.toString()
}

private fun Char.toBinaryString() = when (this) {
    '0' -> "0000"
    '1' -> "0001"
    '2' -> "0010"
    '3' -> "0011"
    '4' -> "0100"
    '5' -> "0101"
    '6' -> "0110"
    '7' -> "0111"
    '8' -> "1000"
    '9' -> "1001"
    'A' -> "1010"
    'B' -> "1011"
    'C' -> "1100"
    'D' -> "1101"
    'E' -> "1110"
    'F' -> "1111"
    else -> throw IllegalArgumentException("Invalid hex character: $this")
}

private fun Int.toOperator(): Operator = when (this) {
    0 -> Operator.Sum
    1 -> Operator.Product
    2 -> Operator.Min
    3 -> Operator.Max
    5 -> Operator.GreaterThan
    6 -> Operator.LessThan
    7 -> Operator.EqualTo
    else -> throw IllegalArgumentException("Invalid operator: $this")
}

private enum class Operator {
    Sum, Product, Min, Max, GreaterThan, LessThan, EqualTo
}

private sealed class BitsTransmission {
    abstract val version: Int
}

private data class Literal(
    override val version: Int,
    val value: Long
) : BitsTransmission()

private data class Operation(
    override val version: Int,
    val operator: Operator,
    val children: List<BitsTransmission>
) : BitsTransmission()
