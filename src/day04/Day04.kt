package day04

import println
import readInput
import kotlin.math.min
import kotlin.math.pow

private const val folder = "day04"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("$folder/test")
    check(part1(testInput1) == 13)
    val testInput2 = readInput("$folder/test")
    check(part2(testInput2) == 30)

    val input = readInput("${folder}/input")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    return calculateWinningCounts(input).sumOf { count ->
        if (count > 0) {
            2.0.pow(count - 1).toInt()
        } else {
            0
        }
    }
}

private fun part2(input: List<String>): Int {
    val counts = calculateWinningCounts(input)
    val copies = MutableList(input.size) { 1 }
    counts.forEachIndexed { index, count ->
        for(i in 1..min(count, counts.size - index - 1)) {
            copies[index + i] += copies[index]
        }
    }
    return copies.sum()
}

private fun calculateWinningCounts(input: List<String>): List<Int> {
    return input.map { line ->
        val (winningNumbers, numbersYouHave) = line.split(": ", " | ")
            .drop(1)
            .map { it.trim().split(""" +""".toRegex()).map { number -> number.toInt() }.toSet() }
        winningNumbers.intersect(numbersYouHave).size
    }
}
