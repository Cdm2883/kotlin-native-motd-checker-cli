package cli

import BuildConfig
import getSelfExecutableName
import motd.GameEdition
import platform.posix.exit

inline val CliHelpMessage
    get() = """
    Usage: ./${getSelfExecutableName()} [options] [address]

    Simple Minecraft MOTD Checker Command Line Tool written in Kotlin/Native!
    
    Arguments:
      address        server address to ping
    
    Options:
      -v, --version  output the version number
      -s, --host     address host
      -p, --port     address port
      -b, --bedrock  ping bedrock edition
      -j, --java     ping java edition
      -h, --help     display help for command
""".trimIndent()

class AppArgs(
    val host: String,
    val port: UShort,
    val edition: GameEdition?,
)

inline fun parsePort(port: String) = port.toUShortOrNull() ?: throw IllegalArgumentException("Invalid port value")
inline fun parseArgs(args: Array<String>): AppArgs {
    var host = ""
    var port: UShort = 0u
    var edition: GameEdition? = null

    val iterator = args.iterator()
    while (iterator.hasNext()) when (val arg = iterator.next()) {
        "-v", "--version" -> {
            println(BuildConfig.VERSION)
            exit(0)
        }

        "-h", "--help" -> {
            println(CliHelpMessage)
            exit(0)
        }

        "-s", "--host" -> host = iterator.next()
        "-p", "--port" -> port = parsePort(iterator.next())

        "-b", "--bedrock" -> edition = GameEdition.Bedrock
        "-j", "--java" -> edition = GameEdition.Java

        else -> {
            if (arg.startsWith('-')) throw IllegalArgumentException("Unknown option: $arg")
            val split = arg.split(":")
            host = split[0]
            split.getOrNull(1)?.let { port = parsePort(it) }
        }
    }

    if (host.isEmpty()) throw IllegalArgumentException("Host is required")
    if (port == 0u.toUShort()) port = 19132u
    return AppArgs(host, port, edition)
}

inline fun handleArgs(args: Array<String>) = try {
    parseArgs(args)
} catch (e: Exception) {
    println(e.message)
    println()
    println("See more by option -h or --help")
    exit(2)
    throw e  // UNREACHABLE
}
