data class Almanac(
    val seeds: List<Long> = emptyList(),
    val maps: List<AlmanacMap> = emptyList()
) {
    private val seedsAsRanges = seeds.chunked(2).map { (s, l) -> s..<s + l }
    fun mapSeedsToLocations() = seeds.map {
        maps.fold(it) { acc, almanacMap -> almanacMap.mapItem(acc) }
    }

    fun mapSeedRangesToLocationRanges() = seedsAsRanges.flatMap { range ->
        maps.fold(listOf(range)) { acc, next ->
            acc.flatMap { next.mapRange(it) }
        }
    }
}

fun Almanac(input: String) = input.split("\n\n").map { section ->
    section.split("\n")
        .filter { line -> line.any { it.isDigit() } }
        .map { line -> line.extractNumbers() }
}.let { content ->
    Almanac(seeds = content.first().first(), maps = content.drop(1).map { AlmanacMap(it) })
}

private fun String.extractNumbers() =
    split(" ").filter { words -> words.any { it.isDigit() } }.mapNotNull { it.toLongOrNull() }

data class AlmanacMap(val mappings: List<AlmanacMapping> = emptyList()) {
    fun mapItem(item: Long) = mappings.find { item in it }?.mapItem(item) ?: item

    fun mapRange(range: LongRange): List<LongRange> {
        val mappedRanges = mutableListOf<LongRange>()
        var unMappedRanges = listOf(range)
        mappings.filter { range in it }.forEach { mapping ->
            unMappedRanges = unMappedRanges.flatMap { currentUnMappedRange ->
                val (unMapped, mapped) = mapping.mapRange(currentUnMappedRange)
                mapped?.let { mappedRanges.add(it) }
                unMapped
            }
        }
        return unMappedRanges + mappedRanges
    }
}

fun AlmanacMap(input: List<List<Long>>) = AlmanacMap(input.map { AlmanacMapping(it) })

data class AlmanacMapping(val destination: Long, val source: Long, val rangeLength: Long) {
    private val change = destination - source
    private val sourceRange = source..<source + rangeLength
    operator fun contains(item: Long) = item in sourceRange
    operator fun contains(input: LongRange) = input.first <= sourceRange.last && input.last >= sourceRange.first
    fun mapItem(item: Long) = item + change
    fun mapRange(input: LongRange): Pair<List<LongRange>, LongRange?> {
        if (input !in this) return listOf(input) to null
        val start = maxOf(input.first, sourceRange.first)
        val end = minOf(input.last, sourceRange.last)
        val mappedRange = (start + change..end + change)
        return listOf(input.first..<start, end + 1..input.last).filterNot { it.isEmpty() } to mappedRange
    }

}

fun AlmanacMapping(mapping: List<Long>) = mapping.let { (dest, source, range) ->
    AlmanacMapping(dest, source, range)
}

fun main() {

    fun part1(input: String) = Almanac(input)
        .mapSeedsToLocations()
        .min()

    fun part2(input: String) = Almanac(input)
        .mapSeedRangesToLocationRanges()
        .minOf { it.first }

    val testInput = readInputAsText("Day05Test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInputAsText("Day05")
    part1(input).println()
    part2(input).println()
}

