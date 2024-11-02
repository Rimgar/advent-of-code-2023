package day12

import util.length
import util.println
import util.product
import util.readInput
import util.repeat
import util.replace
import util.split
import kotlin.math.min
import kotlin.time.measureTime

private const val folder = "day12"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 21L)
    check(part2(testInput) == 525152L)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Long {
    return splitInput(input).solveSmart()
}

private fun part2(input: List<String>): Long {
    return splitInput(input)
        .map { (conditionRecords, groups) -> conditionRecords.repeat(5, "?") to groups.repeat(5) }
        .solveSmart()
}

private fun splitInput(input: List<String>): List<Pair<String, List<Int>>> {
    return input.map { line ->
        val (conditionRecords, groups) = line.split(' ')
        conditionRecords to groups.split(',').map { it.toInt() }
    }
}

private fun List<Pair<String, List<Int>>>.solveSmart(): Long {
    val solver: Solver = SplitByMaxGroupSolver()
    return sumOf { (conditionRecord, groups) ->
        solver.solve(conditionRecord, groups)
    }
}

private fun List<Pair<String, List<Int>>>.solveBruteForce(): Long {
    val solver: Solver = BruteForceSolver()
    return sumOf { (conditionRecord, groups) ->
        solver.solve(conditionRecord, groups)
    }
}

private abstract class Solver {

    fun solve(conditionRecord: String, groups: List<Int>): Long {
        val trimmedConditionRecord = conditionRecord.trim('.')
        return cache[trimmedConditionRecord to groups]
            ?: solveAndCache(trimmedConditionRecord, groups)
    }

    private fun solveAndCache(conditionRecord: String, groups: List<Int>): Long {
        return solveInternal(conditionRecord, groups).also { result ->
            cache[conditionRecord to groups] = result
        }
    }

    protected abstract fun solveInternal(conditionRecord: String, groups: List<Int>): Long

    protected fun validateConditionRecord(conditionRecord: String, groups: List<Int>): Boolean {
        val actualGroups = conditionRecord.split("""[.?]+""".toRegex())
            .mapNotNull { if (it.isEmpty()) null else it.length }
        return actualGroups == groups
    }

    companion object {
        private val cache = mutableMapOf<Pair<String, List<Int>>, Long>()
    }
}

private class SplitByMaxGroupSolver : Solver() {

    private val fromStartSolver: Solver = FromStartSolver()

    override fun solveInternal(conditionRecord: String, groups: List<Int>): Long {
        """#+""".toRegex().findAll(conditionRecord)
            .groupBy({ it.range.length }) { it.range }
            .maxByOrNull { it.key }
            ?.let { (maxGroupLength, ranges) ->
                val count = ranges.size
                return if (groups.count { it > maxGroupLength } != 0) {
                    // longest expected group is longer than the longest found group
                    // NOT SPLITTABLE
                    fromStartSolver.solve(conditionRecord, groups)
                } else if (groups.count { it == maxGroupLength } == count) {
                    // there are as many longest expected groups as there are longest found group
                    // SPLITTABLE
                    val splitConditionRecords = conditionRecord.split(""".?#{$maxGroupLength}.?""".toRegex())
                    val splitGroups = groups.split { it == maxGroupLength }
                    splitConditionRecords.zip(splitGroups)
                        .filter { (_, separatedGroups) -> separatedGroups.isNotEmpty() }
                        .map { solve(it.first, it.second) }
                        .product()
                } else {
                    // NOT SPLITTABLE
                    fromStartSolver.solve(conditionRecord, groups)
                }
            } ?: return fromStartSolver.solve(conditionRecord, groups)
    }
}

private class FromStartSolver : Solver() {

    override fun solveInternal(conditionRecord: String, groups: List<Int>): Long {
        if (groups.isEmpty()) {
            return if (conditionRecord.contains('#')) 0 else 1
        } else if (conditionRecord.isEmpty()) {
            return 0
        } else if (conditionRecord.count { it != '.' } < groups.sum()) {
            return 0
        }
        val expectedLength = groups.first()

        val firstPossibleGroup = conditionRecord.takeWhile { it != '.' }
        if (firstPossibleGroup.length < expectedLength) {
            return if (firstPossibleGroup.contains('#')) {
                0
            } else {
                solve(conditionRecord.drop(firstPossibleGroup.length), groups)
            }
        } else {
            val indexOfFirstHash = firstPossibleGroup.indexOf('#')
            val lastStartIndex = if (indexOfFirstHash == -1) {
                firstPossibleGroup.length - expectedLength
            } else {
                min(indexOfFirstHash, firstPossibleGroup.length - expectedLength)
            }
            val ignoreFirstPossibleGroup = if (indexOfFirstHash == -1) {
                solve(conditionRecord.drop(firstPossibleGroup.length), groups)
            } else {
                0
            }

            return (0..lastStartIndex).mapNotNull { i ->
                if (i + expectedLength < firstPossibleGroup.length && firstPossibleGroup[i + expectedLength] == '#') {
                    null
                } else {
                    solve(conditionRecord.drop(i + expectedLength + 1), groups.drop(1))
                }
            }.sum() + ignoreFirstPossibleGroup
        }
    }
}

private class BruteForceSolver : Solver() {

    override fun solveInternal(conditionRecord: String, groups: List<Int>): Long {
        return solve(conditionRecord, groups, startIndex = 0)
    }

    private fun solve(conditionRecord: String, groups: List<Int>, startIndex: Int): Long {
        val index = conditionRecord.indexOf("?", startIndex)
        return if (index < 0) {
            if (validateConditionRecord(conditionRecord, groups)) 1 else 0
        } else {
            solve(conditionRecord.replace(index, '#'), groups, index + 1) +
                    solve(conditionRecord, groups, index + 1)
        }
    }
}
