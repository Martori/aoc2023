private fun List<String>.parseNumbers() = map { line -> line.split(" ").map { it.toInt() } }

private fun List<Int>.predictNext(): Int =
    if (all { it == 0 }) 0
    else last() + zipWithNext { a, b -> b - a }.predictNext()

private fun List<Int>.predictPrevious(): Int =
    if (all { it == 0 }) 0
    else first() - zipWithNext { a, b -> b - a }.predictPrevious()

fun main() {

    fun part1(input: List<String>) = input.parseNumbers().sumOf {
        it.predictNext()
    }

    fun part2(input: List<String>) = input.parseNumbers().sumOf {
        it.predictPrevious()
    }

    val testInput = readInput("Day09Test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}

