import kotlin.math.max

const val RED = "red"
const val GREEN = "green"
const val BLUE = "blue"

data class Game(val id: Int, val playedHands: List<SetOfCubes>) {
    fun isPossible(limit: SetOfCubes): Boolean =
            playedHands.all { it.isPossible(limit) }

    private val minimumSetOfCubes = playedHands
            .fold(SetOfCubes(0, 0, 0)) { acc, next ->
                acc max next
            }

    val minimumPower = minimumSetOfCubes.power

}

fun Game(line: String) = line.split(":").let { split ->
    Game(split.first().split(" ").last().toInt(), split[1].split(";").map { SetOfCubes(it) })
}

data class SetOfCubes(val red: Int, val green: Int, val blue: Int) {
    fun isPossible(limit: SetOfCubes) =
            red <= limit.red && green <= limit.green && blue <= limit.blue

    val power = red * green * blue

    infix fun max(other: SetOfCubes) =
            SetOfCubes(max(red, other.red), max(green, other.green), max(blue, other.blue))
}

fun SetOfCubes(text: String) = text.split(", ").map { it.trim() }.fold(emptyMap<String, Int>()) { acc, next ->
    val (value, color) = next.split(" ")
    acc.plus(color to value.toInt())
}.let { SetOfCubes(it[RED] ?: 0, it[GREEN] ?: 0, it[BLUE] ?: 0) }

val limitSetOfCubes = SetOfCubes(12, 13, 14)

fun main() {
    fun part1(input: List<String>) = input.map { Game(it) }.filter { it.isPossible(limitSetOfCubes) }.sumOf { it.id }

    fun part2(input: List<String>): Int = input.sumOf { Game(it).minimumPower }


    check(part1(readInput("Day02Test")) == 8)
    check(part2(readInput("Day02Test")) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}