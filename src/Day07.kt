val cardMap = mapOf(
    '2' to 2, '3' to 3, '4' to 4, '5' to 5, '6' to 6, '7' to 7, '8' to 8, '9' to 9, 'T' to 10, 'J' to 11, 'Q' to 12, 'K' to 13, 'A' to 14,
)
val jokerCardMap = mapOf(
    '2' to 2, '3' to 3, '4' to 4, '5' to 5, '6' to 6, '7' to 7, '8' to 8, '9' to 9, 'T' to 10, 'J' to 1, 'Q' to 12, 'K' to 13, 'A' to 14,
)

enum class Kind {
    Highest, Pair, DoublePair, Three, Full, Poker, RePoker
}

data class Hand(val cards: String, val bid: Int, val jAsJoker: Boolean) : Comparable<Hand> {
    private val cardValues = cards.map { if (jAsJoker) jokerCardMap[it]!! else cardMap[it]!! }
    private fun Char.isJoker() = jAsJoker && this == 'J'
    private val jokers = cards.count { it.isJoker() }
    private val kind = cards.filter { !it.isJoker() }
        .associateWith { card -> cards.count { it == card } }
        .values.sortedDescending()
        .let { sorted -> sorted.getOrElse(0) { 0 } to sorted.getOrElse(1) { 0 } }
        .let { (highest, second) ->
            when (highest + jokers) {
                5 -> Kind.RePoker
                4 -> Kind.Poker
                3 -> if (second == 2) Kind.Full else Kind.Three
                2 -> if (second == 2) Kind.DoublePair else Kind.Pair
                else -> Kind.Highest
            }
        }

    override fun compareTo(other: Hand) =
        if (kind != other.kind) kind.ordinal - other.kind.ordinal
        else cardValues.zip(other.cardValues) { c, o -> c.compareTo(o) }.firstOrNull { it != 0 } ?: 0
}

fun main() {
    fun List<String>.solve(jokers: Boolean) = map {
        val (cards, bid) = it.split(" ")
        Hand(cards, bid.toInt(), jokers)
    }
        .sorted()
        .mapIndexed { index, hand -> (index + 1) * hand.bid }
        .sum()

    fun part1(input: List<String>) = input.solve(false)

    fun part2(input: List<String>) = input.solve(true)

    val testInput = readInput("Day07Test")

    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")

    part1(input).println()
    part2(input).println()
}
