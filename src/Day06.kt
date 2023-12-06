import java.time.Duration


data class Race(val duration: Long, val record: Long) {
    private fun calculateDistance(timePressing: Long) = (duration - timePressing) * timePressing
    private fun Long.beatsRecord() = calculateDistance(this) > record

    val waysToBeatTheRecord = (1..<duration).let { range ->
        val first = range.indexOfFirst { it.beatsRecord() }
        val last = range.indexOfLast { it.beatsRecord() }
        last - first + 1
    }
}

fun List<String>.getRaces() = map { line -> line.split(" ").mapNotNull { it.toLongOrNull() } }
    .zipWithNext { a, b -> a.zip(b) { duration, record -> Race(duration, record) } }
    .flatten()

fun List<String>.getSingleRace() = mapNotNull { line -> line.filter { it.isDigit() }.toLongOrNull() }
    .let { (duration, record) -> Race(duration, record) }

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
