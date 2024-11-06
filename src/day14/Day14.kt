package day14

import util.Vector
import util.println
import util.readInput
import util.x
import util.y
import kotlin.time.measureTime

private const val folder = "day14"

private const val MAX_CYCLES = 1000000000

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    val currentBlockedRow = MutableList(input.first().length) { -1 }
    return input.mapIndexed { rowIndex, line ->
        var weight = 0
        line.forEachIndexed { columnIndex, field ->
            when (field) {
                '#' -> currentBlockedRow[columnIndex] = rowIndex
                'O' -> {
                    currentBlockedRow[columnIndex] += 1
                    weight += input.size - currentBlockedRow[columnIndex]
                }
            }
        }
        weight
    }.sum()
}

private fun part2(input: List<String>): Int {
    return input.toGrid()
        .doLotsOfCyclesSmart()
        .weight()
}

private fun Array<CharArray>.doLotsOfCyclesSmart(): Array<CharArray> {
    val (start, period) = findRepetition()
    val remainingCycles = (MAX_CYCLES - start) % period
    for (i in 1..remainingCycles) {
        this.performOneCycle()
    }
    return this
}

private fun Array<CharArray>.findRepetition(): Pair<Int, Int> {
    val history: MutableMap<String, Int> = mutableMapOf()
    for (i in 1..1000000000) {
        this.performOneCycle()
        history[this.asString()]?.let {
            return i to i - it
        }
        history[this.asString()] = i
    }
    throw IllegalArgumentException("No repetition found")
}

private fun Array<CharArray>.performOneCycle(): Array<CharArray> {
    return moveNorth()
        .moveWest()
        .moveSouth()
        .moveEast()
}

private fun Array<CharArray>.moveNorth(): Array<CharArray> {
    val currentBlockedRow = MutableList(first().size) { -1 }
    indices.forEach { rowIndex ->
        first().indices.forEach { columnIndex ->
            when (this[rowIndex][columnIndex]) {
                '#' -> currentBlockedRow[columnIndex] = rowIndex
                'O' -> {
                    currentBlockedRow[columnIndex] += 1
                    swap(columnIndex to rowIndex, columnIndex to currentBlockedRow[columnIndex])
                }
            }
        }
    }
    return this
}

private fun Array<CharArray>.moveWest(): Array<CharArray> {
    val currentBlockedColumn = MutableList(size) { -1 }
    first().indices.forEach { columnIndex ->
        indices.forEach { rowIndex ->
            when (this[rowIndex][columnIndex]) {
                '#' -> currentBlockedColumn[rowIndex] = columnIndex
                'O' -> {
                    currentBlockedColumn[rowIndex] += 1
                    swap(columnIndex to rowIndex, currentBlockedColumn[rowIndex] to rowIndex)
                }
            }
        }
    }
    return this
}

private fun Array<CharArray>.moveSouth(): Array<CharArray> {
    val currentBlockedRow = MutableList(first().size) { size }
    indices.reversed().forEach { rowIndex ->
        first().indices.forEach { columnIndex ->
            when (this[rowIndex][columnIndex]) {
                '#' -> currentBlockedRow[columnIndex] = rowIndex
                'O' -> {
                    currentBlockedRow[columnIndex] -= 1
                    swap(columnIndex to rowIndex, columnIndex to currentBlockedRow[columnIndex])
                }
            }
        }
    }
    return this
}

private fun Array<CharArray>.moveEast(): Array<CharArray> {
    val currentBlockedColumn = MutableList(size) { first().size }
    first().indices.reversed().forEach { columnIndex ->
        indices.forEach { rowIndex ->
            when (this[rowIndex][columnIndex]) {
                '#' -> currentBlockedColumn[rowIndex] = columnIndex
                'O' -> {
                    currentBlockedColumn[rowIndex] -= 1
                    swap(columnIndex to rowIndex, currentBlockedColumn[rowIndex] to rowIndex)
                }
            }
        }
    }
    return this
}

private fun Array<CharArray>.weight(): Int {
    return this.mapIndexed { index, row ->
        row.count { it == 'O' } * (size - index)
    }.sum()
}

private fun Array<CharArray>.swap(a: Vector, b: Vector): Array<CharArray> {
    val temp = this[a.y][a.x]
    this[a.y][a.x] = this[b.y][b.x]
    this[b.y][b.x] = temp
    return this
}

private fun List<String>.toGrid(): Array<CharArray> {
    return this.map { it.toCharArray() }
        .toTypedArray()
}

private fun Array<CharArray>.printPlatform() {
    forEach { it.joinToString(separator = "").println() }
    "".println()
}

private fun Array<CharArray>.asString(): String {
    return this.joinToString("") { it.joinToString("") }
}
