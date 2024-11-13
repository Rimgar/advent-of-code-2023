package util

import kotlin.math.abs

typealias Vector = Pair<Int, Int>

operator fun Vector.unaryMinus() = -first to -second
operator fun Vector.plus(p: Vector) = (first + p.first) to (second + p.second)
operator fun Vector.minus(p: Vector) = (first - p.first) to (second - p.second)

val Vector.x get() = first
val Vector.y get() = second

fun Vector.cityBlockDistance(other: Vector) =
    (other - this).let { (first, second) ->
        abs(first) + abs(second)
    }

operator fun List<String>.get(p: Vector) = this[p.second][p.first]
