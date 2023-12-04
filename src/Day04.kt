import kotlin.math.pow

fun main() {

    fun String.extractNumbers() = split(" ")
            .filter { it.isNotBlank() }
            .map { it.trim().toInt() }

    fun String.countWinners() = split("|")
            .map { it.extractNumbers() }
            .let { (winning, ours) -> ours.count { it in winning } }

    fun List<String>.getAmountOfWinners() = map { it.split(": ")[1] }
            .map { line -> line.countWinners() }

    fun part1(input: List<String>) = input.getAmountOfWinners()
            .filter { it > 0 }
            .sumOf { 2f.pow(it - 1).toInt() }

    fun part2(input: List<String>) = input.getAmountOfWinners()
            .foldIndexed(MutableList(input.size) { 1 }) { cardNumber, acc, next ->
                val numberOfTickets = acc[cardNumber]
                for (i in 1..next) {
                    acc[cardNumber + i] = acc[cardNumber + i] + numberOfTickets
                }
                acc
            }
            .sum()

    val testInput = readInput("Day04Test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}