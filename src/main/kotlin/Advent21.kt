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

fun main(args: Array<String>) {
    println(puzzle1b())
}