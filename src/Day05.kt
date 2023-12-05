data class Almanac(
    val seeds: List<Long> = emptyList(),
    val maps: List<AlmanacMap> = emptyList()
) {

    private val seedsAsRanges = seeds.chunked(2)
    fun mapSeedsToLocations() =
        seeds.map {
            maps.fold(it) { acc, almanacMap ->
                almanacMap.mapItem(acc)
            }
        }

    fun mapSeedRangesToLocations() =
        seedsAsRanges.mapNotNull { (start, length) ->
            (start..<start + length).minOfOrNull {
                maps.fold(it) { acc, almanacMap ->
                    almanacMap.mapItem(acc)
                }
            }
        }

    fun appendList(input: List<Long>): Almanac {
        return if (seeds.isEmpty()) copy(seeds = input)
        else if (input.isNotEmpty())
            copy(maps = maps.dropLast(1) + maps.last().addMapping(AlmanacMapping(input)))
        else copy(maps = maps + AlmanacMap())
    }
}

fun Almanac(input: List<List<Long>>) = input.fold(Almanac()) { acc, next ->
    acc.appendList(next)
}

data class AlmanacMap(val mappings: List<AlmanacMapping> = emptyList()) {
    fun addMapping(mapping: AlmanacMapping) = copy(mappings = mappings + mapping)

    fun mapItem(item: Long) = mappings.find { item in it }?.mapItem(item) ?: item
}

data class AlmanacMapping(
    val destinationRange: Long,
    val sourceRange: Long,
    val rangeLength: Long
) {

    operator fun contains(item: Long) = item in sourceRange..<sourceRange + rangeLength
    fun mapItem(item: Long) = item - sourceRange + destinationRange
}

fun AlmanacMapping(mapping: List<Long>) = mapping.let { (dest, source, range) ->
    AlmanacMapping(dest, source, range)
}

fun main() {

    fun part1(input: List<String>) = input.filter { it.isNotBlank() }
        .map { line -> line.split(" ").mapNotNull { it.toLongOrNull() } }
        .let { Almanac(it) }
        .mapSeedsToLocations()
        .min()

    fun part2(input: List<String>) = input.filter { it.isNotBlank() }
        .map { line -> line.split(" ").mapNotNull { it.toLongOrNull() } }
        .let { Almanac(it) }
        .mapSeedRangesToLocations()
        .min()


    val testInput = readInput("Day05Test")

    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}

