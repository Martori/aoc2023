data class LightRay(
    val position: Position,
    val direction: Direction
) {

    override fun toString(): String {
        return "(${position.x},${position.y})$direction"
    }

    fun move(grid: List<String>) = copy(position = position.move(direction)).takeIf { it.isInBounds(grid) }

    private fun isInBounds(grid: List<String>) =
        position.x >= 0 && position.x < grid[0].length &&
                position.y >= 0 && position.y < grid.size

    fun split(grid: List<String>): List<LightRay> =
        listOfNotNull(rightReflection().move(grid), leftReflection().move(grid))

    /* / */
    fun rightReflection(): LightRay = copy(
        direction = when (direction) {
            Direction.N -> Direction.E
            Direction.S -> Direction.W
            Direction.E -> Direction.N
            Direction.W -> Direction.S
        }
    )

    /* \ */
    fun leftReflection(): LightRay = copy(
        direction = when (direction) {
            Direction.N -> Direction.W
            Direction.S -> Direction.E
            Direction.E -> Direction.S
            Direction.W -> Direction.N
        }
    )

    val isVertical = direction == Direction.N || direction == Direction.S
    val ishorizontal = direction == Direction.W || direction == Direction.E
}

private fun List<String>.atPosition(elem: LightRay) = this[elem.position.y][elem.position.x]


fun main() {
    tailrec fun List<String>.findVisitedLightraysFunctional(toVisit: List<LightRay>, visited: Set<LightRay>): Set<LightRay> {
        val toVisit2 = toVisit.filter { it !in visited }
        if (toVisit2.isEmpty()) return visited
        val current = toVisit2.first()
        val nextNodes = toVisit2.drop(1) +
                when (atPosition(current)) {
                    '.' -> listOfNotNull(current.move(this))
                    '-' -> if (current.ishorizontal) listOfNotNull(current.move(this)) else current.split(this)
                    '|' -> if (current.isVertical) listOfNotNull(current.move(this)) else current.split(this)
                    '/' -> listOfNotNull(current.rightReflection().move(this))
                    '\\' -> listOfNotNull(current.leftReflection().move(this))
                    else -> emptyList()
                }
        return findVisitedLightraysFunctional(nextNodes, visited + current)
    }

    fun List<String>.findAmountOfEnergizedTilesFunctional(initialRay: LightRay) =
        findVisitedLightraysFunctional(listOfNotNull(initialRay), emptySet()).distinctBy { it.position }.size

    fun List<String>.findAmountOfEnergizedTiles(initialRay: LightRay): Int {
        val toVisit = mutableListOf<LightRay?>(initialRay)
        val visited = mutableSetOf<LightRay>()
        while (toVisit.isNotEmpty()) {
            val current = toVisit.first()
            toVisit.removeAt(0)
            if (current in visited || current == null) continue
            visited.add(current)
            val char = atPosition(current)
            when (char) {
                '.' -> toVisit += current.move(this)
                '-' -> if (current.ishorizontal) toVisit += current.move(this) else toVisit += current.split(this)
                '|' -> if (current.isVertical) toVisit += current.move(this) else toVisit += current.split(this)
                '/' -> toVisit += current.rightReflection().move(this)
                '\\' -> toVisit += current.leftReflection().move(this)
            }
        }

        return visited.distinctBy { it.position }.count()
    }

    fun part1(input: List<String>) = input.findAmountOfEnergizedTiles(LightRay(Position(0, 0), Direction.E))

    fun part2(input: List<String>) = maxOf(
        input[0].indices.fold(0) { acc, i ->
            maxOf(
                acc,
                input.findAmountOfEnergizedTiles(LightRay(Position(i, 0), Direction.S)),
                input.findAmountOfEnergizedTiles(LightRay(Position(i, input.lastIndex), Direction.N))
            )
        },
        input.indices.fold(0) { acc, i ->
            maxOf(
                acc,
                input.findAmountOfEnergizedTiles(LightRay(Position(input[0].lastIndex, i), Direction.W)),
                input.findAmountOfEnergizedTiles(LightRay(Position(0, i), Direction.E)),
            )
        })

    val testInput = readInput("Day16Test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}

