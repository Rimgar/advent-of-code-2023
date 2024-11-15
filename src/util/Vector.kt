package util

import kotlin.math.abs

data class Vector(
    val x: Int,
    val y: Int
) {

    val first = x
    val second = y
    val length = abs(x) + abs(y)

    operator fun unaryMinus() = Vector(-x, -y)
    operator fun plus(p: Vector) = Vector(x + p.x, y + p.y)
    operator fun minus(p: Vector) = Vector(x - p.x, y - p.y)
    operator fun times(p: Int) = Vector(x * p, y * p)
    operator fun div(p: Int) = Vector(x / p, y / p)

    fun inRangeOf(input: List<String>) = y in input.indices && x in input.first().indices

    fun cityBlockDistance(other: Vector) = (other - this).length
}

operator fun List<String>.get(p: Vector) = this[p.y][p.x]
