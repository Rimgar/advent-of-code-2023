package day16

import day16.Direction.Down
import day16.Direction.Left
import day16.Direction.Right
import day16.Direction.Up
import util.Vector
import util.get
import util.plus
import util.println
import util.readInput
import kotlin.time.measureTime

private const val folder = "day16"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    return countEnergizedFields(input, Vector(0, 0), Right)
}

private fun part2(input: List<String>): Int {
    return getStartingPoints(input).maxOf { (startingVector, startingDirection) ->
        countEnergizedFields(input, startingVector, startingDirection)
    }
}

private fun getStartingPoints(input: List<String>): List<Pair<Vector, Direction>> {
    val rowCount = input.size
    val columnCount = input.first().length

    return (0..<rowCount).flatMap {
        listOf(
            Vector(0, it) to Right,
            Vector(columnCount - 1, it) to Left
        )
    } + (0..<columnCount).flatMap {
        listOf(
            Vector(it, 0) to Down,
            Vector(it, rowCount -1) to Up
        )
    }
}

private fun countEnergizedFields(input: List<String>, startingVector: Vector, startingDirection: Direction): Int {
    // queue contains the coordinates of the field about to be entered and the direction of enter
    val queue = ArrayDeque<Pair<Vector, Direction>>()
    queue.addLast(startingVector to startingDirection)
    val energizedFields = mutableSetOf<Pair<Vector, Direction>>()

    while (queue.isNotEmpty()) {
        val (currentCoordinates, direction) = queue.removeFirst()
        try {
            val currentField = input[currentCoordinates]
            energizedFields.add(currentCoordinates to direction)
            getNextDirections(currentField, direction).forEach { newDirection ->
                val element = currentCoordinates + newDirection.vector to newDirection
                if (element !in energizedFields) queue.addLast(element)
            }
        } catch (e: IndexOutOfBoundsException) {
            // Do nothing
        } catch (e: StringIndexOutOfBoundsException) {
            // Do nothing
        }
    }
    return energizedFields.map { it.first }.toSet().size
}

private fun getNextDirections(field: Char, direction: Direction): List<Direction> {
    return when (field) {
        '.' -> listOf(direction)
        '/' -> {
            when (direction) {
                Up -> listOf(Right)
                Right -> listOf(Up)
                Down -> listOf(Left)
                Left -> listOf(Down)
            }
        }
        '\\' -> {
            when (direction) {
                Up -> listOf(Left)
                Right -> listOf(Down)
                Down -> listOf(Right)
                Left -> listOf(Up)
            }
        }
        '|' -> {
            when (direction) {
                Right, Left -> listOf(Up, Down)
                else -> listOf(direction)
            }
        }
        '-' -> {
            when (direction) {
                Up, Down -> listOf(Right, Left)
                else -> listOf(direction)
            }
        }
        else -> throw IllegalArgumentException("Unknown field $field")
    }
}

private enum class Direction(val vector: Vector) {
    Up(Vector(0, -1)),
    Right(Vector(1, 0)),
    Down(Vector(0, 1)),
    Left(Vector(-1, 0))
}
