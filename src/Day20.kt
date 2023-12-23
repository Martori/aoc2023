sealed interface PulseModule {
    val name: String
    val connectionNames: List<String>
    fun processPulse(pulse: Pulse): List<Pulse>
    fun Pulse(destinationName: String, high: Boolean) = Pulse(this, destinationName, high)
}

data object Button : PulseModule {
    override val name = "button"
    override val connectionNames = emptyList<String>()
    override fun processPulse(pulse: Pulse) = emptyList<Pulse>()
}

fun buttonPulse() = Pulse(Button, "broadcaster", false)

data class FlipFlop(
    override val name: String,
    override val connectionNames: List<String>,
) : PulseModule {

    private var state: Boolean = false
    override fun processPulse(pulse: Pulse) = run {
        if (!pulse.high) {
            state = !state
            connectionNames.map { Pulse(it, state) }
        } else emptyList()
    }
}

data class Broadcaster(override val connectionNames: List<String>) : PulseModule {
    override val name: String = "broadcaster"
    override fun processPulse(pulse: Pulse) = connectionNames.map { Pulse(it, pulse.high) }
}

data class Conjunction(override val name: String, override val connectionNames: List<String> = emptyList()) : PulseModule {

    private val inputs: MutableMap<String, Boolean> = mutableMapOf()

    fun setInputs(values: List<String>) {
        values.forEach { inputs[it] = false }
    }

    override fun processPulse(pulse: Pulse) = run {
        inputs[pulse.emmiter.name] = pulse.high
        connectionNames.map { dest -> Pulse(dest, !inputs.values.all { it }) }
    }
}

data class Pulse(val emmiter: PulseModule, val destinationName: String, val high: Boolean)

private fun String.extractConnections() = split(" -> ")[1].split(", ")
private fun String.extractNameAndConnections() = split(" -> ").let { (name, cons) -> name.drop(1) to cons.split(", ") }

tailrec fun pulsate(map: Map<String, PulseModule>, pulses: List<Pulse>, highs: List<Pulse?> = emptyList(), lows: List<Pulse?> = emptyList()): Pair<List<Pulse>, List<Pulse>> {
    if (pulses.isEmpty()) return highs.filterNotNull() to lows.filterNotNull()

    val toProcess = pulses.first()
    val others = pulses.drop(1)
    val more = map[toProcess.destinationName]?.processPulse(toProcess) ?: emptyList()

    return pulsate(map, others + more, highs + toProcess.takeIf { it.high }, lows + toProcess.takeUnless { it.high })
}

fun main() {

    fun part1(input: List<String>) = input
        .buildMap()
        .let { map ->
            (0..<1000)
                .map {
                    pulsate(map, listOf(buttonPulse()))
                }
                .fold(0L to 0L) { (high, low), (h, l) ->
                    high + h.size to low + l.size
                }.let { (h, l) -> h * l }
        }

    fun part2(input: List<String>) = input.count()

    val testInput = readInput("Day20Test")
    check(part1(testInput) == 32000000L)

    val input = readInput("Day20")
    part1(input).println()
    part2(input).println()
}

private fun List<String>.buildMap() = map {
    when (it.first()) {
        '%' -> it.extractNameAndConnections().let { (name, cons) -> FlipFlop(name, cons) }
        '&' -> it.extractNameAndConnections().let { (name, cons) -> Conjunction(name, cons) }
        else -> Broadcaster(it.extractConnections())
    }
}
    .associateBy { it.name }
    .let { map ->
        map.values.filterIsInstance<Conjunction>().forEach { module ->
            module.setInputs(map.filterValues { module.name in it.connectionNames }.map { it.key })
        }
        map
    }


