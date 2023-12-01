val textDigits = mapOf("one" to "1", "two" to "2", "three" to "3", "four" to "4", "five" to "5", "six" to "6", "seven" to "7", "eight" to "8", "nine" to "9")
val numberDigits = textDigits.mapKeys { it.value }
val digits = textDigits.entries + numberDigits.entries
val maxTextDigitLength = textDigits.keys.maxOf { it.length }

fun main() {

    fun part1(input: List<String>) =
            input.sumOf { char -> char.filter { it.isDigit() }.map { it.toString() }.let { it.first() + it.last() }.toInt() }

    fun part2(input: List<String>): Int {
        fun String.firstDigit() = digits.filter { it.key in this }.minByOrNull { indexOf(it.key) }?.value
        fun String.valueOf() = windowed(maxTextDigitLength, partialWindows = true)
                .mapNotNull(String::firstDigit)
                .let { it.first() + it.last() }
                .toInt()

        return input.sumOf(String::valueOf)
    }

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
