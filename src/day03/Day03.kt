package day03

import util.println
import util.readInput

private const val folder = "day03"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("$folder/test")
    check(part1(testInput1) == 4361)
    val testInput2 = readInput("$folder/test")
    check(part2(testInput2) == 467835)

    val input = readInput("${folder}/input")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    var isPartNumber = false
    var firstIndex: Int? = null
    var sum = 0
    input.forEachIndexed { row, line ->
        line.forEachIndexed { column, c ->
            if (c.isDigit()) {
                firstIndex = firstIndex ?: column
                isPartNumber = isPartNumber || isPartNumber(input, row, column)
            } else {
                if (isPartNumber) {
                    sum += firstIndex?.let { line.substring(it, column).toInt() } ?: 0
                }
                isPartNumber = false
                firstIndex = null
            }
        }
        if (isPartNumber) {
            sum += firstIndex?.let { line.substring(it, line.length).toInt() } ?: 0
        }
        isPartNumber = false
        firstIndex = null
    }
    return sum
}

fun isPartNumber(input: List<String>, row: Int, column: Int): Boolean {
    val startRow = if (row > 0) row - 1 else row
    val endRow = if (row < input.size - 1) row + 1 else row
    val startCol = if (column > 0) column - 1 else column
    val endCol = if (column < input.first().length - 1) column + 1 else column
    return input.subList(startRow, endRow + 1)
        .joinToString(separator = "") { line -> line.substring(startCol, endCol + 1) }
        .any { it.isSymbol() }
}

private fun Char.isSymbol(): Boolean {
    return !(isDigit() || this == '.')
}

private fun part2(input: List<String>): Int {
    val partToNumber = mutableMapOf<Pair<Int, Int>, List<Int>>().withDefault { emptyList() }
    val adjacentParts: MutableSet<Pair<Int, Int>> = mutableSetOf()
    var firstIndex: Int? = null
    input.forEachIndexed { row, line ->
        line.forEachIndexed { column, c ->
            if (c.isDigit()) {
                firstIndex = firstIndex ?: column
                adjacentParts.addAll(findAdjacentParts(input, row, column))
            } else {
                if (adjacentParts.isNotEmpty() && firstIndex != null) {
                    val partNumber = line.substring(firstIndex!!, column).toInt()
                    adjacentParts.forEach { coordinates ->
                        partToNumber[coordinates] = partToNumber.getValue(coordinates).plus(partNumber)
                    }
                }
                adjacentParts.clear()
                firstIndex = null
            }
        }
        if (adjacentParts.isNotEmpty() && firstIndex != null) {
            val partNumber = line.substring(firstIndex!!, line.length).toInt()
            adjacentParts.forEach { coordinates ->
                partToNumber[coordinates] = partToNumber.getValue(coordinates).plus(partNumber)
            }
        }
        adjacentParts.clear()
        firstIndex = null
    }
    return partToNumber.entries.sumOf { (coordinates, partNumbers) ->
        val (row, column) = coordinates
        if (partNumbers.size == 2 && input[row][column] == '*') {
            partNumbers.reduce { acc, i -> acc * i }
        } else {
            0
        }
    }
}

fun findAdjacentParts(input: List<String>, row: Int, column: Int): Set<Pair<Int, Int>> {
    val adjacentParts = mutableSetOf<Pair<Int, Int>>()
    val startRow = if (row > 0) row - 1 else row
    val endRow = if (row < input.size - 1) row + 1 else row
    val startCol = if (column > 0) column - 1 else column
    val endCol = if (column < input.first().length - 1) column + 1 else column
    for (i in startRow..endRow) {
        for (j in startCol..endCol) {
            if (input[i][j].isSymbol()) {
                adjacentParts.add(i to j)
            }
        }
    }
    return adjacentParts
}
