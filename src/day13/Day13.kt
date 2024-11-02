package day13

import util.println
import util.readInput
import util.split
import kotlin.math.min

private const val folder = "day13"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInput("$folder/input")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    return input.split { it.isEmpty() }
        .sumOf { pattern ->
            findHorizontalReflection(pattern)?.let { it * 100 }
                ?: findVerticalReflection(pattern)
                ?: 0 // should not happen
        }
}

private fun findHorizontalReflection(pattern: List<String>): Int? {
    (1 until pattern.size).forEach { splittingIndex ->
        val sizeOfReflection = min(splittingIndex, pattern.size - splittingIndex)
        (0 until sizeOfReflection).all { runningIndex ->
            pattern[splittingIndex - runningIndex - 1] == pattern[splittingIndex + runningIndex]
        }.let { isReflection ->
            if (isReflection) return splittingIndex
        }
    }
    return null
}

private fun findVerticalReflection(pattern: List<String>): Int? {
    val patternSize = pattern.first().length
    (1 until patternSize).forEach { splittingIndex ->
        val sizeOfReflection = min(splittingIndex, patternSize - splittingIndex)
        (0 until sizeOfReflection).all { runningIndex ->
            pattern.map { it[splittingIndex - runningIndex - 1] } == pattern.map { it[splittingIndex + runningIndex] }
        }.let { isReflection ->
            if (isReflection) return splittingIndex
        }
    }
    return null
}

private fun part2(input: List<String>): Int {
    return input.split { it.isEmpty() }
        .sumOf { pattern ->
            findHorizontalReflection(pattern, 1)?.let { it * 100 }
                ?: findVerticalReflection(pattern,1)
                ?: 0 // should not happen
        }
}

private fun findHorizontalReflection(pattern: List<String>, requiredHammingDistance: Int): Int? {
    (1 until pattern.size).forEach { splittingIndex ->
        val sizeOfReflection = min(splittingIndex, pattern.size - splittingIndex)
        (0 until sizeOfReflection).sumOf { runningIndex ->
            hammingDistance(pattern[splittingIndex - runningIndex - 1], pattern[splittingIndex + runningIndex])
        }.let { distance ->
            if (distance == requiredHammingDistance) return splittingIndex
        }
    }
    return null
}

private fun findVerticalReflection(pattern: List<String>, requiredHammingDistance: Int): Int? {
    val patternSize = pattern.first().length
    (1 until patternSize).forEach { splittingIndex ->
        val sizeOfReflection = min(splittingIndex, patternSize - splittingIndex)
        (0 until sizeOfReflection).sumOf { runningIndex ->
            hammingDistance(
                pattern.map { it[splittingIndex - runningIndex - 1] }.joinToString(separator = ""),
                pattern.map { it[splittingIndex + runningIndex]}.joinToString(separator = "")
            )
        }.let { distance ->
            if (distance == requiredHammingDistance) return splittingIndex
        }
    }
    return null
}

private fun hammingDistance(s1: String, s2: String): Int {
    // Length check not necessary here
    return s1.indices.map { i ->
        if (s1[i] == s2[i]) 0 else 1
    }.sum()
}
