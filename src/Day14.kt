typealias DishPosition = Pair<Int, Int>

val DishPosition.x get() = first
val DishPosition.y get() = second

data class Dish(val maxY: Int, val maxX: Int, val rocks: List<DishPosition>, val stones: List<DishPosition>) {
    constructor(input: List<String>) : this(
        input.size,
        input[0].length,
        input.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { column, c -> if (c == '#') column to row else null }
        },
        input.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { column, c -> if (c == 'O') column to row else null }
        }
    )

    private val yRocks = rocks.groupBy({ it.x }) { it.y }
    private val yStones = stones.groupBy({ it.x }) { it.y }
    private val xRocks = rocks.groupBy({ it.y }) { it.x }
    private val xStones = stones.groupBy({ it.y }) { it.x }

    val northLoad = stones.sumOf { stone -> maxY - stone.y }
    fun tiltNorth() = copy(stones = yStones.mapValues { (x, ys) ->
        yRocks.getRanges(x, Direction.N).map { r -> r.first..<(r.first + ys.count { it in r }) }.flatten()
    }.flatMap { (x, ys) -> ys.map { x to it } })

    private fun tiltSouth() = copy(stones = yStones.mapValues { (x, ys) ->
        yRocks.getRanges(x, Direction.S).map { r -> (r.last - ys.count { it in r } + 1)..(r.last) }.flatten()
    }.flatMap { (x, ys) -> ys.map { x to it } })

    private fun tiltEast() = copy(stones = xStones.mapValues { (y, xs) ->
        xRocks.getRanges(y, Direction.E).map { r -> r.first..<(r.first + xs.count { it in r }) }.flatten()
    }.flatMap { (y, xs) -> xs.map { it to y } })

    private fun tiltWest() = copy(stones = xStones.mapValues { (y, xs) ->
        xRocks.getRanges(y, Direction.W).map { r -> (r.last - xs.count { it in r } + 1)..(r.last) }.flatten()
    }.flatMap { (y, xs) -> xs.map { it to y } })

    fun tiltCycle() = tiltNorth().tiltEast().tiltSouth().tiltWest()

    private fun Map<Int, List<Int>>.getRanges(i: Int, direction: Direction) =
        (listOf(0) +
                (this[i]?.map { it + if (direction == Direction.N || direction == Direction.E) 1 else 0 } ?: emptyList()) +
                if (direction == Direction.E || direction == Direction.W) maxY else maxX
                )
            .zipWithNext { a, b -> a..<b }
}

fun main() {

    fun part1(input: List<String>) = Dish(input).tiltNorth().northLoad

    fun part2(input: List<String>): Int {
        var dish = Dish(input)
        val visited = mutableMapOf<Dish, Int>()
        var lastCycle = 0
        for (i in 0..<1000000000) {
            val cycle = visited[dish]
            if(cycle != null){
                lastCycle = cycle
                break
            }
            visited[dish] = i
            dish = dish.tiltCycle()
        }

        val remainingTilts = (1000000000-visited.size) % (visited.size - lastCycle)

        for (i in 0..<remainingTilts) {
            dish = dish.tiltCycle()
        }
        return dish.northLoad
    }


    val testInput = readInput("Day14Test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
