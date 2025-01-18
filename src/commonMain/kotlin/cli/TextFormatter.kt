package cli

import kotlin.math.max

fun formatTable(vararg rows: Pair<String, String>): String {
    var column1 = Int.MIN_VALUE
    rows.forEach {
        column1 = max(column1, it.first.length)
    }

    return buildString {
        rows.forEach {
            append("$BOLD_ANSI${it.first.padEnd(column1)}$CLEAR_ANSI   ${it.second}\n")
        }
    }.dropLast(1)
}
