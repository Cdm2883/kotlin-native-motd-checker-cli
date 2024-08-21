import cli.AppArgs
import cli.GameEdition
import cli.NoColor
import cli.parse
import kotlinx.cinterop.toKString
import motd.BEMotdChecker
import motd.JEMotdChecker
import motd.MotdChecker
import platform.posix.getenv

fun main(args: Array<String>) {
    NoColor = getenv("NO_COLOR")?.toKString()?.isNotEmpty() ?: false

    val parsed = try {
        AppArgs.parse(args)
    } catch (e: Exception) {
        println(e.message)
        println()
        return println("See more by option -h or --help")
    }
    main(parsed)
}

fun main(args: AppArgs) {
    val advertisement = when (args.edition) {
        GameEdition.Bedrock -> BEMotdChecker.ping(args.host, args.port)
        GameEdition.Java -> JEMotdChecker.ping(args.host, args.port)
        null -> MotdChecker.ping(args.host, args.port)
    }
    println(advertisement)
}
