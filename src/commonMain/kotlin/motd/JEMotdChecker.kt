package motd

abstract class JEMotdChecker0 : MotdChecker<JEMotd>

expect object JEMotdChecker : JEMotdChecker0

class JEMotd(
    private val favicon: String,
    override val motd: String,
    override val protocol: Int,
    override val version: String,
    override val playerOnline: Int,
    override val playerMax: Int,
    override val delay: Int
) : Motd {
    override fun toString() = TODO()
}
