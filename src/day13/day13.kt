package day13

import java.io.File

fun main() {
    val (gridInput, foldsInput) = File("src/day13/input.txt").readText().split("\n\n")
    val grid = Grid(gridInput)
    val folds = foldsInput.lines().map { Fold.parse(it) }

    for (fold in folds) {
        grid.fold(fold)
    }

    grid.prettyPrint()
}

class Grid(data: String) {
    private val grid: Array<BooleanArray>
    private var rows: Int
    private var cols: Int

    init {
        val coordinates = data.lines().map { line -> line.split(",").map { it.toInt() } }
        rows = coordinates.maxOf { it[1] } + 1
        cols = coordinates.maxOf { it[0] } + 1

        grid = Array(rows) { y ->
            BooleanArray(cols) { x ->
                coordinates.any { it[0] == x && it[1] == y }
            }
        }
    }

    fun fold(fold: Fold) {
        when (fold.axis) {
            Axis.Y -> {
                for (row in fold.index until rows) {
                    val opposite = fold.index - (row - fold.index)
                    if (opposite >= 0) {
                        for (col in 0 until cols) {
                            grid[opposite][col] = grid[opposite][col] || grid[row][col]
                        }
                    }
                }
                rows = fold.index
            }
            Axis.X -> {
                for (col in fold.index until cols) {
                    val opposite = fold.index - (col - fold.index)
                    if (opposite >= 0) {
                        for (row in 0 until rows) {
                            grid[row][opposite] = grid[row][opposite] || grid[row][col]
                        }
                    }
                }
                cols = fold.index
            }
        }
    }

    fun prettyPrint() {
        val sb = StringBuilder()
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                sb.append(if (grid[row][col]) '#' else '.')
            }
            sb.append('\n')
        }
        println(sb.toString())
    }
}

data class Fold(val axis: Axis, val index: Int) {
    companion object {
        fun parse(data: String): Fold {
            val (axisInput, indexInput) = data.split("=")
            val axis = if (axisInput.last() == 'x') Axis.X else Axis.Y
            return Fold(axis, indexInput.toInt())
        }
    }
}

enum class Axis { X, Y }
