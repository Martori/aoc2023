typealias Galaxies = List<Pair<Long, Long>>
typealias Galaxy = Pair<Long, Long>

val Galaxy.x get() = first
val Galaxy.y get() = second

private fun List<String>.getGalaxiesPositions() = flatMapIndexed { row, line ->
    line.mapIndexed { column, c ->
        if (c == '#') column.toLong() to row.toLong() else null
    }.filterNotNull()
}

private fun Galaxies.expandSpace(factor: Long): Galaxies {
    val (x, y) = unzip()
    return x.expand(factor).zip(y.expand(factor))
}

const val expansionFactor = 1000000L

private fun List<Long>.expand(factor: Long) = map { position -> position + (factor-1)*(position - distinct().count { it < position }) }
private infix fun Galaxy.distanceTo(other: Galaxy) =
    maxOf(x, other.x) - minOf(x, other.x) + maxOf(y, other.y) - minOf(y, other.y)

private fun Galaxy.distancesTo(others: Galaxies) =
    others.map { this distanceTo it }

fun Galaxies.sumAllDistances(): Long {
    if (size <= 1) return 0
    val first = first()
    val others = drop(1)
    return first.distancesTo(others).sum() + others.sumAllDistances()
}

fun main() {
    fun part1(input: List<String>) = input.getGalaxiesPositions().expandSpace(2).sumAllDistances()
    fun part2(input: List<String>) = input.getGalaxiesPositions().expandSpace(expansionFactor).sumAllDistances()

    val testInput = readInput("Day11Test")
    check(part1(testInput) == 374L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}




