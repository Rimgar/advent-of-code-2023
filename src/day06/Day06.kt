package day06

import day06.CalculationType.Analytics
import day06.CalculationType.Loop
import util.println
import util.product
import util.readInput
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

private const val folder = "day06"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput, Analytics) == 288L)
    check(part2(testInput, Analytics) == 71503L)

    val input = readInput("$folder/input")
    part1(input, Analytics).println()
    part2(input, Analytics).println()
}

private fun part1(input: List<String>, type: CalculationType): Long {
    val (times, distanceRecords) = input.map { line -> line.split(" +".toRegex()).drop(1) }
    return when (type) {
        Analytics -> calculate(times, distanceRecords)
        Loop -> calculateWithLoop(times, distanceRecords)
    }
}

private fun part2(input: List<String>, type: CalculationType): Long {
    val (times, distanceRecords) = input.map { line ->
        line.split(":")[1]
            .replace(" ", "")
            .let { listOf(it) }
    }
    return when (type) {
        Analytics -> calculate(times, distanceRecords)
        Loop -> calculateWithLoop(times, distanceRecords)
    }
}

private fun calculate(times: List<String>, distanceRecords: List<String>): Long {
    // d: distance; t: race time; x: button time
    // d = (t-x)*x
    // pq: t/2 +- sqrt (t/2)^2 - d
    return times.map { it.toLong() }
        .zip(distanceRecords.map { it.toLong() })
        .map { (time, distanceRecord) ->
            val timeHalf = (time / 2.0)
            val root = sqrt(timeHalf.pow(2) - distanceRecord)
            (ceil(timeHalf + root) - floor(timeHalf - root) - 1).toLong()
        }.product()
}

private fun calculateWithLoop(times: List<String>, distanceRecords: List<String>): Long {
    // d: distance; t: race time; x: button time
    // d = (t-x)*x
    return times.map { it.toLong() }.zip(distanceRecords.map { it.toLong() })
        .map { (time, distanceRecord) ->
            (1 until time)
                .map { calculateDistance(time, it) }
                .count { it > distanceRecord }
                .toLong()
        }.product()
}

private fun calculateDistance(totalRaceTime: Long, buttonPressTime: Long): Long {
    return (totalRaceTime - buttonPressTime) * buttonPressTime
}

private enum class CalculationType {
    Analytics, Loop
}
