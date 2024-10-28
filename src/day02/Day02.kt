package day02

import util.println
import util.readInput

private const val folder = "day02"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("$folder/test")
    check(part1(testInput1) == 8)
    val testInput2 = readInput("$folder/test")
    check(part2(testInput2) == 2286)

    val input = readInput("${folder}/input")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    val allowedCubes = mapOf("red" to 12, "green" to 13, "blue" to 14)
    return input.sumOf { line ->
        val (game, drawnCubes) = line.split(": ")
        val maxReached = drawnCubes.split("; ", ", ").any {
            it.split(" ")
                .let { (count, color) ->
                    count.toInt() > allowedCubes.getValue(color)
                }
        }
        if (maxReached) {
            0
        } else {
            game.split(" ")[1].toInt()
        }
    }
}

private fun part2(input: List<String>): Int {
    return input.sumOf { line ->
        line.split(": ", "; ", ", ")
            .asSequence()
            .drop(1)
            .map {
                it.split(" ")
                    .let { (count, color) ->
                        color to count.toInt()
                    }
            }
            .groupBy { it.first }
            .map { (_, counts) ->
                counts.maxOf { it.second }
            }
            .reduce { power, count ->
                power * count
            }
    }
}
