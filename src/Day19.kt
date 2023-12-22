data class XmasPart(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int,
) {
    fun applyWorkFlow(workFlow: List<Pair<(XmasPart) -> Boolean, String>>) =
        workFlow.first { it.first(this) }.second

    val rating = x + m + a + s
    operator fun get(char: Char) = when (char) {
        'x' -> x
        'm' -> m
        'a' -> a
        's' -> s
        else -> error("invalid parameter")
    }
}

data class XmasRange(
    val x: IntRange,
    val m: IntRange,
    val a: IntRange,
    val s: IntRange,
) {
    fun applyWorkFlow(workFlow: List<(XmasRange) -> List<Pair<XmasRange, String?>>>) =
        workFlow.fold(emptyList<Pair<XmasRange, String>>() to this) { (mapped, forMapping: XmasRange?), next ->
            val (newMapped, newForMapping) = next(forMapping).partition { it.second != null }
            (mapped + newMapped.filterIsInstance<Pair<XmasRange, String>>()) to (newForMapping.firstOrNull()?.first ?: forMapping)
        }.first

    fun splitAt(char: Char, value: Int) = when (char) {
        'x' -> listOf(copy(x = x.first..<value), copy(x = value..x.last)).filter { !it.x.isEmpty() }
        'm' -> listOf(copy(m = m.first..<value), copy(m = value..m.last)).filter { !it.m.isEmpty() }
        'a' -> listOf(copy(a = a.first..<value), copy(a = value..a.last)).filter { !it.a.isEmpty() }
        's' -> listOf(copy(s = s.first..<value), copy(s = value..s.last)).filter { !it.s.isEmpty() }
        else -> error("invalid parameter")
    }
}

fun singlePartRule(rule: String): Pair<(XmasPart) -> Boolean, String> {
    val split = rule.split(":")
    if (split.size == 1) return { _: XmasPart -> true } to split.first()
    val condition = split.first()
    val char = condition.first()
    val comp = condition[1]
    val value = condition.drop(2).toInt()
    return { part: XmasPart ->
        if (comp == '>') part[char] > value
        else part[char] < value
    } to split[1]
}

fun rangeRule(rule: String): (XmasRange) -> List<Pair<XmasRange, String?>> {
    val split = rule.split(":")
    if (split.size == 1) return { range: XmasRange -> listOf(range to split.first()) }
    val condition = split.first()
    val char = condition.first()
    val comp = condition[1]
    val value = condition.drop(2).toInt()
    return { range: XmasRange ->
        val splitAt = range.splitAt(char, value + if (comp == '>') 1 else 0)
        if (splitAt.size == 1) listOf(splitAt.first() to split[1])
        else {
            val (small, large) = splitAt
            listOf(small to split[1].takeIf { comp == '<' }, large to split[1].takeIf { comp == '>' })
        }
    }
}

fun XmasPart(input: String) = input
    .split(",")
    .map { p -> p.filter { it.isDigit() }.toInt() }
    .let { (x, m, a, s) -> XmasPart(x, m, a, s) }

fun singlePartWorkFlow(input: String) = input.split("{").let { (name, flow) ->
    name to flow.dropLast(1).split(",").map { singlePartRule(it) }
}

fun rangeWorkFlow(input: String) = input.split("{").let { (name, flow) ->
    name to flow.dropLast(1).split(",").map { rangeRule(it) }
}


fun main() {
    val fullrange = XmasRange(1..4000, 1..4000, 1..4000, 1..4000)

    fun part1(input: String) = input.split("\n\n").let { (workflows, parts) ->
        workflows.split("\n").map { singlePartWorkFlow(it) }.associate { it } to
                parts.split("\n").map { XmasPart(it) }
    }.let { (workflows, parts) ->
        parts.map { part ->
            part to generateSequence("in") { flow -> part.applyWorkFlow(workflows[flow]!!) }
                .first { it == "A" || it == "R" }
        }
    }.sumOf { (part, accept) ->
        part.takeIf { accept == "A" }?.rating ?: 0
    }

    fun part2(input: String) = input
        .split("\n\n")
        .first()
        .split("\n")
        .map { rangeWorkFlow(it) }
        .associate { it }
        .let { rules ->
            generateSequence(listOf(fullrange to "in")) { rangePair ->
                rangePair.flatMap { (range, name) ->
                    when(name) {
                        "A" -> listOf(range to name)
                        "R" -> emptyList()
                        else -> range.applyWorkFlow(rules[name]!!)
                    }
                }
            }
        }
        .first { it.all { (_, name) -> name == "A" } }
        .map { it.first }
        .sumOf { it.x.count().toLong() * it.m.count() *it.a.count() *it.s.count() }

    val testInput = readInputAsText("Day19Test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000L)


    val input = readInputAsText("Day19")
    part1(input).println()
    part2(input).println()
}