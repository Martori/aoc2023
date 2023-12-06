fun List<String>.getRaces() = map { line -> line.split(" ").mapNotNull { it.toLongOrNull() } }
    .zipWithNext { a, b -> a.zip(b) }
    .flatten()

fun List<String>.getSingleRace() = mapNotNull { line -> line.filter { it.isDigit() }.toLongOrNull() }
    .zipWithNext()
    .first()

val Pair<Long, Long>.waysToBeatTheRecord get() = (1..<first).count { (first - it) * it > second }

fun main() {

    fun part1(input: List<String>) = input.getRaces()
        .map { it.waysToBeatTheRecord }
        .fold(1) { acc, next -> acc * next }

    fun part2(input: List<String>) = input.getSingleRace().waysToBeatTheRecord

    val testInput = readInput("Day06Test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
