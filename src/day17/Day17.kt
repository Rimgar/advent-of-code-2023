package day17

import day17.Vertex.VertexDirection
import util.Vector
import util.get
import util.println
import util.readInput
import kotlin.collections.set
import kotlin.time.measureTime

private const val folder = "day17"

fun main() {

    // test if implementation meets criteria from the description, like:

    val myTestInput = readInput("$folder/my_test")
    check(part1(myTestInput) == 7)
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 102)
    check(part2(testInput) == 94)
    val testInput2 = readInput("$folder/test2")
    check(part2(testInput2) == 71)

    val input = readInput("$folder/input")
    measureTime {
        part1(input).println()
    }.println()
    measureTime {
        part2(input).println()
    }.println()
}

private fun part1(input: List<String>): Int {
    val graph = buildGraph(input)
    return findShortestPath(graph)
}

private fun part2(input: List<String>): Int {
    val graph = buildGraph(input, 4..10)
    return findShortestPath(graph)
}

private fun findShortestPath(graph: Graph): Int {
    val heatLossMap = mutableMapOf(graph.startVertex to 0).withDefault { Int.MAX_VALUE }
    val queue = graph.vertices.toMutableSet()

    while (queue.isNotEmpty()) {
        val currentElement = queue.minBy { heatLossMap.getValue(it) }
        queue.remove(currentElement)
        val currentHeatLoss = heatLossMap.getValue(currentElement)
        if (currentElement == graph.destinationVertex) {
            return currentHeatLoss
        }
        currentElement.edges.forEach { edge ->
            val newHeatLoss = currentHeatLoss + edge.heatLoss
            if (newHeatLoss < heatLossMap.getValue(edge.target)) {
                heatLossMap[edge.target] = newHeatLoss
            }
        }
    }

    throw IllegalStateException("A path to the destination should have been found")
}

private fun buildGraph(input: List<String>, stepRange: IntRange = 1..3): Graph {
    val startVertex = Vertex(Vector(0, 0), null)
    val destinationVertex = Vertex(Vector(input.last().lastIndex, input.lastIndex), null)
    val horizontalVertices = mutableMapOf<Vector, Vertex>()
    val verticalVertices = mutableMapOf<Vector, Vertex>()
    val vertices = mapOf(VertexDirection.Horizontal to horizontalVertices, VertexDirection.Vertical to verticalVertices)
    for (x in input.first().indices) {
        for (y in input.indices) {
            val coordinates = Vector(x, y)
            horizontalVertices[coordinates] = Vertex(coordinates, VertexDirection.Horizontal)
            verticalVertices[coordinates] = Vertex(coordinates, VertexDirection.Vertical)
        }
    }
    horizontalVertices.remove(startVertex.coordinates)
    horizontalVertices.remove(destinationVertex.coordinates)
    verticalVertices.remove(startVertex.coordinates)
    verticalVertices.remove(destinationVertex.coordinates)

    val toProcess = setOf(startVertex) + horizontalVertices.values + verticalVertices.values

    horizontalVertices[startVertex.coordinates] = startVertex
    horizontalVertices[destinationVertex.coordinates] = destinationVertex
    verticalVertices[startVertex.coordinates] = startVertex
    verticalVertices[destinationVertex.coordinates] = destinationVertex

    toProcess.forEach { vertex ->
        getPossibleDirections(vertex.incomingDirection).forEach { baseDirection ->
            var heatLoss = getPreRangeHeatLoss(stepRange, vertex, baseDirection, input)
            for (stepSize in stepRange) {
                val successorCoordinates = vertex.coordinates + baseDirection * stepSize
                if (successorCoordinates.inRangeOf(input)) {
                    heatLoss += input[successorCoordinates].digitToInt()
                    val successorVertex = vertices.getValue(VertexDirection.fromVector(baseDirection)).getValue(successorCoordinates)
                    vertex.edges.add(Edge(successorVertex, heatLoss))
                } else {
                    break
                }
            }
        }

    }

    return Graph(toProcess + destinationVertex, startVertex, destinationVertex)
}

private fun getPreRangeHeatLoss(
    stepRange: IntRange,
    vertex: Vertex,
    baseDirection: Vector,
    input: List<String>
): Int {
    var heatLoss = 0
    for (stepSize in 1..<stepRange.first) {
        val successorCoordinates = vertex.coordinates + baseDirection * stepSize
        if (successorCoordinates.inRangeOf(input)) {
            heatLoss += input[successorCoordinates].digitToInt()
        } else {
            break
        }
    }
    return heatLoss
}

private fun getPossibleDirections(direction: VertexDirection?): List<Vector> {
    return when (direction) {
        VertexDirection.Horizontal -> verticalDirections
        VertexDirection.Vertical -> horizontalDirections
        else -> horizontalDirections + verticalDirections
    }
}

private class Graph(
    val vertices: Set<Vertex>,
    val startVertex: Vertex,
    val destinationVertex: Vertex
)

private data class Vertex(
    val coordinates: Vector,
    val incomingDirection: VertexDirection?,
) {

    val edges = mutableSetOf<Edge>()

    enum class VertexDirection {
        Horizontal, Vertical;

        companion object {

            fun fromVector(v: Vector): VertexDirection {
                return if (v.x == 0) {
                    if (v.y == 0) {
                        throw IllegalArgumentException("Zero vector does not have a direction")
                    } else {
                        Vertical
                    }
                } else if (v.y == 0) {
                    Horizontal
                } else {
                    throw IllegalArgumentException("Vector has both components != 0")
                }
            }
        }
    }
}

private data class Edge(
    val target: Vertex,
    val heatLoss: Int
) {

    override fun hashCode(): Int = target.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Edge

        return target == other.target
    }
}

private val horizontalDirections = listOf(
    Vector(1, 0),
    Vector(-1, 0),
)

private val verticalDirections = listOf(
    Vector(0, 1),
    Vector(0, -1),
)
