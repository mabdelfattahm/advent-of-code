@file:Suppress("unused")

import java.nio.charset.Charset
import java.nio.file.Paths
import kotlin.io.path.readLines
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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

object Puzzle4 {
    data class BoardCell(val content: Int, var marked: Boolean = false)
    data class Board(val cells: List<List<BoardCell>>)
    val marker: (Board, Int) -> Board = { b, n -> b.cells.flatten().filterNot { it.marked  }.firstOrNull { it.content == n }?.let { c -> c.marked = true; b; } ?: b }
    val rowChecker: (Board) -> Boolean = { b -> b.cells.any { r -> r.all { it.marked } } }
    val colChecker: (Board) -> Boolean = { b -> b.cells.map { r -> r.map { it.marked } }.reduce { acc, r -> acc.zip(r) {a, b -> a && b } }.any { it } }
    val markAndCheck: (Board, Int) -> Boolean = { b, n -> marker(b, n); rowChecker(b) || colChecker(b); }
    val scoreCalculator: (Board) -> Int = { b -> b.cells.flatten().filterNot { it.marked }.sumOf { it.content } }
    val numbers: (List<String>) -> List<Int> = { it.first().split(",").map { n -> n.toInt() } }
    val boards: (List<String>) -> List<Board> = { it.drop(2).windowed(5, 6) { l -> Board(l.map { ll -> ll.windowed(2, 3).map { c -> BoardCell(c.trim().toInt()) } }) } }
}

fun puzzle4a(): Int {
    return readLines("advent21/puzzle4-input.txt").let {
        val numbers = Puzzle4.numbers(it)
        val boards = Puzzle4.boards(it)
        numbers.map { n -> (boards.firstOrNull { b -> Puzzle4.markAndCheck(b, n) }?.let { b -> Puzzle4.scoreCalculator(b) * n } ?: 0) }.first { s -> s != 0 }
    }
}

fun puzzle4b(): Int {
    return readLines("advent21/puzzle4-input.txt").let {
        val numbers = Puzzle4.numbers(it)
        val boards = Puzzle4.boards(it)
        val solved = mutableListOf<Pair<Puzzle4.Board, Int>>()
        numbers.map { n ->
            boards.filterNot { b -> solved.map { p -> p.first }.contains(b) }
                .forEach { b -> if (Puzzle4.markAndCheck(b, n)) { solved.add(Pair(b, Puzzle4.scoreCalculator(b) * n)) } }
        }
        solved.last().second
    }
}

fun puzzle5a(): Int {
    return readLines("advent21/puzzle5-input.txt")
        .map { it.split("->").flatMap { p -> p.trim().split(",") } }
        .map { (x1, y1, x2, y2) -> Pair(min(x1.toInt(), x2.toInt())..max(x1.toInt(), x2.toInt()), min(y1.toInt(), y2.toInt())..max(y1.toInt(), y2.toInt())) }
        .filter { (xs, ys) -> xs.first == xs.last || ys.first == ys.last }
        .flatMap { (xs, ys) -> xs.flatMap { x -> ys.map { y -> Pair(x, y) } } }
        .groupBy { it }
        .filter { it.value.size > 1 }
        .count()
}

fun puzzle5b(): Int {

    return readLines("advent21/puzzle5-input.txt")
        .map { it.split("->").flatMap { p -> p.trim().split(",").map { it.toInt() } } }
        .filter { (x1, y1, x2, y2) -> x1 == x2 || y1 == y2 || abs(y2 - y1) == abs(x2 - x1)  }
        .fold(mutableMapOf<Pair<Int, Int>, Int>()) { acc, (x1, y1, x2, y2) ->
            if(x1 == x2 || y1 == y2){
                (min(x1,x2)..max(x1,x2)).forEach { x ->
                    (min(y1,y2)..max(y1,y2)).forEach { y ->
                        val point = Pair(x,y)
                        acc[point] = acc.getOrPut(point) {0} + 1
                    }
                }
            } else {
                val slope = (y2 - y1)/(x2 - x1)
                val b = (y1 - x1 * slope)
                (min(x1,x2)..max(x1,x2)).forEach { x ->
                    val point = Pair(x, b + x * slope)
                    acc[point] = acc.getOrPut(point) {0} + 1
                }
            }
            acc
        }
        .filterValues { it > 1 }
        .count()
}

object Puzzle6 {
    fun advance(days: Int): Long {
        return readLines("advent21/puzzle6-input.txt")
            .first()
            .split(",")
            .map { it.toInt() }
            .groupBy { it }
            .mapValues { it.value.size.toLong() }
            .let { g ->
                (0 until days).fold(g) { acc, _ ->
                    val m = mutableMapOf<Int, Long>()
                    acc.entries
                        .sortedBy { it.key }
                        .forEach { e ->
                            when (e.key) {
                                0 -> {
                                    m[6] = e.value
                                    m[8] = e.value
                                }
                                7 -> m[6] = e.value + (m[6]?:0)
                                else -> m[e.key - 1] = e.value
                            }
                        }
                    m
                }
            }
            .values
            .sum()
    }
}

fun puzzle6a(): Int = Puzzle6.advance(80).toInt()

fun puzzle6b(): Long = Puzzle6.advance(256)

object Puzzle7 {
    fun fuelCalc(costFn: (Int, Int) -> Int): Int {
        return readLines("advent21/puzzle7-input.txt")
            .first()
            .split(",")
            .map { it.toInt() }
            .let { ns ->
                val ps = ns.maxOrNull() ?: ns.size
                ns.map { n -> List(ps) { index -> costFn(n, index) } }
                    .reduce { acc, ints -> acc.zip(ints) { a,b -> a+b } }
                    .let { fs -> fs.minOrNull() ?: -1 /* ?.let { fs.indexOf(it) } ?: -1 */ }
            }
    }
}

fun puzzle7a(): Int = Puzzle7.fuelCalc { n, i -> abs(n - i) }

fun puzzle7b(): Int = Puzzle7.fuelCalc { n, i -> ((abs(n - i) + 1) * abs(n - i)) / 2 }

object Puzzle8 {
    val isOne: (String) -> Boolean = { s -> s.length == 2 }
    val isFour: (String) -> Boolean = { s -> s.length == 4 }
    val isSeven: (String) -> Boolean = { s -> s.length == 3 }
    val isEight: (String) -> Boolean = { s -> s.length == 7 }
}

fun puzzle8a(): Int {
    return readLines("advent21/puzzle8-input.txt")
        .flatMap { it.split("|")[1].trim().split(" ") }
        .count { n -> Puzzle8.isOne(n) || Puzzle8.isFour(n) || Puzzle8.isSeven(n) || Puzzle8.isEight(n) }
}

fun puzzle8b(): Int {

    fun decode(l: List<String>): Map<String, Int> {
        val one = l.first(Puzzle8.isOne) // cf
        val four = l.first(Puzzle8.isFour) // bcdf
        val seven = l.first(Puzzle8.isSeven) // acf
        val eight = l.first(Puzzle8.isEight) // abcdefg

        val rem = l.filterNot { it == one || it == four || it == seven || it == eight }
        val sixPieces = rem.filter { it.length == 6 } // six pieces are 0, 6, 9
        val fivePieces = rem.filter { it.length == 5 } // five pieces are 2, 3, 5
        val aeg = eight.filterNot { four.contains(it) }
        val bdeg = eight.filterNot { seven.contains(it) }
        val bdegPieces = rem.filter { s -> bdeg.all { s.contains(it) }  }
        val aegPieces = rem.filter { s -> aeg.all { s.contains(it) } } // aeg pieces should be 0, 2, 6

        val two = fivePieces.first { aegPieces.contains(it) } // only 2 (acdeg) in the five piece group and aeg group
        val six = bdegPieces.first() // bdeg piece should be 6 (abdefg) only
        val zero = aegPieces.filterNot { it == two || it == six }.first() // zero (abcefg) is the remaining piece in the aeg group
        val nine = sixPieces.filterNot { it == zero || it == six }.first() // nine (abcdfg) is the remaining piece in the five pieces group
        val three = fivePieces.filterNot { it == two }.first { s -> one.all { s.contains(it) } } // three (acdfg) is the only element that contains both pieces of one
        val five = fivePieces.filterNot { it == two || it == three }.first() // five (acdfg) is the remaining piece

        return mapOf(
            zero to 0, one to 1, two to 2, three to 3, four to 4, five to 5, six to 6, seven to 7, eight to 8, nine to 9
        ).mapKeys { it.key.toSortedSet().joinToString("") }
    }

    fun convert(l: List<String>, m: Map<String, Int>): List<Int> {
        return l.map { s -> m[s]!! }
    }

    return readLines("advent21/puzzle8-input.txt")
        .map { it.split("|").map { p -> p.trim().split(" ") } }
        .map { (all, ns) -> convert(ns.map { s -> s.toSortedSet().joinToString("") }, decode(all)) }
        .sumOf { it.joinToString("").toInt() }
}

fun main() {
    println(puzzle8b())
}