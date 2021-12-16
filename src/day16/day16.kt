package day16

import java.io.File

fun main() {
    val input = File("src/day16/input.txt").readText().toBinaryString()

    println(input.toBitsTransmission(0).compute())
}

private fun BitsTransmission.compute(): Long = when (this) {
    is Literal -> value
    is Operation -> {
        when (operator) {
            Operator.Sum -> children.sumOf { it.compute() }
            Operator.Product -> {
                var result = children[0].compute()
                for (i in 1 until children.size) {
                    result *= children[i].compute()
                }
                result
            }
            Operator.Min -> children.minOf { it.compute() }
            Operator.Max -> children.maxOf { it.compute() }
            Operator.GreaterThan -> if (children[0].compute() > children[1].compute()) 1 else 0
            Operator.LessThan -> if (children[0].compute() < children[1].compute()) 1 else 0
            Operator.EqualTo -> if (children[0].compute() == children[1].compute()) 1 else 0
        }
    }
}

private enum class Operator {
    Sum,
    Product,
    Min,
    Max,
    GreaterThan,
    LessThan,
    EqualTo
}

private sealed class BitsTransmission {
    abstract val version: Int
    abstract val contentLength: Int
}

private data class Literal(
    override val version: Int,
    override val contentLength: Int,
    val value: Long
) : BitsTransmission()

private data class Operation(
    override val version: Int,
    override val contentLength: Int,
    val operator: Operator,
    val children: List<BitsTransmission>
) : BitsTransmission()

private fun String.toBitsTransmission(offset: Int): BitsTransmission {
    val version = substring(offset, offset + 3).parseBinaryLong().toInt()
    return when (val typeId = substring(offset + 3, offset + 6)) {
        "100" -> {
            var i = offset + 6
            val sb = StringBuilder()
            do {
                sb.append(substring(i + 1, i + 5))
                i += 5
            } while (this[i - 5] == '1')
            Literal(version, i - offset, sb.toString().parseBinaryLong())
        }
        else -> {
            val operator = typeId.parseBinaryLong().toOperator()
            val children = mutableListOf<BitsTransmission>()
            var len = 0
            if (this[offset + 6] == '0') {
                while (len < substring(offset + 7, offset + 22).parseBinaryLong()) {
                    val child = toBitsTransmission(offset + 22 + len)
                    len += child.contentLength
                    children += child
                }
                len += 22
            } else {
                val childCount = substring(offset + 7, offset + 18).parseBinaryLong().toInt()
                repeat(childCount) {
                    val child = toBitsTransmission(offset + 18 + len)
                    len += child.contentLength
                    children += child
                }
                len += 18
            }
            Operation(version, len, operator, children)
        }
    }
}

private fun Long.toOperator(): Operator = when (this) {
    0L -> Operator.Sum
    1L -> Operator.Product
    2L -> Operator.Min
    3L -> Operator.Max
    5L -> Operator.GreaterThan
    6L -> Operator.LessThan
    7L -> Operator.EqualTo
    else -> throw IllegalArgumentException("Invalid operator: $this")
}

private fun String.parseBinaryLong(): Long {
    var result = 0L
    for (c in this) {
        result = (result shl 1) + (c - '0')
    }
    return result
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
