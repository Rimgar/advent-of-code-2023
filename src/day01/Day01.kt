package day01

import util.println
import util.readInput

private const val folder = "day01"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("$folder/test")
    check(part1(testInput1) == 142)
    val testInput2 = readInput("$folder/test_2")
    check(part2(testInput2) == 281)

    val input = readInput("$folder/input")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    return input.sumOf { line ->
        "${line.first { it.isDigit() }}${line.last { it.isDigit() }}"
            .toInt()
    }
}

private fun part2(input: List<String>): Int {
    val digits = listOf(
        "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    )
    return input.sumOf { line ->
        val firstDigit = line.findAnyOf(digits)!!.second.toDigit()
        val lastDigit = line.findLastAnyOf(digits)!!.second.toDigit()
        "$firstDigit$lastDigit".toInt()
    }
}

private fun String.toDigit(): String {
    return when (this) {
        "one" -> "1"
        "two" -> "2"
        "three" -> "3"
        "four" -> "4"
        "five" -> "5"
        "six" -> "6"
        "seven" -> "7"
        "eight" -> "8"
        "nine" -> "9"
        else -> this
    }
}
