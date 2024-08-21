package cli

import Constant
import getSelfExecutableName
import platform.posix.exit

val Help by lazy {
    """
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
}

class AppArgs(
    val host: String,
    val port: UShort,
    val edition: GameEdition?,
) {
    companion object
}

fun AppArgs.Companion.parse(args: Array<String>): AppArgs {
    var host = ""
    var port: UShort = 0u
    var edition: GameEdition? = null

    val iterator = args.iterator()
    while (iterator.hasNext()) when (val arg = iterator.next()) {
        "-v", "--version" -> {
            println(Constant.VERSION)
            exit(0)
        }

        "-h", "--help" -> {
            println(Help)
            exit(0)
        }

        "-s", "--host" -> host = iterator.next()
        "-p", "--port" -> port =
            iterator.next().toUShortOrNull() ?: throw IllegalArgumentException("Invalid port value")

        "-b", "--bedrock" -> edition = GameEdition.Bedrock
        "-j", "--java" -> edition = GameEdition.Java

        else -> {
            if (arg.startsWith('-')) throw IllegalArgumentException("Unknown option: $arg")
            val split = arg.split(":")
            host = split[0]
            split.getOrNull(1)?.let { port = it.toUShort() }
        }
    }

    if (host.isEmpty()) throw IllegalArgumentException("Host is required")
    if (port == 0u.toUShort()) port = 19132u
    return AppArgs(host, port, edition)
}
