import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

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

/**
 * Split a list by a predicate. The items that match the predicate will not be part of the result.
 */
inline fun <T> Iterable<T>.split(predicate: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<List<T>>()
    var currentList = mutableListOf<T>()
    this.forEach { item ->
        if (predicate(item)) {
            result.add(currentList)
            currentList = mutableListOf()
        } else {
            currentList.add(item)
        }
    }
    result.add(currentList)
    return result
}

fun Iterable<Int>.product(): Int {
    return reduce { product, factor -> product * factor }
}

fun Iterable<Long>.product(): Long {
    return reduce { product, factor -> product * factor }
}

fun computeLeastCommonMultiple(num1: Long, num2: Long): Long {
    var multiple1 = num1
    var multiple2 = num2
    while (multiple1 != multiple2) {
        if (multiple1 < multiple2) {
            multiple1 += num1
        } else {
            multiple2 += num2
        }
    }
    return multiple1
}

fun computeLeastCommonMultiple(nums: List<Long>): Long {
    var multiple = nums.first()
    for (i in 1 until nums.size) {
        multiple = computeLeastCommonMultiple(multiple, nums[i])
    }
    return multiple
}

fun String.replace(index: Int, char: Char) =
    replaceRange(index..index, "$char")
