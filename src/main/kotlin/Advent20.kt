import java.nio.charset.Charset
import java.nio.file.Paths
import kotlin.io.path.readLines

fun main(args: Array<String>) {
    puzzle8()
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