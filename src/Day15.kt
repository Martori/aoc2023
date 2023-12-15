private fun String.hash() = fold(0) { acc, next ->
    ((acc + next.code) * 17) % 256
}

private fun Map<String, Int>.power() = values.mapIndexed { index, i -> (index + 1) * i }.sum()

private fun MutableList<Map<String, Int>>.applyLensAction(lensAction: String): MutableList<Map<String, Int>> = apply {
    if (lensAction.last() == '-') lensAction.dropLast(1).let { key -> this[key.hash()] = this[key.hash()] - key }
    else lensAction.split("=").let { (key, value) -> this[key.hash()] = this[key.hash()] + (key to value.toInt()) }
}

fun main() {

    fun part1(input: String) = input.split(",").sumOf { it.hash() }

    fun part2(input: String) = input.split(",").fold(MutableList<Map<String, Int>>(256) { emptyMap() }) { acc, s ->
        acc.applyLensAction(s)
    }.mapIndexed { index, map -> (index + 1) * map.power() }.sum()

    val testInput = readInputAsText("Day15Test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInputAsText("Day15")
    part1(input).println()
    part2(input).println()
}
