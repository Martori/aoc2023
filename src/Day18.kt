import kotlin.math.absoluteValue


private fun String.toDirection() = when (this) {
    "R", "0" -> Direction.E
    "D", "1" -> Direction.S
    "L", "2" -> Direction.W
    "U", "3" -> Direction.N
    else -> error("invalid input")
}

fun main() {
    fun List<Pair<Direction, Long>>.perimeter() = sumOf { (_, num) -> num }

    fun List<Pair<Direction, Long>>.vertices() = scan(0L to 0L) { (x, y), (dir, num) ->
        when (dir) {
            Direction.E -> x + num to y
            Direction.W -> x - num to y
            Direction.N -> x to y + num
            Direction.S -> x to y - num
        }
    }

    fun List<String>.getInstructions() =
        map { line -> line.split(" ").let { it[0] to it[1] } }
            .map { (dir, num) -> dir.toDirection() to num.toLong() }

    fun List<String>.getHexaInstructions() =
        map { line ->
            line.split("#")[1].dropLast(1)
                .let { it.last().toString().toDirection() to it.dropLast(1).toLong(radix = 16) }
        }

    fun Pair<Long, List<Pair<Long, Long>>>.getArea() = let { (perimeter, vertices) ->
        vertices.zipWithNext { a, b -> (a.x * b.y - b.x * a.y) }
            .sum()
            .div(2)
            .absoluteValue + ((perimeter / 2) + 1)
    }

    fun part1(input: List<String>) = input.getInstructions().let { it.perimeter() to it.vertices() }.getArea()

    fun part2(input: List<String>) = input.getHexaInstructions().let { it.perimeter() to it.vertices() }.getArea()

    val testInput = readInput("Day18Test")
    check(part1(testInput) == 62L)
    check(part2(testInput) == 952408144115L)

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}

