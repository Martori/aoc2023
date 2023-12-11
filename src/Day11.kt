typealias Galaxies = List<Pair<Long, Long>>
typealias Galaxy = Pair<Long, Long>

val Galaxy.x get() = first
val Galaxy.y get() = second

private fun List<String>.getGalaxiesPositions() = flatMapIndexed { row, line ->
    line.mapIndexed { column, c ->
        if (c == '#') column.toLong() to row.toLong() else null
    }.filterNotNull()
}

private fun Galaxies.expandSpace(factor: Long) = unzip().let { (xs, ys) ->
    xs.expand(factor).zip(ys.expand(factor))
}

private fun List<Long>.expand(factor: Long) =
    map { position -> position + (factor - 1) * (position - distinct().count { it < position }) }

private infix fun Galaxy.distanceTo(other: Galaxy) =
    maxOf(x, other.x) - minOf(x, other.x) + maxOf(y, other.y) - minOf(y, other.y)

private fun Galaxy.distanceTo(others: Galaxies) =
    others.map { this distanceTo it }

private fun Galaxies.sumAllDistances(): Long =
    if (size <= 1) 0
    else drop(1).let { others ->
        first().distanceTo(others).sum() + others.sumAllDistances()
    }

fun main() {
    fun List<String>.solve(factor: Long) = getGalaxiesPositions().expandSpace(factor).sumAllDistances()

    fun part1(input: List<String>) = input.solve(2)
    fun part2(input: List<String>) = input.solve(1000000)

    val testInput = readInput("Day11Test")
    check(part1(testInput) == 374L)
    check(testInput.solve(10) == 1030L)
    check(testInput.solve(100) == 8410L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}




