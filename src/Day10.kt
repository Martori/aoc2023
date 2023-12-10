enum class TileType(val char: Char, val connections: Set<Direction>) {
    NS('|', setOf(Direction.N, Direction.S)),
    EW('-', setOf(Direction.E, Direction.W)),
    NE('L', setOf(Direction.N, Direction.E)),
    NW('J', setOf(Direction.N, Direction.W)),
    SW('7', setOf(Direction.W, Direction.S)),
    SE('F', setOf(Direction.E, Direction.S)),
    G('.', emptySet()),
    Start('S', Direction.entries.toSet());

    companion object {
        val map = entries.associateBy { it.char }
        operator fun get(char: Char) = map[char]!!
    }

    override fun toString() = char.toString()
}

enum class Direction {
    N, S, E, W
}

val Direction.reverse
    get() = when (this) {
        Direction.N -> Direction.S
        Direction.S -> Direction.N
        Direction.E -> Direction.W
        Direction.W -> Direction.E
    }

enum class Side {
    Outside, Inside, Loop
}

operator fun Side.not() = when (this) {
    Side.Outside -> Side.Inside
    Side.Inside -> Side.Outside
    Side.Loop -> Side.Loop
}

data class Position(val x: Int, val y: Int) {
    fun move(direction: Direction) = when (direction) {
        Direction.N -> copy(y = y - 1)
        Direction.S -> copy(y = y + 1)
        Direction.E -> copy(x = x + 1)
        Direction.W -> copy(x = x - 1)
    }
}

typealias TileMatrix = List<List<Tile>>

operator fun TileMatrix.get(position: Position) = runCatching { this[position.y][position.x] }.getOrNull()

data class Tile(val type: TileType, val position: Position, var side: Side? = null)

data class TileMap(
    val start: Tile, val matrix: TileMatrix
) {

    constructor(tileMatrix: TileMatrix) : this(tileMatrix.flatten().first { it.type == TileType.Start }, tileMatrix)

    private fun Tile.move(direction: Direction) =
        matrix[position.move(direction)]

    private fun Tile.getConnectingNeighbours() =
        type.connections.mapNotNull { direction ->
            move(direction)?.takeIf { direction.reverse in it.type.connections }
        }

    private fun Tile.nextFrom(prev: Tile) = getConnectingNeighbours().first { it != prev }

    val mainLoop by lazy {
        listOf(start) + generateSequence(start to start.getConnectingNeighbours().first()) { (prev, current) ->
            current to current.nextFrom(prev)
        }.map { it.second }.takeWhile { it != start }.toList()
    }

    val insideTiles by lazy {
        matrix.flatten().filter { it.side == Side.Inside }
    }

    private fun Tile.canMoveInDirection(direction: Direction) = position.move(direction) in getConnectingNeighbours().map { it.position }

    private val startShape by lazy {
        if (start.canMoveInDirection(Direction.N)) {
            if (start.canMoveInDirection(Direction.S)) TileType.NS
            else if (start.canMoveInDirection(Direction.E)) TileType.NE
            else TileType.NW
        } else if (start.canMoveInDirection(Direction.S)) {
            if (start.canMoveInDirection(Direction.E)) TileType.SE
            else TileType.SW
        } else {
            TileType.EW
        }
    }
    private val Tile.actualType get() = if (type == TileType.Start) startShape else type

    init {
        markSides()
    }

    private fun markSides() {
        mainLoop.forEach { it.side = Side.Loop }
        matrix.forEach { line ->
            line.fold<Tile, Pair<Side, TileType?>>(Side.Outside to null) { (side, previousType), tile ->
                if (tile.side == null) {
                    tile.side = side
                    side to previousType
                } else computeNextStep(tile.actualType, side, previousType)
            }
        }
    }

    private fun computeNextStep(type: TileType, side: Side, previous: TileType?) = when {
        type == TileType.NS -> !side to previous
        previous == null -> side to type
        else -> {
            when {
                previous.connections.contains(Direction.N) -> when {
                    type.connections.contains(Direction.N) -> side to null
                    type.connections.contains(Direction.S) -> !side to null
                    else -> side to previous
                }

                previous.connections.contains(Direction.S) -> when {
                    type.connections.contains(Direction.S) -> side to null
                    type.connections.contains(Direction.N) -> !side to null
                    else -> side to previous
                }

                else -> side to previous
            }
        }
    }

}

fun main() {
    fun List<String>.toTileMap() = mapIndexed { y, line ->
        line.mapIndexed { x, char -> Tile(TileType[char], Position(x, y)) }
    }.let { TileMap(it) }

    fun part1(input: List<String>) = input.toTileMap().mainLoop.size / 2
    fun part2(input: List<String>) = input.toTileMap().insideTiles.count()

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}


