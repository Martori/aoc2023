import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

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

infix fun Long.lcm(b: Long): Long {
    val larger = maxOf(this, b)
    val maxLcm = this * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % this == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

infix fun Int.lcm(b: Int): Int {
    val larger = maxOf(this, b)
    val maxLcm = this * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % this == 0 && lcm % b == 0) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

fun List<Int>.lcm() = reduce(Int::lcm)
fun List<Long>.lcm() = reduce(Long::lcm)