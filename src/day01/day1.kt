import java.io.File

fun main() {
    val nums = File("src/day01/input.txt").readLines().map { it.toInt() }

    var count = 0
    var prev = Int.MAX_VALUE

    for (i in 2 until nums.size) {
        val sum = nums[i] + nums[i-1] + nums[i-2]
        if (prev < sum) {
            count++
        }
        prev = sum
    }

    println(count)
}
