package day11

import util.println
import util.readInput
import util.Vector
import util.cityBlockDistance

private const val folder = "day11"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 374)
    check(part2(testInput,10) == 1030L)
    check(part2(testInput,100) == 8410L)

    val input = readInput("$folder/input")
    part1(input).println()
    part2(input, 1000000).println()
}

private fun part1(input: List<String>): Int {
    val expandedInput = input.addRows().addColumns()
    val coordinates = expandedInput
        .flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c -> if (c == '#') Vector(x, y) else null }
        }
    return coordinates.withIndex()
        .sumOf { (index1, vector1) ->
            (0 until index1).sumOf { index2 ->
                coordinates[index2].cityBlockDistance(vector1)
            }
        }
}

private fun List<String>.addRows(): List<String> {
    val columnCount = first().length
    val output = toMutableList()
    (size - 1 downTo 0).forEach { index ->
        if (this[index].all { it == '.' }) output.add(index, ".".repeat(columnCount))
    }
    return output
}

private fun List<String>.addColumns(): List<String> {
    val columnCount = first().length
    val output = toMutableList()
    (columnCount - 1 downTo 0).forEach { index ->
        if (map { line -> line[index] }.all { it == '.' }) {
            output.replaceAll { line -> "${line.take(index)}.${line.drop(index)}" }
        }
    }
    return output
}

private fun part2(input: List<String>, expansionFactor: Long): Long {
    val emptyRows = getEmptyRows(input)
    val emptyColumns = getEmptyColumns(input)
    val coordinates = input
        .flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c -> if (c == '#') Vector(x, y) else null }
        }
    return coordinates.withIndex()
        .sumOf { (index1, vector1) ->
            (0 until index1).sumOf { index2 ->
                val vector2 = coordinates[index2]
                vector2.cityBlockDistance(vector1) +
                        calculateExpandedSpaces(listOf(vector1.first, vector2.first), emptyColumns, expansionFactor) +
                        calculateExpandedSpaces(listOf(vector1.second, vector2.second), emptyRows, expansionFactor)
            }
        }
}

private fun getEmptyRows(input: List<String>): List<Int> {
    return input.mapIndexedNotNull { index, line ->
        if (line.all { it == '.' }) index else null
    }
}

private fun getEmptyColumns(input: List<String>): List<Int> {
    return (0..<input.first().length)
        .mapNotNull { index ->
            if (input.all { line -> line[index] == '.' }) index else null
        }
}

private fun calculateExpandedSpaces(components: List<Int>, expandedIndices: List<Int>, expansionFactor: Long): Long {
    val (comp1, comp2) = components.sorted()
    return expandedIndices.count { it in (comp1)..<comp2 } * (expansionFactor - 1)
}
