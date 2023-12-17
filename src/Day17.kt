val Direction.left
    get() = when (this) {
        Direction.N -> Direction.W
        Direction.S -> Direction.E
        Direction.E -> Direction.N
        Direction.W -> Direction.S
    }
val Direction.right
    get() = when (this) {
        Direction.N -> Direction.E
        Direction.S -> Direction.W
        Direction.E -> Direction.S
        Direction.W -> Direction.N
    }

data class Crucible(
    val position: Position,
    val direction: Direction,
    val line: Int
) {
    fun neighbours() = buildList {
        if (line < 3) add(Crucible(position.move(direction), direction, line + 1))
        add(Crucible(position.move(direction.left), direction.left, 1))
        add(Crucible(position.move(direction.right), direction.right, 1))
    }

    fun ultraNeighbours() = buildList {
        if (line < 10) add(Crucible(position.move(direction), direction, line + 1))
        if (line >= 4) {
            add(Crucible(position.move(direction.left), direction.left, 1))
            add(Crucible(position.move(direction.right), direction.right, 1))
        }
    }

    fun isInBounds(grid: List<String>) =
        position.x >= 0 && position.x < grid[0].length && position.y >= 0 && position.y < grid.size
}

fun List<String>.heatLoss(crucible: Crucible) = heatLoss(crucible.position)
fun List<String>.heatLoss(position: Position) = this[position.y][position.x].digitToInt()

data class HeatedCrucible(
    val crucible: Crucible,
    val heat: Int
)

fun main() {
    fun part1(input: List<String>) = findCoolestPath(input) { c -> c.neighbours().filter { it.isInBounds(input) } }

    fun part2(input: List<String>) = findCoolestPath(input) { c -> c.ultraNeighbours().filter { it.isInBounds(input) } }

    val testInput = readInput("Day17Test")

    check(part1(testInput) == 102)
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    part1(input).println()
    part2(input).println()
}

private fun findCoolestPath(testInput: List<String>, neighbours: (Crucible) -> List<Crucible>): Int? {
    val source = Crucible(Position(0, 0), Direction.E, 1)
    val target = Position(testInput[0].lastIndex, testInput.lastIndex)

    val totalHeatLoss = mutableMapOf<Crucible, Int>()
    val previous = mutableMapOf<Crucible, Crucible>()
    val priority: MutableList<HeatedCrucible> = mutableListOf(HeatedCrucible(source, 0))

    totalHeatLoss[source] = 0

    while (priority.isNotEmpty()) {
        priority.sortBy { it.heat }
        val current = priority.first()
        if (current.crucible.position == target) break
        priority.removeAt(0)
        neighbours(current.crucible)
            .filter { it !in previous.keys }
            .forEach { next ->
                val newHeat = totalHeatLoss[current.crucible]!! + testInput.heatLoss(next)
                totalHeatLoss[next] = newHeat
                previous[next] = current.crucible
                val nextScored = HeatedCrucible(next, newHeat)
                if (nextScored !in priority) priority.add(nextScored)
            }
    }

    val key = totalHeatLoss.keys.first { it.position == target }

    return totalHeatLoss[key]
}

