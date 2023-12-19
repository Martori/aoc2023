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

fun Rule(rule: String): Pair<(XmasPart) -> Boolean, String> {
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

fun XmasPart(input: String) = input
    .split(",")
    .map { p -> p.filter { it.isDigit() }.toInt() }
    .let { (x, m, a, s) -> XmasPart(x, m, a, s) }

fun WorkFlow(input: String) = input.split("{").let { (name, flow) ->
    name to flow.dropLast(1).split(",").map { Rule(it) }
}


fun main() {
    fun part1(input: String) = input.split("\n\n").let { (workflows, parts) ->
        workflows.split("\n").map { WorkFlow(it) }.associate { it } to
                parts.split("\n").map { XmasPart(it) }
    }.let { (workflows, parts) ->
        parts.map { part ->
            part to generateSequence("in") { flow -> part.applyWorkFlow(workflows[flow]!!) }
                .first { it == "A" || it == "R" }
        }
    }.sumOf { (part, accept) ->
        part.takeIf { accept == "A" }?.rating ?: 0
    }

    fun part2(input: String) = 5

    val testInput = readInputAsText("Day19Test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 5)


    val input = readInputAsText("Day19")
    part1(input).println()
    part2(input).println()
}

