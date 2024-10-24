package day08

import computeLeastCommonMultiple
import day08.Direction.L
import day08.Direction.R
import println
import readInput

private const val folder = "day08"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 2)
    val testInput2 = readInput("$folder/test2")
    check(part1(testInput2) == 6)
    val testInput3 = readInput("$folder/test3")
    check(part2(testInput3) == 6L)

    val input = readInput("$folder/input")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    val directions = input.first().map { Direction.valueOf(it.toString()) }
    val startNode = createGraph(input.drop(2)).getValue("AAA")

    return navigateThroughNetwork(startNode, directions) { node -> node.name == "ZZZ" }
}

private fun part2(input: List<String>): Long {
    val directions = input.first().map { Direction.valueOf(it.toString()) }
    val steps = createGraph(input.drop(2))
        .filterValues { node -> node.name.endsWith("A") }
        .values
        .map { startNode -> navigateThroughNetwork(startNode, directions) { node -> node.name.endsWith("Z") } }
        .map { it.toLong() }
    return computeLeastCommonMultiple(steps)
}

private fun createGraph(input: List<String>): Map<String, Node> {
    val triples = input.map { line ->
        """[A-Z0-9]{3}""".toRegex()
            .findAll(line)
            .map { matchResult -> matchResult.value }
            .toList()
    }
    val nodes = triples.associate { line ->
        line.first() to Node(line.first())
    }
    triples.forEach { line ->
        nodes.getValue(line.first()).apply {
            leftNode = nodes.getValue(line[1])
            rightNode = nodes.getValue(line[2])
        }
    }
    return nodes
}

private fun navigateThroughNetwork(startNode: Node, directions: List<Direction>, goalCondition: (Node) -> Boolean): Int {
    var currentNode = startNode
    var steps = 0
    while (true) {
        directions.forEach { direction ->
            currentNode = currentNode.goto(direction)
            steps++
            if (goalCondition(currentNode)) {
                return steps
            }
        }
    }
}

private class Node(
    val name: String
) {

    lateinit var leftNode: Node
    lateinit var rightNode: Node

    fun goto(direction: Direction): Node =
        when (direction) {
            L -> leftNode
            R -> rightNode
        }

    override fun toString(): String {
        return "$name -> ${leftNode.name} ${rightNode.name}"
    }
}

private enum class Direction {
    L, R
}
