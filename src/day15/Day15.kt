package day15

import util.println
import util.readInput
import kotlin.time.measureTime

private const val folder = "day15"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check("HASH".hash() == 52)
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    return input.joinToString("")
        .split(',')
        .sumOf { it.hash() }
}

private fun part2(input: List<String>): Int {
    val boxes = mutableMapOf<Int, MutableList<Pair<String, Int>>>().withDefault { mutableListOf() }
    input.joinToString("")
        .split(',')
        .forEach { step ->
            """([A-Za-z]+)([=-])(\d*)""".toRegex().matchEntire(step)
                ?.let { matchResult ->
                    val label = matchResult.groupValues[1]
                    val labelHash = label.hash()
                    val operation = matchResult.groupValues[2]
                    when (operation) {
                        "-" -> {
                            boxes.getValue(labelHash).removeIf { it.first == label }
                        }
                        "=" -> {
                            val focalLength = matchResult.groupValues[3].toInt()
                            val box = boxes.getValue(labelHash)
                            boxes[labelHash] = box
                            val index = box.indexOfFirst { it.first == label }
                            if (index >= 0) {
                                box[index] = label to focalLength
                            } else {
                                box.add(label to focalLength)
                            }
                        }
                        else -> {}
                    }
                }
                ?: throw NullPointerException()
        }
    return boxes.toSortedMap()
        .entries
        .sumOf { (key, box) ->
            (key + 1) * box.mapIndexed { index, (_, focalLength) ->
                (index + 1) * focalLength
            }.sum()
        }
}

private fun String.hash(): Int {
    var currentValue = 0
    forEach { c ->
        currentValue += c.code
        currentValue *= 17
        currentValue %= 256
    }

    return currentValue
}
