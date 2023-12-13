private fun AshPattern.reflectsAt(reflectPoint: Int) = (0..reflectPoint)
    .map { index -> reflectPoint - index to reflectPoint + index + 1 }
    .filter { (_, last) -> last <= lastIndex }
    .all { (first, second) -> this[first] == this[second] }

private fun AshPattern.smudgesToReflection(reflectPoint: Int) = (0..reflectPoint)
    .map { index -> reflectPoint - index to reflectPoint + index + 1 }
    .filter { (_, last) -> last <= lastIndex }
    .sumOf { (first, second) -> this[first].difference(this[second]) }

private fun String.difference(other: String) = zip(other).count { (a, b) -> a != b }

private fun AshPattern.horizontalReflectionPointWithSmudges(smudges: Int) = asSequence()
    .zipWithNext { a, b -> a.difference(b) }
    .mapIndexed { index, b -> index to b }
    .filter { it.second <= smudges }
    .map { it.first }
    .map { reflectPoint -> smudgesToReflection(reflectPoint) to reflectPoint }
    .firstOrNull { it.first == smudges }
    ?.second?.plus(1)

private fun AshPattern.verticalReflectionPointWithSmudges(smudges: Int) = transpose().horizontalReflectionPointWithSmudges(smudges)

typealias AshPattern = List<String>

fun main() {
    fun String.toAshPatterns() = split("\n\n").map { it.split("\n") }

    fun part1(input: String) = input.toAshPatterns().sumOf { pattern ->
        pattern.horizontalReflectionPointWithSmudges(0)?.times(100)
            ?: pattern.verticalReflectionPointWithSmudges(0)
            ?: 0
    }

    fun part2(input: String) = input.toAshPatterns().sumOf { pattern ->
        pattern.horizontalReflectionPointWithSmudges(1)?.times(100)
            ?: pattern.verticalReflectionPointWithSmudges(1)
            ?: 0
    }

    val testInput = readInputAsText("Day13Test")

    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInputAsText("Day13")

    part1(input).println()
    part2(input).println()
}
