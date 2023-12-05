data class Almanac(
    val seeds: List<Long> = emptyList(),
    val categoryMappers: List<CategoryMapper> = emptyList()
) {
    private val seedsAsRanges = seeds.chunked(2).map { (s, l) -> s..<s + l }
    fun mapSeedsToLocations() = seeds.map {
        categoryMappers.fold(it) { acc, almanacMap -> almanacMap.mapItem(acc) }
    }

    fun mapSeedRangesToLocationRanges() = seedsAsRanges.flatMap { range ->
        categoryMappers.fold(listOf(range)) { acc, next ->
            acc.flatMap { next.mapRange(it) }
        }
    }
}

fun Almanac(input: String) = input.split("\n\n").map { section ->
    section.split("\n")
        .filter { line -> line.any { it.isDigit() } }
        .map { line -> line.extractNumbers() }
}.let { content ->
    Almanac(seeds = content.first().first(), categoryMappers = content.drop(1).map { CategoryMapper(it) })
}

private fun String.extractNumbers() =
    split(" ").filter { words -> words.any { it.isDigit() } }.mapNotNull { it.toLongOrNull() }

data class CategoryMapper(val rangeMappers: List<RangeMapper> = emptyList()) {
    fun mapItem(item: Long) = rangeMappers.find { item in it }?.mapItem(item) ?: item
    fun mapRange(range: LongRange) =
        rangeMappers.filter { range in it }
            .fold(listOf(range) to listOf<LongRange>()) { (unmappedRanges, mappedRanges), mapping ->
                unmappedRanges
                    .map(mapping::mapRange)
                    .unzip()
                    .let { (unmapped, mapped) ->
                        unmapped.flatten() to (mappedRanges + mapped).filterNotNull()
                    }
            }
            .toList()
            .reduce(List<LongRange>::plus)
}

fun CategoryMapper(input: List<List<Long>>) = CategoryMapper(input.map { RangeMapper(it) })

data class RangeMapper(val destination: Long, val source: Long, val rangeLength: Long) {
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

fun RangeMapper(mapping: List<Long>) = mapping.let { (dest, source, range) ->
    RangeMapper(dest, source, range)
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

