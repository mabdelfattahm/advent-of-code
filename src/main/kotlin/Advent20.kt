import java.nio.charset.Charset
import java.nio.file.Paths
import kotlin.io.path.readLines
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    puzzle12()
}

fun puzzle1() {
    val numbers =
        {}::class.java.getResource("advent20/puzzle1-input.txt")?.let {
            Paths
                .get(it.toURI())
                .readLines(Charset.defaultCharset())
                .mapNotNull { s -> s.toIntOrNull() }
                .toSet()
        } ?: emptySet()

    fun puzzle1a(): Int {
        for (n in numbers) {
            val rem = 2020 - n
            if (numbers.contains(rem)) {
                println("$n, $rem")
                println(n * rem)
                return n * rem
            }
        }
        return -1
    }

    fun puzzle1b(): Int {
        for (n in numbers) {
            val rem = 2020 - n
            for (m in numbers) {
                val r = rem - m
                if (numbers.contains(r)) {
                    println("$n, $m, $r")
                    println(n * m * r)
                    return n * m * r
                }
            }
        }
        return -1
    }
    puzzle1a()
    puzzle1b()
}

fun puzzle2() {

    val regex = Regex("^(?<min>[^-]+)-(?<max>[^\\s]+)\\s(?<ch>[^:]+):\\s(?<word>.*)$")

    val policy2a: (String) -> Boolean = { inp ->
        when (val res = regex.find(inp)) {
            null -> false
            else -> {
                val (min, max, ch, word) = res.destructured
                val count = word.count { it == ch[0] }
                count in min.toInt() .. max.toInt()
            }
        }
    }

    val policy2b: (String) -> Boolean = { inp ->
        when (val res = regex.find(inp)) {
            null -> false
            else -> {
                val (f, s, ch, word) = res.destructured
                val first = f.toInt() - 1
                val second = s.toInt() - 1
                val sanity = word.length > second && word.length > first
                sanity && (word[first] == ch[0]).xor(word[second] == ch[0])
            }
        }
    }

    fun puzzle2a(): Int {
        return {}::class.java.getResource("advent20/puzzle2-input.txt")?.let {
            Paths
                .get(it.toURI())
                .readLines(Charset.defaultCharset())
                .count(policy2a)
        } ?: 0
    }

    fun puzzle2b(): Int {
        return {}::class.java.getResource("advent20/puzzle2-input.txt")?.let {
            Paths
                .get(it.toURI())
                .readLines(Charset.defaultCharset())
                .count(policy2b)
        } ?: 0
    }

    println(puzzle2a())
    println(puzzle2b())
}

fun puzzle3() {
    val input = {}::class.java.getResource("advent20/puzzle3-input.txt")?.let {
        Paths
            .get(it.toURI())
            .readLines(Charset.defaultCharset())
            .map { s -> s.toCharArray() }
    } ?: listOf()

    data class NavigationResult(val x: Int, val y: Int, val t: Int)

    val w = input[0].size

    fun navigate(sd: Int, sr: Int, x: Int = 0, y: Int = 0, t: Int = 0): NavigationResult {
        return if (y >= input.size) {
            NavigationResult(x, y, t)
        } else {
            val nx = (x + sr) % w
            val ny = y + sd
            if (input[y][x] == '#') {
                navigate(sd, sr, x = nx, y = ny,t = t + 1)
            } else {
                navigate(sd, sr, x = nx, y = ny,t = t)
            }
        }
    }

    fun navigator(): Long {
        return listOf(Pair(1,1), Pair(3,1), Pair(5,1), Pair(7,1), Pair(1,2))
            .map { navigate(sr = it.first, sd = it.second) }
            .fold(1) { acc, r ->
                println(r.t)
                acc * r.t
            }
    }

    println(navigator())

}

fun puzzle4() {

    val input = {}::class.java.getResource("advent20/puzzle4-input.txt")?.let {
        Paths
            .get(it.toURI())
            .readLines(Charset.defaultCharset())
            .fold(mutableListOf(mutableMapOf<String, String>())) { acc, l ->
                if (l.isBlank()) {
                    acc.add(mutableMapOf())
                } else {
                    acc.last().putAll(
                        l.split(" ").associate { s ->
                            val att = s.split(":")
                            Pair(att[0], att[1])
                        }
                    )
                }
                acc
            }
    } ?: listOf()

    fun puzzle4a(): Int {
        return input.count { it.keys.containsAll(setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")) }
    }

    fun puzzle4b(): Int {
        val hr = Regex("(\\d+)(in|cm)")
        val cr = Regex("#[0-9a-f]{6}")

        val matchHgt: (String) -> Boolean = { s ->
            hr.find(s)?.let {
                val (v, unit) = it.destructured
                when(unit) {
                    "cm" -> v.toInt() in 150..193
                    "in" -> v.toInt() in 59..76
                    else -> false
                }
            } ?: false
        }

        return input.count {
            listOf(
                (it["byr"] ?: "0").toInt() in 1920..2002,
                (it["iyr"] ?: "0").toInt() in 2010..2020,
                (it["eyr"] ?: "0").toInt() in 2020..2030,
                (it["hcl"]?:"").matches(cr),
                (it["ecl"] ?: "") in setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth"),
                (it["pid"] ?: "0").length == 9,
                matchHgt(it["hgt"] ?: "")
            ).reduce { acc, b -> acc && b }
        }
    }

    println(puzzle4b())
}

fun puzzle5() {
    val input = {}::class.java.getResource("advent20/puzzle5-input.txt")?.let {
        Paths
            .get(it.toURI())
            .readLines(Charset.defaultCharset())
            .map { s -> Pair(s.substring(0..6), s.substring(7)) }
    } ?: listOf()

    fun searIds(): List<Int> {
        return input
            .map {
                val rr = it.first.fold(0..128) { r, c ->
                    val n = (r.first + r.last) / 2
                    when(c) {
                        'F' -> (r.first..n)
                        'B' -> (n..(r.last))
                        else -> r
                    }
                }
                val row = if (it.first.last() == 'F') { rr.first } else { rr.last - 1 }

                val cr = it.second.fold(0..8) { r, c ->
                    val n = (r.first + r.last) / 2
                    when(c) {
                        'L' -> (r.first..n)
                        'R' -> (n..r.last)
                        else -> r
                    }
                }
                val col = if (it.first.last() == 'L') { cr.first } else { cr.last - 1 }
                (row * 8) + col
            }
    }

    fun puzzle5a(): Int {
        return searIds().maxOrNull()?: 0
    }

    fun puzzle5b(): Int {
        val o = searIds().sorted()
        var s = o.first()
        return (o.drop(1).firstOrNull { i ->
            println("$s, $i")
            if (i - s == 2) {
                true
            } else {
                s = i
                false
            }
        } ?: 1) - 1
    }

    println(puzzle5b())
}

fun puzzle6(){
    // every list represents a group
    // every line in a list represents the questions someone answered yes
    val input = {}::class.java.getResource("advent20/puzzle6-input.txt")?.let {
        Paths
            .get(it.toURI())
            .readLines(Charset.defaultCharset())
            .fold(mutableListOf(mutableListOf<String>())) { acc, l ->
                acc.also { if (l.isBlank()) { acc.add(mutableListOf()) } else { acc.last().add(l) } }
            }
    } ?: listOf()

    fun puzzle6a(): Int {
        return input.map { l ->
            l.fold(mutableSetOf<Char>()) { acc, s ->  acc.also { it.addAll(s.toSet()) } }
        }.sumOf { it.size }
    }

    fun puzzle6b(): Int {
        return input
            .filterNot{ g -> g.isEmpty() }
            .sumOf { g ->
                // start with the first line into set, for each other line remove characters from set if they weren't there
                val s = g.first().toSet().toMutableSet()
                g.drop(1).map(String::toSet).forEach { l -> s.removeIf { c -> !l.contains(c) } }
                s.size
            }
    }

    println(puzzle6b())
}

fun puzzle7() {

    val input = {}::class.java.getResource("advent20/puzzle7-input.txt")?.let {
        Paths
            .get(it.toURI())
            .readLines(Charset.defaultCharset())
            .map { line ->
                line.split(Regex("\\sbags?,?\\.?\\s?(?>contain\\s)?\\b"))
                    .filterNot { s -> s == "." || s.contains( "no other")}
            }
            .associate { r ->
                Pair(r[0], r.drop(1).associate { c ->
                    val rc = c.split(" ", limit = 2)
                    Pair(rc[1].trim(), rc[0].trim().toInt())
                })
            }.filterValues { m -> m.isNotEmpty() }
    } ?: mapOf()


    fun puzzle7a(): Int {

        val memo: MutableMap<String, Boolean> = mutableMapOf()

        fun checkContains(b: String): Boolean {
            return if (memo[b] != null) {
                memo[b]!!
            } else {
                val content = input[b] ?: mapOf()
                val c = content.contains("shiny gold") || content.keys.any { checkContains(it) }
                memo[b] = c
                c
            }
        }
        return input.keys.count{ checkContains(it) }
    }

    fun puzzle7b():Int {

        val memo: MutableMap<String, Int> = mutableMapOf()

        fun countOfBags(b: String): Int {
            return if (memo[b] != null) {
               memo[b]!!
            } else {
                val content = input[b] ?: mapOf()
                val c = content.entries.fold(1) { acc, entry -> acc + (entry.value * countOfBags(entry.key)) }
                memo[b] = c
                c
            }
        }

        return (input["shiny gold"] ?: mapOf()).entries.sumOf { b -> b.value * countOfBags(b.key) }
    }

    println(puzzle7b())
}

fun puzzle8() {

    val input = {}::class.java.getResource("advent20/puzzle8-input.txt")?.let {
        Paths.get(it.toURI())
            .readLines(Charset.defaultCharset())
            .map { s ->
                val p = s.split(" ", limit = 2)
                Pair(p[0], p[1].toInt())
            }
    } ?: listOf()

    fun puzzle8a(): Int{
        val m: MutableSet<Int> = LinkedHashSet()
        val ins: MutableList<Pair<String, Int>> = mutableListOf()
        var acc = 0
        var i = 0
        while(m.contains(i).not()) {
            m.add(i)
            ins.add(input[i])
            when(input[i].first) {
                "acc" -> {
                    acc += input[i].second
                    i += 1
                }
                "jmp" -> {
                    i += input[i].second
                }
                "nop" -> {
                    i+=1
                }
            }
        }
        return acc
    }

    fun puzzle8b(): Int {
        var acc = 0
        var i = 0
        var tmpI = 0
        var tmpAcc = acc
        var tmpV = 0
        var ch = false
        val s = HashSet<Int>()
        var v = mutableListOf<Int>()
        while (i < input.size) {
            val ins = input[i]
            if(!ch && !s.contains(i) && ins.first == "nop") {
                tmpI = i
                tmpAcc = acc
                tmpV = v.size
                ch = true
                v.add(i)
                i += ins.second
                continue
            }
            if(!ch && !s.contains(i) && ins.first == "jmp") {
                tmpI = i
                tmpAcc = acc
                tmpV = v.size
                ch = true
                v.add(i)
                i += 1
                continue
            }
            if (ch && v.contains(i)) {
                s.add(tmpI)
                i = tmpI
                v = v.subList(0, tmpV)
                acc = tmpAcc
                ch = false
                continue
            }
            if (!ch && v.contains(i)) {
                println("fk")
                break
            }
            if (ins.first == "acc") {
                acc += ins.second
                v.add(i)
                i += 1
                continue
            }
            if (ins.first == "jmp") {
                v.add(i)
                i += ins.second
                continue
            }
            if (ins.first == "nop") {
                v.add(i)
                i += 1
                continue
            }
        }
        println("Change $tmpI")
        return acc
    }

    println(puzzle8b())

}

fun puzzle9() {

    val input =
        {}::class.java
            .getResource("advent20/puzzle9-input.txt")
            ?.let { f -> Paths.get(f.toURI()).readLines(Charset.defaultCharset()).mapNotNull { it.toIntOrNull() } }
            ?: listOf()

    fun checkSumContains(list: List<Int>, number: Int): Boolean = list.map { number - it }.any { list.contains(it) }

    fun puzzle9a(): Int {
        // we drop the last window because if we reach last window without failure then preamble is correct
        return input.windowed(25, 1)
            .dropLast(1)
            .mapIndexed { i, l -> if (checkSumContains(l, input[i+25])) { -1 } else { input[i+25] } }
            .first { it != -1 }
    }

    fun puzzle9b(): Int {
        val (max, res) =
            input
                .windowed(25, 1)
                .dropLast(1)
                .mapIndexed { i, l -> if (checkSumContains(l, input[i+25])) { Pair(-1, -1) } else { Pair(i+25, input[i+25]) } }
                .first { it.first != -1 }
        var start = 0
        var end = -1
        var sum: Int = input[0]
        for (i in 1 until max) {
            if (sum == res) {
                end = i - 1
                break
            } else {
                sum += input[i]
                while (sum > res) {
                    sum -= input[start]
                    start += 1
                }
            }
        }
        return input.subList(start, end + 1).sorted().let { it.first() + it.last() }
    }

    println(puzzle9b())
}

fun puzzle10() {

    val input =
        {}::class.java
            .getResource("advent20/puzzle10-input.txt")
            ?.let { f -> Paths.get(f.toURI()).readLines(Charset.defaultCharset()).mapNotNull { it.toIntOrNull() } }
            ?: listOf()

    fun puzzle10a(): Int {
        val dist = input
            .sorted()
            .zipWithNext { a, b -> b - a }
            .groupBy { it }
            .mapValues { it.value.count() }
        return (dist[1]!! + 1) * (dist[3]!! + 1)
    }

    fun puzzle10b(): Long {
        val srt = (input + 0).sorted()
        val all = srt + (srt.last() + 3)
        val memo = mutableMapOf<Int, Int>()
        fun fact(x: Int): Int = if(x <= 1) { 1 } else { memo.getOrPut(x) { x * fact(x-1) } }

        val run = all
            .zipWithNext { a, b -> b - a }
            .fold(mutableListOf(Pair(1,0))) { acc, i ->
                val end = acc.removeLast()
                if (end.first == i) {
                    acc.add(Pair(end.first, end.second + 1))
                } else {
                    acc.add(end)
                    acc.add(Pair(i, 1))
                }
                acc
            }
        val res = run
            .map { p ->
                if (p.first == 1) {
                    if (p.second < 2) {
                        1
                    } else {
                        val n = p.second - 1
                        val r = (n/2) + 1
                        var s = 0
                        val nf = fact(n)
                        for (i in 0..r) {
                            val rf = fact(i)
                            val nrf = fact(n-i)
                            val cs = nf / (rf * nrf)
                            s += cs
                        }
                       s
                    }
                } else {
                    1
                }
            }
        return res.fold(1L) { acc, i -> acc * i }
    }

    println(puzzle10b())
}

fun puzzle11() {

    val input =
        {}::class.java
            .getResource("advent20/puzzle11-input.txt")
            ?.let { f -> Paths.get(f.toURI()).readLines(Charset.defaultCharset()).map { it.toMutableList() } }
            ?: listOf()

    fun puzzle11a(): Int {

        val isEmpty: (Char) -> Boolean = { c -> c in setOf('L', '.') }

        fun checkSeatForEmpty(grid: List<List<Char>>, r: Int, c: Int): List<Boolean> {
            val isFirstRow = (r == 0)
            val isLastRow = (r == input.size - 1)
            val isFirstCol = (c == 0)
            val isLastCol = (c == input.first().size - 1)

            val res = (0 until 8)
                .map { i ->
                    when (i) {
                        0 -> isFirstRow || isFirstCol || isEmpty(grid[r-1][c-1])
                        1 -> isFirstRow || isEmpty(grid[r-1][c])
                        2 -> isFirstRow || isLastCol || isEmpty(grid[r-1][c+1])
                        3 -> isFirstCol || isEmpty(grid[r][c-1])
                        4 -> isLastCol || isEmpty(grid[r][c+1])
                        5 -> isLastRow || isFirstCol || isEmpty(grid[r+1][c-1])
                        6 -> isLastRow || isEmpty(grid[r+1][c])
                        7 -> isLastRow || isLastCol || isEmpty(grid[r+1][c+1])
                        else -> false
                    }
                }
            return res
        }

        fun checkSeatForOccupied(grid: List<List<Char>>, r: Int, c: Int): List<Boolean> {
            val isFirstRow = (r == 0)
            val isLastRow = (r == input.size - 1)
            val isFirstCol = (c == 0)
            val isLastCol = (c == input.first().size - 1)

            val res = (0 until 8)
                .map { i ->
                    when (i) {
                        0 -> isFirstRow || isFirstCol || isEmpty(grid[r-1][c-1])
                        1 -> isFirstRow || isEmpty(grid[r-1][c])
                        2 -> isFirstRow || isLastCol || isEmpty(grid[r-1][c+1])
                        3 -> isFirstCol || isEmpty(grid[r][c-1])
                        4 -> isLastCol || isEmpty(grid[r][c+1])
                        5 -> isLastRow || isFirstCol || isEmpty(grid[r+1][c-1])
                        6 -> isLastRow || isEmpty(grid[r+1][c])
                        7 -> isLastRow || isLastCol || isEmpty(grid[r+1][c+1])
                        else -> true
                    }
                }.map { it.not() }
            return res
        }

        var nc: Int
        var re = input.toMutableList()
        do {
            val cpy = MutableList(re.size) { r -> MutableList(re[r].size) { c -> re[r][c] } }
            nc = 0
            for (r in input.indices) {
                for (c in input[r].indices) {
                    val st = re[r][c]
                    when {
                        (st == 'L' && checkSeatForEmpty(re, r, c).all { it }) -> {
                            nc += 1
                            cpy[r][c] = '#'
                        }
                        (st == '#' && checkSeatForOccupied(re, r, c).count { it } >= 4) -> {
                            nc += 1
                            cpy[r][c] = 'L'
                        }
                        else -> {
                            cpy[r][c] = st
                        }
                    }
                }
            }
            re = cpy
        } while (nc != 0)
        return re.fold(0) { acc, r -> acc + r.count { it == '#' } }
    }

    fun puzzle11b(): Int {
        val isSeat: (Char) -> Boolean = { c -> c != '.' }
        val isEmpty: (Char) -> Boolean = { c -> c == 'L' }
        val isOccupied: (Char) -> Boolean = { c -> c == '#' }
        val height = input.size
        val width = input.first().size

        fun compileLr(grid: List<List<Char>>, r:Int , c: Int): List<Char> {
            val u = mutableListOf<Char>()
            val d = mutableListOf<Char>()
            var cr = r-1
            var cc = c-1
            while (cr >= 0 && cc >= 0) {
                u.add(grid[cr][cc])
                cr -= 1
                cc -= 1
            }
            cr = r+1
            cc = c+1
            while (cr < height && cc < width) {
                d.add(grid[cr][cc])
                cr += 1
                cc += 1
            }
            return u.reversed() + listOf('x') + d
        }

        fun compileRl(grid: List<List<Char>>, r:Int , c: Int): List<Char> {
            val u = mutableListOf<Char>()
            val d = mutableListOf<Char>()
            var cr = r-1
            var cc = c+1
            while (cr >= 0 && cc < width) {
                u.add(grid[cr][cc])
                cr -= 1
                cc += 1
            }
            cr = r+1
            cc = c-1
            while (cr < height && cc >= 0) {
                d.add(grid[cr][cc])
                cr += 1
                cc -= 1
            }
            return u.reversed() + listOf('x') + d
        }

        fun checkSeat(grid: List<List<Char>>, r: Int, c: Int, check: (Char) -> Boolean): List<Boolean?> {
            val row = grid[r]
            val col = grid.map { it[c] }
            val lr = compileLr(grid, r, c)
            val rl = compileRl(grid, r, c)
            val ilr = lr.indexOf('x')
            val irl = rl.indexOf('x')
            val res = (0 until 8)
                .map { i ->
                    when (i) {
                        0 -> lr.subList(0, ilr).reversed().firstOrNull(isSeat)?.let(check)
                        1 -> col.subList(0, r).reversed().firstOrNull(isSeat)?.let(check)
                        2 -> rl.subList(0, irl).reversed().firstOrNull(isSeat)?.let(check)
                        3 -> row.subList(0, c).reversed().firstOrNull(isSeat)?.let(check)
                        4 -> row.subList(c+1, width).firstOrNull(isSeat)?.let(check)
                        5 -> rl.subList(irl+1, rl.size).firstOrNull(isSeat)?.let(check)
                        6 -> col.subList(r+1, height).firstOrNull(isSeat)?.let(check)
                        7 -> lr.subList(ilr+1, lr.size).firstOrNull(isSeat)?.let(check)
                        else -> false
                    }
                }
            return res
        }

        var nc: Int
        var re = input.toMutableList()
        do {
            val cpy = MutableList(re.size) { r -> MutableList(re[r].size) { c -> re[r][c] } }
            nc = 0
            for (r in input.indices) {
                for (c in input[r].indices) {
                    val st = re[r][c]
                    when {
                        (st == 'L' && checkSeat(re, r, c, isEmpty).all { it == null || it }) -> {
                            nc += 1
                            cpy[r][c] = '#'
                        }
                        (st == '#' && checkSeat(re, r, c, isOccupied).count { it != null && it } >= 5) -> {
                            nc += 1
                            cpy[r][c] = 'L'
                        }
                        else -> {
                            cpy[r][c] = st
                        }
                    }
                }
            }
            re = cpy
        } while (nc != 0)
        return re.fold(0) { acc, r -> acc + r.count { it == '#' } }
    }

    println(puzzle11b())
}

fun puzzle12() {
    val input =
        {}::class.java
            .getResource("advent20/puzzle12-input.txt")
            ?.let { f -> Paths.get(f.toURI()).readLines(Charset.defaultCharset()).map { Pair(it.first(), it.substring(1).toInt()) } }
            ?: listOf()

    fun puzzle12a() : Int {
        val f = input.fold(Pair(90, Pair(0,0))) { acc, p ->
            val d = acc.first
            val (x, y) = acc.second
            when(p.first) {
                'R' -> Pair((d + p.second).mod(360), acc.second)
                'L' -> Pair((d - p.second + 360).mod(360), acc.second)
                'F' -> when (d) {
                    0 -> Pair(d, Pair(x, y + p.second))
                    90 -> Pair(d, Pair(x + p.second, y))
                    180 -> Pair(d, Pair(x, y - p.second))
                    270 -> Pair(d, Pair(x - p.second, y))
                    else -> Pair(d, Pair(x, y))
                }
                'N' -> Pair(d, Pair(x, y + p.second))
                'S' -> Pair(d, Pair(x, y - p.second))
                'E' -> Pair(d, Pair(x + p.second, y))
                'W' -> Pair(d, Pair(x - p.second, y))
                else -> acc
            }
        }
        return (abs(f.second.first) + abs(f.second.second))
    }

    fun puzzle12b() : Int {
        data class WayPoint(val x: Int, val y: Int) {
            fun rotateCW(deg: Int): WayPoint =
                when (deg) {
                    90 -> WayPoint(y, -x)
                    180 -> WayPoint(-x, -y)
                    270 -> rotateCCW(90)
                    else -> WayPoint(x, y)
                }

            fun rotateCCW(deg: Int): WayPoint =
                when (deg) {
                    90 -> WayPoint(-y, x)
                    180 -> WayPoint(-x, -y)
                    270 -> rotateCW(90)
                    else -> WayPoint(x, y)
                }
        }
        data class Coordinates(val x: Int, val y: Int) {
            fun advance(wayPoint: WayPoint, scale: Int): Coordinates =
                Coordinates(x + (scale * wayPoint.x), y + (scale * wayPoint.y))
        }
        data class Navigator(val wp: WayPoint, val cp: Coordinates) {
            fun advance(scale: Int): Navigator = Navigator(wp, cp.advance(wp, scale))
            fun adjustWp(x: Int, y: Int): Navigator = Navigator(WayPoint(wp.x + x, wp.y + y), cp)
            fun rotateCW(deg: Int): Navigator = Navigator(wp.rotateCW(deg), cp)
            fun rotateCCW(deg: Int): Navigator = Navigator(wp.rotateCCW(deg), cp)
        }

        val f = input.fold(Navigator(WayPoint(10, 1), Coordinates(0, 0))) { nav, p ->
            when(p.first) {
                'R' -> nav.rotateCW(p.second)
                'L' -> nav.rotateCCW(p.second)
                'F' -> nav.advance(p.second)
                'N' -> nav.adjustWp(0, p.second)
                'S' -> nav.adjustWp(0, -p.second)
                'E' -> nav.adjustWp(p.second, 0)
                'W' -> nav.adjustWp(-p.second, 0)
                else -> nav
            }
        }
        return (abs(f.cp.x) + abs(f.cp.y))
    }

    println(puzzle12b())
}