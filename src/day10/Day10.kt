package day10

import println
import readInput
import replace
import util.Vector
import util.plus
import util.unaryMinus

private const val folder = "day10"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    val testInput2 = readInput("$folder/test2")
    val testInput3 = readInput("$folder/test3")
    val testInput4 = readInput("$folder/test4")
    check(part1(testInput) == 4)
    check(part1(testInput2) == 4)
    check(part1(testInput3) == 8)
    check(part1(testInput4) == 8)

    val testInput5 = readInput("$folder/test5")
    val testInput6 = readInput("$folder/test6")
    val testInput7 = readInput("$folder/test7")
    val testInput8 = readInput("$folder/test8")
//    check(part2(testInput) == 1)
    check(part2(testInput2) == 1)
    check(part2(testInput3) == 1)
    check(part2(testInput4) == 1)
    check(part2(testInput5) == 4)
    check(part2(testInput6) == 4)
    check(part2(testInput7) == 8)
    check(part2(testInput8) == 10)

    val input = readInput("$folder/input")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    val start = findStart(input)
    var direction = findConnectedPipes(input, start).first()
    var currentField = start + direction
    var count = 1
    while (currentField != start) {
        direction = findDirection(input[currentField], -direction).first
        currentField += direction
        count++
    }

    return count / 2
}

private fun part2(input: List<String>): Int {
    val start = findStart(input)
    val (marker, startDirection) = createMarkerAndStartDirection(input, start)
    return findEnclosedTiles(input, marker, start, startDirection)
}

private fun findStart(input: List<String>): Vector {
    input.forEachIndexed { row, line ->
        line.forEachIndexed { column, c ->
            if (c == 'S') return Vector(column, row)
        }
    }
    throw IllegalArgumentException("input has no start")
}

private fun findConnectedPipes(input: List<String>, start: Vector): List<Vector> {
    val directions = listOf(Vector(1, 0), Vector(0, 1), Vector(-1, 0), Vector(0, -1))
    return directions.mapNotNull { direction ->
        try {
            findDirection(input[start + direction], -direction)
            direction
        } catch (e: Exception) {
            null
        }
    }
}

private fun findDirection(field: Char, enteringFrom: Vector): Pair<Vector, Char> {
    val directions = when (field) {
        '|' -> mapOf(Vector(0, -1) to (Vector(0, 1) to 'S'), Vector(0, 1) to (Vector(0, -1) to 'S'))
        '-' -> mapOf(Vector(-1, 0) to (Vector(1, 0) to 'S'), Vector(1, 0) to (Vector(-1, 0) to 'S'))
        'L' -> mapOf(Vector(0, -1) to (Vector(1, 0) to 'L'), Vector(1, 0) to (Vector(0, -1) to 'R'))
        'J' -> mapOf(Vector(0, -1) to (Vector(-1, 0) to 'R'), Vector(-1, 0) to (Vector(0, -1) to 'L'))
        '7' -> mapOf(Vector(0, 1) to (Vector(-1, 0) to 'L'), Vector(-1, 0) to (Vector(0, 1) to 'R'))
        'F' -> mapOf(Vector(0, 1) to (Vector(1, 0) to 'R'), Vector(1, 0) to (Vector(0, 1) to 'L'))
        else -> throw IllegalArgumentException("Unknown char $field")
    }
    return directions[enteringFrom]
        ?: throw IllegalArgumentException("Entering $field from wrong direction $enteringFrom")
}

private fun createMarkerAndStartDirection(input: List<String>, start: Vector): Pair<MutableList<String>, Vector> {
    val directions = findConnectedPipes(input, start)
    var direction = directions.first()
    var currentField = start + direction
    var right = 0
    val marker = MutableList(input.size) { input[it] }
    marker[start.second] = marker[start.second].replace(start.first, 'P')
    while (currentField != start) {
        marker[currentField.second] = marker[currentField.second].replace(currentField.first, 'P')
        val directionTurn = findDirection(input[currentField], -direction)
        direction = directionTurn.first
        when (directionTurn.second) {
            'R' -> right++
            'L' -> right--
        }
        currentField += direction
    }
    val startDirection = if (right > 0) directions.first() else directions[1]
    return marker to startDirection
}

private fun findEnclosedTiles(input: List<String>, marker: MutableList<String>, start: Vector, startDirection: Vector): Int {
    var direction = startDirection
    var currentField = start + direction
    while (currentField != start) {
        val queue = ArrayDeque(
            findTilesToRight(input[currentField], -direction).map { neighbor ->
                currentField + neighbor
            }
        )
        while (queue.isNotEmpty()) {
            val pop = queue.removeFirst()
            try {
                if (marker[pop] !in listOf('P', 'I')) {
                    marker[pop.second] = marker[pop.second].replace(pop.first, 'I')
                    queue.addLast(pop + Vector(1, 0))
                    queue.addLast(pop + Vector(0, 1))
                    queue.addLast(pop + Vector(-1, 0))
                    queue.addLast(pop + Vector(0, -1))
                }
            } catch (e: Exception) {
                // DO NOTHING
            }
        }

        direction = findDirection(input[currentField], -direction).first
        currentField += direction
    }
    return marker.sumOf { line -> line.count { it == 'I' } }
}

private fun findTilesToRight(tile: Char, enteringFrom: Vector): List<Vector> {
    val directions = when (tile) {
        '|' -> mapOf(Vector(0, -1) to listOf(Vector(-1, 0)), Vector(0, 1) to listOf(Vector(1, 0)))
        '-' -> mapOf(Vector(-1, 0) to listOf(Vector(0, 1)), Vector(1, 0) to listOf(Vector(0, -1)))
        'L' -> mapOf(Vector(0, -1) to listOf(Vector(-1, 0), Vector(0, 1)), Vector(1, 0) to listOf())
        'J' -> mapOf(Vector(0, -1) to listOf(), Vector(-1, 0) to listOf(Vector(1, 0), Vector(0, 1)))
        '7' -> mapOf(Vector(0, 1) to listOf(Vector(1, 0), Vector(0, -1)), Vector(-1, 0) to listOf())
        'F' -> mapOf(Vector(0, 1) to listOf(), Vector(1, 0) to listOf(Vector(0, -1), Vector(-1, 0)))
        else -> throw IllegalArgumentException("Unknown char $tile")
    }
    return directions[enteringFrom]
        ?: throw IllegalArgumentException("Entering $tile from wrong direction $enteringFrom")
}

private fun List<String>.prettyPrint() {
    joinToString(separator = "\n") { line ->
        line.replace('-', '━')
            .replace('|', '┃')
            .replace('F', '┏')
            .replace('7', '┓')
            .replace('L', '┗')
            .replace('J', '┛')
            .replace("S", "\u001b[93mS\u001b[0m")
    }.println()
}

private operator fun List<String>.get(p: Vector) = this[p.second][p.first]

