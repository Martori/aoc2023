enum class TileType(val char: Char, val connections: Set<Direction>) {
    NS('|', setOf(Direction.N, Direction.S)),
    EW('-', setOf(Direction.E, Direction.W)),
    NE('L', setOf(Direction.N, Direction.E)),
    NW('J', setOf(Direction.N, Direction.W)),
    WS('7', setOf(Direction.W, Direction.S)),
    ES('F', setOf(Direction.E, Direction.S)),
    G('.', emptySet()),
    S('S', Direction.entries.toSet());

    companion object {
        val map = entries.associateBy { it.char }
        operator fun get(char: Char) = map[char]!!
    }

    override fun toString() = char.toString()
}

enum class Direction {
    N {
        override fun reverse() = S
    },
    S {
        override fun reverse() = N
    },
    E {
        override fun reverse() = W
    },
    W {
        override fun reverse() = E
    };

    abstract fun reverse(): Direction
}

data class Position(val x: Int, val y: Int)

typealias TileMatrix = List<List<Tile>>

operator fun TileMatrix.get(position: Position) = runCatching { this[position.y][position.x] }.getOrNull()

data class Tile(val type: TileType, val position: Position)

data class TileMap(
    val start: Tile, val matrix: TileMatrix
) {
    constructor(tileMatrix: TileMatrix) : this(tileMatrix.flatten().first { it.type == TileType.S }, tileMatrix)

    private fun Tile.move(direction: Direction) = when (direction) {
        Direction.N -> position.copy(y = position.y - 1)
        Direction.S -> position.copy(y = position.y + 1)
        Direction.E -> position.copy(x = position.x + 1)
        Direction.W -> position.copy(x = position.x - 1)
    }.let { matrix[it] }

    private fun Tile.neighbours() =
        type.connections.mapNotNull { direction ->
            move(direction)?.takeIf { direction.reverse() in it.type.connections }
        }

    private fun Tile.nextFrom(prev: Tile) = neighbours().first { it != prev }

    fun buildLoop() =
        listOf(start) + generateSequence(start to start.neighbours().first()) { (prev, current) ->
            current to current.nextFrom(prev)
        }.map { it.second }.takeWhile { it != start }.toList()
}

fun main() {
    fun List<String>.toTileMap() = mapIndexed { y, line ->
        line.mapIndexed() { x, char -> Tile(TileType[char], Position(x, y)) }
    }.let { TileMap(it) }

    fun part1(input: List<String>) = input.toTileMap().buildLoop().size / 2
    fun part2(input: List<String>) = 5

    val testInput = readInput("Day10Test")

    val testInput2 = readInput("Day10Test2")

    testInput.toTileMap().buildLoop()

    check(part1(testInput) == 4)
    check(part1(testInput2) == 8)
//    check(part2(testInput) == 5)

    val input = readInput("Day10")
    part1(input).println()
    check(part1(input) == 6864)
//    part2(input).println()
}


