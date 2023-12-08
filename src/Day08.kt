typealias Problem = Pair<Sequence<Char>, Map<String, Pair<String, String>>>

val Problem.instructions get() = first
val Problem.map get() = second

fun main() {
    fun List<String>.parseMap() = associate { line -> line.split(" = ").let { (k, v) -> k to v.asPair() } }

    fun List<String>.parseProblem() = let {
        it.first() to it.drop(2)
    }.let { (instructions, map) ->
        instructions.endLessRepeat() to map.parseMap()
    }

    fun Problem.solve(startPoint: String, isEndNode: (String) -> Boolean) =
        instructions.scan(startPoint) { acc, direction ->
            when (direction) {
                'L' -> map[acc]!!.first
                'R' -> map[acc]!!.second
                else -> error("wrong direction")
            }
        }.takeWhile { !isEndNode(it) }.count()

    fun part1(input: List<String>) = input.parseProblem().solve("AAA") { it == "ZZZ" }

    fun part2(input: List<String>) = input.parseProblem().let { problem ->
        problem.map.keys.filter { it.endsWith("A") }.map { startPoint ->
            problem.solve(startPoint) { it.endsWith("Z") }
        }
    }.map(Int::toLong).lcm()

    val testInput = readInput("Day08Test")
    check(part1(testInput) == 2)

    val testInput2 = readInput("Day08Test2")
    check(part1(testInput2) == 6)

    val testInput3 = readInput("Day08Test3")
    check(part2(testInput3) == 6L)

    val input = readInput("Day08")

    part1(input).println()
    part2(input).println()
}
