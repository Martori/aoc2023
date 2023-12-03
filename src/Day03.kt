sealed class Positioned
data class Number(val value: String, val row: Int, val index: Int) : Positioned() {

    val extendedRange = (index - 1).coerceAtLeast(0)..(index + value.length)

    val intValue = value.toInt()
    fun extendBy(next: Char) =
            copy(value = value + next)
}

data class Symbol(val value: Char, val row: Int, val column: Int) : Positioned() {
    fun neighboursIn(matrix: Schematic) =
            matrix.subList((row - 1).coerceAtLeast(0), (row + 2).coerceAtMost(matrix.size))
                    .flatten()
                    .filterIsInstance<Number>()
                    .filter { column in (it.extendedRange + 1) }

    fun gearRatioIn(matrix: Schematic) = neighboursIn(matrix).let { neighbours ->
        if (isGear(neighbours)) neighbours.fold(1) { acc, number -> acc * number.intValue }
        else null
    }

    private fun isGear(neighbours: List<Number>) = value == '*' && neighbours.size == 2

}

data object Dot : Positioned()

fun Char.isSymbol() = !isLetterOrDigit() && this != '.'

typealias Schematic = List<List<Positioned>>

fun main() {

    fun processDigit(row: Int, column: Int, next: Char, acc: List<Positioned>) = acc.lastOrNull().let { last ->
        if (last is Number) {
            acc.dropLast(1) + last.extendBy(next)
        } else acc + Number(next.toString(), row, column)
    }

    fun processNext(row: Int, column: Int, next: Char, acc: List<Positioned>) = when {
        next.isDigit() -> processDigit(row, column, next, acc)
        next.isSymbol() -> acc + Symbol(next, row, column)
        else -> acc + Dot
    }

    fun List<String>.toSchematic(): Schematic = mapIndexed { row, item ->
        item.foldIndexed(emptyList<Positioned>()) { column, acc, next ->
            processNext(row, column, next, acc)
        }.filter { it !is Dot }
    }

    fun part1(input: List<String>) = input.toSchematic().let { schematic ->
        schematic.flatten().filterIsInstance<Symbol>().flatMap {
            it.neighboursIn(schematic)
        }.distinct().sumOf { it.intValue }
    }

    fun part2(input: List<String>): Int = input.toSchematic().let { schematic ->
        schematic.flatten().filterIsInstance<Symbol>()
                .mapNotNull { it.gearRatioIn(schematic) }
                .sum()
    }


    val testInput = readInput("Day03Test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}