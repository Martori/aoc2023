private fun List<String>.springMap() = map { line ->
    val (status, checkSums) = line.split(" ")
    SpringRow(status, checkSums.split(",").map { it.toInt() })
}


data class SpringRow(
    val status: String,
    val checkSums: List<Int>
) {
    private val cache = mutableMapOf<Triple<String, List<Int>, Int>, Long>()
    fun findMatches() = findMatches(status, checkSums, 0)
    private fun findMatches(partialStatus: String, partialChecks: List<Int>, currentGroupSize: Int): Long {
        fun Long.cached() = also { cache[Triple(partialStatus, partialChecks, currentGroupSize)] = it }

        if (cache.getOrDefault(Triple(partialStatus, partialChecks, currentGroupSize), -1) >= 0)
            return cache[Triple(partialStatus, partialChecks, currentGroupSize)]!!
        if (partialStatus.isEmpty())
            return if (partialChecks.isEmpty() || partialChecks.size == 1 && currentGroupSize == partialChecks.first()) 1L.cached() else 0L.cached()
        if (partialChecks.isNotEmpty() && currentGroupSize > partialChecks.first())
            return 0L.cached()
        else if (partialChecks.isEmpty() && currentGroupSize > 0)
            return 0L.cached()
        else if (partialChecks.isEmpty() && currentGroupSize == 0)
            return 1L.cached()

        if (partialStatus.matches(partialChecks.reduceFirstBy(currentGroupSize))) return 1

        val currentChar = partialStatus.first()
        val res = (if (currentChar.isField) {
            if (currentGroupSize > 0 && partialChecks.first() == currentGroupSize) findMatches(partialStatus.drop(1), partialChecks.drop(1), 0)
            else if (currentGroupSize == 0) findMatches(partialStatus.drop(1), partialChecks, 0)
            else 0
        } else 0) + if (currentChar.isSpring) {
            findMatches(partialStatus.drop(1), partialChecks, currentGroupSize + 1)
        } else 0
        return res.cached()
    }


    private val Char.isSpring get() = this == '#' || this == '?'
    private val Char.isField get() = this == '.' || this == '?'

    fun unfold() = copy(
        status = ("$status?").repeat(5).dropLast(1),
        checkSums = checkSums.repeatIt(5)
    )
}

private fun List<Int>.reduceFirstBy(subtract: Int) = mapIndexed { index, i -> if(index == 0) i-subtract else i }
fun String.matches(check: List<Int>) = split('.').filter { it.isNotEmpty() }.map { it.length } == check


fun main() {

    fun part1(input: List<String>) = input.springMap().sumOf {
        it.findMatches()
    }

    fun part2(input: List<String>) = input.springMap().sumOf {
        it.unfold().findMatches()
    }

    val testInput = readInput("Day12Test")

    check(part1(testInput) == 21L)
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")

    part1(input).println()
    part2(input).println()
}