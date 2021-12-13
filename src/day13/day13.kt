package day13

import java.io.File

fun main() {
    val (gridInput, foldsInput) = File("src/day13/input.txt").readText().split("\n\n")
    val grid = Grid(gridInput)
    val folds = foldsInput.lines().map { parseFold(it) }

    for ((axis, index) in folds) {
        grid.fold(axis, index)
    }

    println(grid.toString())
}

private class Grid(data: String) {
    private val grid: Array<BooleanArray>
    private var rows: Int
    private var cols: Int

    init {
        val coordinates = data.lines().map { line -> line.split(",").map { it.toInt() } }

        rows = coordinates.maxOf { it[1] } + 1
        cols = coordinates.maxOf { it[0] } + 1
        grid = Array(rows) { BooleanArray(cols) }

        for (coordinate in coordinates) {
            grid[coordinate[1]][coordinate[0]] = true
        }
    }

    fun fold(axis: Axis, index: Int) {
        when (axis) {
            Axis.Y -> {
                for (row in index until rows) {
                    val opposite = index - (row - index)
                    if (opposite >= 0) {
                        for (col in 0 until cols) {
                            grid[opposite][col] = grid[opposite][col] || grid[row][col]
                        }
                    }
                }
                rows = index
            }
            Axis.X -> {
                for (col in index until cols) {
                    val opposite = index - (col - index)
                    if (opposite >= 0) {
                        for (row in 0 until rows) {
                            grid[row][opposite] = grid[row][opposite] || grid[row][col]
                        }
                    }
                }
                cols = index
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                sb.append(if (grid[row][col]) '#' else '.')
            }
            sb.append('\n')
        }
        return sb.toString()
    }
}

private fun parseFold(data: String): Pair<Axis, Int> {
    val (axisInput, indexInput) = data.split("=")
    val axis = if (axisInput.last() == 'x') Axis.X else Axis.Y
    return axis to indexInput.toInt()
}

enum class Axis { X, Y }
