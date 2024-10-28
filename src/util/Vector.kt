package util

import kotlin.math.abs

typealias Vector = Pair<Int, Int>

operator fun Vector.unaryMinus() = -first to -second
operator fun Vector.plus(p: Vector) = (first + p.first) to (second + p.second)
operator fun Vector.minus(p: Vector) = (first - p.first) to (second - p.second)

fun Vector.cityBlockDistance(other: Vector) =
    (other - this).let { (first, second) ->
        abs(first) + abs(second)
    }