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


fun puzzle3a(): Int =
    readLines("advent21/puzzle3-input.txt")
        .map { it.toList() }
        .fold(mutableMapOf<Int, MutableMap<Char, Int>>()) { acc, l ->
            l.forEachIndexed { index, s ->
                val m = acc.getOrPut(index) { mutableMapOf() }
                m[s] = m.getOrPut(s) { 0 } + 1
            }
            acc
        }
        .entries
        .sortedBy { it.key }
        .fold(Pair(mutableListOf<Char>(), mutableListOf<Char>())) { acc, e ->
            val s = e.value.entries.sortedBy { it.value }
            acc.first.add(e.key, s.first().key)
            acc.second.add(e.key, s.last().key)
            acc
        }.let { it.first.joinToString("").toInt(2) * it.second.joinToString("").toInt(2) }

fun puzzle3b(): Int {
    val input = readLines("advent21/puzzle3-input.txt").map { it.toList() }
    val length = input.first().size

    val o2cond: (Int, Int) -> Char = { s0, s1 -> if(s0 == s1) { '1' } else if(s0 > s1) { '0' } else { '1' } }
    val co2cond: (Int, Int) -> Char = { s0, s1 -> if(s0 == s1) { '0' } else if(s0 > s1) { '1' } else { '0' } }
    val filter: (Int, List<List<Char>>, (Int, Int) -> Char) -> List<List<Char>> = { idx, list, cond ->
        if (list.size == 1) { list } else {
            list.let { l ->
                var s0 = 0
                var s1 = 0
                l.map { it[idx] }.forEach { if (it == '0') { s0 += 1 } else { s1 += 1 } }
                val c = cond(s0, s1)
                l.filter { it[idx] == c }.toMutableList()
            }
        }
    }

    tailrec fun calc(list0: List<List<Char>> = input.toMutableList(), list1: List<List<Char>> = input.toMutableList(), index: Int = 0): Int =
        if (index < length) {
            calc(filter(index, list0, o2cond).toMutableList(), filter(index, list1, co2cond).toMutableList(), index + 1)
        } else {
            list0.first().joinToString("").toInt(2) * list1.first().joinToString("").toInt(2)
        }

    return calc()

}

fun main() {
    println(puzzle3b())
}