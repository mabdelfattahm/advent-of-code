import java.nio.charset.Charset
import java.nio.file.Paths
import kotlin.io.path.readLines

fun readLines(path: String): List<String> =
    {}::class.java.getResource(path)?.let { Paths.get(it.toURI()).readLines(Charset.defaultCharset()) } ?: emptyList()

fun puzzle1a(): Int =
    readLines("advent21/puzzle1-input.txt")
        .mapNotNull { it.toIntOrNull() }
        .zipWithNext { a, b -> b > a }
        .count { it }

fun puzzle1b(): Int =
    readLines("advent21/puzzle1-input.txt")
        .mapNotNull { it.toIntOrNull() }
        .windowed(3, 1)
        .zipWithNext { a, b -> a.sum() < b.sum() }
        .count { it }

fun puzzle2a(): Int =
    readLines("advent21/puzzle2-input.txt")
        .map {
            val pts = it.split(" ", limit = 2)
            Pair(pts[0], pts[1].toInt())
        }
        .fold(Pair(0, 0)) { acc, p ->
            when(p.first) {
                "forward" -> Pair(acc.first + p.second, acc.second)
                "down" -> Pair(acc.first, acc.second + p.second)
                "up" -> Pair(acc.first, acc.second - p.second)
                else -> acc
            }
        }
        .let { it.first * it.second }

fun puzzle2b(): Int =
    readLines("advent21/puzzle2-input.txt")
        .map {
            val pts = it.split(" ", limit = 2)
            Pair(pts[0], pts[1].toInt())
        }
        .fold(Triple(0, 0, 0)) { acc, p ->
            when(p.first) {
                "forward" -> Triple(acc.first, acc.second + p.second, acc.third + (acc.first * p.second))
                "down" -> Triple(acc.first + p.second, acc.second, acc.third)
                "up" -> Triple(acc.first - p.second, acc.second, acc.third)
                else -> acc
            }
        }
        .let { it.second * it.third }


fun main() {
    println(puzzle2b())
}