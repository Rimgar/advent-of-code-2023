package day05

import println
import readInput
import split

private const val folder = "day05"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("$folder/input")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Long {
    val splitInput = input.split { it.isEmpty() }
    val seeds = getSeeds(splitInput.first().first())
    return splitInput.drop(1)
        .map { Mapper.build(it.drop(1)) }
        .fold(seeds) { acc, mapper ->
            acc.map { mapper.map(it) }
        }
        .min()
}

private fun part2(input: List<String>): Long {
    val splitInput = input.split { it.isEmpty() }
    val mappers = splitInput.drop(1)
        .map { Mapper.build(it.drop(1)) }
    return getSeedRanges(splitInput.first().first())
        .minOf { range ->
            range.minOf { value ->
                mappers.fold(value) { acc, mapper ->
                    mapper.map(acc)
                }
            }
        }
}

private fun getSeeds(line: String): List<Long> {
    return line.removePrefix("seeds: ")
        .split(" ")
        .map { it.toLong() }
}

private fun getSeedRanges(line: String): List<LongRange> {
    return getSeeds(line).chunked(2)
        .map { (start, range) ->
            start until (start + range)
        }
}

private data class Mapper(
    private val lines: List<MapperLine>
) {

    fun map(value: Long): Long {
        for (line in lines) {
            val destination = line.map(value)
            if (destination != null) return destination
        }
        return value
    }

    companion object {

        fun build(input: List<String>): Mapper {
            return Mapper(input.map { MapperLine.build(it) })
        }
    }
}

private data class MapperLine(
    private val sourceStart: Long,
    private val destinationStart: Long,
    private val range: Long
) {

    private val sourceEnd = sourceStart + range

    fun map(value: Long): Long? {
        return if (value in sourceStart until sourceEnd) {
            destinationStart + value - sourceStart
        } else {
            null
        }
    }

    companion object {
        fun build(line: String): MapperLine {
            val values = line.split(" ").map { it.toLong() }
            return MapperLine(values[1], values[0], values[2])
        }
    }
}
