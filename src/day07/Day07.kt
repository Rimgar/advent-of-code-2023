package day07

import day07.Type.FiveOfAKind
import day07.Type.FourOfAKind
import day07.Type.FullHouse
import day07.Type.HighCard
import day07.Type.OnePair
import day07.Type.ThreeOfAKind
import day07.Type.TwoPair
import println
import readInput

private const val folder = "day07"

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("$folder/test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("$folder/input")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    return calculate(input, false)
}

private fun part2(input: List<String>): Int {
    return calculate(input, true)
}

private fun calculate(input: List<String>, withJokers: Boolean) = input.map { line ->
    line.split(" ")
        .let { (handString, bid) -> Hand.fromString(handString, withJokers = withJokers) to bid.toInt() }
}
    .sortedBy { it.first }
    .mapIndexed { index, (_, bid) -> (index + 1) * bid }
    .sum()

private class Hand(
    val cards: List<Card>, withJokers: Boolean
) : Comparable<Hand> {

    val type: Type = calculateType(cards, withJokers)

    private val cardComparator = if (withJokers) Card2Comparator() else Card1Comparator()

    override fun compareTo(other: Hand): Int {
        val typeComparison = -type.compareTo(other.type)
        if (typeComparison == 0) {
            cards.zip(other.cards)
                .forEach { (card, otherCard) ->
                    val cardComparison = -cardComparator.compare(card, otherCard)
                    if (cardComparison != 0) return cardComparison
                }
            return 0
        } else {
            return typeComparison
        }
    }

    private fun calculateType(cards: List<Card>, withJokers: Boolean): Type {
        val groups = cardListMap(cards, withJokers)
            .values
            .map { it.size }
            .sortedDescending()
        return when (groups) {
            listOf(5) -> FiveOfAKind
            listOf(4, 1) -> FourOfAKind
            listOf(3, 2) -> FullHouse
            listOf(3, 1, 1) -> ThreeOfAKind
            listOf(2, 2, 1) -> TwoPair
            listOf(2, 1, 1, 1) -> OnePair
            else -> HighCard
        }
    }

    private fun cardListMap(cards: List<Card>, withJokers: Boolean): Map<Card, List<Card>> {
        val groups = cards.groupBy { it }
        return if (withJokers && groups.containsKey(Card.J) && groups.size != 1) {
            val jokers = groups.getValue(Card.J)
            val filtered = groups.filterKeys { it != Card.J }.toMutableMap()
            filtered
                .entries.maxBy { (_, cards) -> cards.size }
                .key
                .let { filtered.put(it, filtered.getValue(it) + jokers) }
            filtered
        } else {
            groups
        }
    }

    override fun toString(): String {
        return cards.joinToString("", postfix = " $type")
    }

    companion object {
        fun fromString(handString: String, withJokers: Boolean): Hand {
            val cards = handString.map { Card.fromChar(it) }
            return Hand(cards, withJokers)
        }
    }
}

private enum class Type {
    FiveOfAKind, FourOfAKind, FullHouse, ThreeOfAKind, TwoPair, OnePair, HighCard
}

private enum class Card {
    A, K, Q, J, T, N9, N8, N7, N6, N5, N4, N3, N2;

    override fun toString(): String {
        return when (this) {
            A -> "A"
            K -> "K"
            Q -> "Q"
            J -> "J"
            T -> "T"
            N9 -> "9"
            N8 -> "8"
            N7 -> "7"
            N6 -> "6"
            N5 -> "5"
            N4 -> "4"
            N3 -> "3"
            N2 -> "2"
        }
    }

    companion object {

        fun fromChar(char: Char): Card {
            return when (char) {
                'A' -> A
                'K' -> K
                'Q' -> Q
                'J' -> J
                'T' -> T
                '9' -> N9
                '8' -> N8
                '7' -> N7
                '6' -> N6
                '5' -> N5
                '4' -> N4
                '3' -> N3
                '2' -> N2
                else -> throw IllegalArgumentException("$char is unknown")
            }
        }
    }
}

private class Card1Comparator : Comparator<Card> {

    override fun compare(p0: Card, p1: Card): Int {
        return p0.compareTo(p1)
    }
}

private class Card2Comparator : Comparator<Card> {

    override fun compare(p0: Card, p1: Card): Int {
        return if (p0 == Card.J) {
            if (p1 == Card.J) {
                0
            } else {
                1
            }
        } else if (p1 == Card.J) {
            -1
        } else {
            p0.compareTo(p1)
        }
    }
}
