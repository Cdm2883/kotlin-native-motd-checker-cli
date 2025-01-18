package motd

enum class GameEdition {
    Bedrock,
    Java
}

interface Motd {
    val motd: String
    val protocol: Int
    val version: String
    val playerOnline: Int
    val playerMax: Int
    val delay: Int
}

interface MotdChecker<T : Motd> {
    fun ping(host: String, port: UShort): T
}

const val TIME_OUT = 3000

fun ping(host: String, port: UShort, edition: GameEdition? = null) = when (edition) {
    GameEdition.Bedrock -> BEMotdChecker.ping(host, port)
    GameEdition.Java -> JEMotdChecker.ping(host, port)
    else -> try {
        BEMotdChecker.ping(host, port)  // TODO timeout
    } catch (_: Exception) {
        JEMotdChecker.ping(host, port)
    }
}
