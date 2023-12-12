import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.math.pow

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()
fun readInputAsText(name: String) = Path("src/$name.txt").readText()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun String.asPair() = removeSurrounding("(", ")").split(", ").let { (f, s) -> f to s }
fun String.endLessRepeat() = sequence { while (true) this.yieldAll(toList()) }
fun <T> List<T>.endLessRepeat() = sequence { while (true) this.yieldAll(this@endLessRepeat) }
fun <T> List<T>.repeatIt(times: Int) = sequence {
    repeat(times) {
        this.yieldAll(this@repeatIt)
    }
}.toList()

infix fun Long.lcm(b: Long): Long {
    val larger = maxOf(this, b)
    val maxLcm = this * b
    return (larger..maxLcm step larger).firstOrNull { it % this == 0L && it % b == 0L } ?: maxLcm
}

infix fun Int.lcm(b: Int): Int = (toLong() lcm b.toLong()).toInt()

fun List<Int>.lcm() = reduce(Int::lcm)
fun List<Long>.lcm() = reduce(Long::lcm)

fun Int.pow(i: Int) = toDouble().pow(i).toInt()