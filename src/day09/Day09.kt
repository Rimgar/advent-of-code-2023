package day09

import util.println
import util.readInput

private const val folder = "day09"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("$folder/input")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    return calculate(input, true)
}

private fun part2(input: List<String>): Int {
    return calculate(input, false)
}

private fun calculate(input: List<String>, future: Boolean): Int {
    return input.sumOf { line ->
        line.split(" ")
            .map { it.toInt() }
            .predictValue(future = future)
    }
}

private fun List<Int>.predictValue(future: Boolean): Int {
    var currentList = this
    val lastValues = mutableListOf<Int>()
    while (currentList.any { it != 0 }) {
        lastValues.add(if (future) currentList.last() else currentList.first())
        currentList = currentList.calculateDifferences()
    }
    return if (future) {
        lastValues.foldRight(initial = 0) { value, acc ->
            (value + acc)
        }
    } else {
        lastValues.foldRight(initial = 0) { value, acc ->
            (value - acc)
        }
    }
}

private fun List<Int>.calculateDifferences(): List<Int> {
    return this.windowed(2)
        .map { (first, second) -> second - first }
}
