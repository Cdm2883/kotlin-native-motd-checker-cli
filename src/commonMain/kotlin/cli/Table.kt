package cli

import kotlin.math.max

fun printTable(vararg rows: Pair<String, String>): String {
    var column1 = Int.MIN_VALUE
    rows.forEach {
        column1 = max(column1, it.first.length)
    }

    return buildString {
        rows.forEach {
            append("${it.first.padEnd(column1)} -> ${it.second}\n")
        }
    }.dropLast(1)
}
