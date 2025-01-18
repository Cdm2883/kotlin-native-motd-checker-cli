import cli.*
import kotlinx.cinterop.toKString
import motd.*
import platform.posix.getenv

var NoColor = false
    private set

fun main(args: Array<String>) {
    NoColor = getenv("NO_COLOR")?.toKString()?.isNotEmpty() ?: false

    val parsed = handleArgs(args)
    println(ping(parsed.host, parsed.port, parsed.edition))
}
