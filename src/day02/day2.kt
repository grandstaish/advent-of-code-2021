package day02

import java.io.File

fun main() {
    val commands = File("src/day02/input.txt").readLines()

    var x = 0
    var y = 0
    var aim = 0

    for (command in commands) {
        val (direction, distance) = command.split(" ")

        when (direction) {
            "forward" -> {
                x += distance.toInt()
                y += aim * distance.toInt()
            }
            "down" -> aim += distance.toInt()
            "up" -> aim -= distance.toInt()
        }
    }

    println(x * y)
}
