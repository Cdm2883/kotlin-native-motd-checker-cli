package motd

class JEMotd(
    private val favicon: String,
    override val motd: String,
    override val protocol: Int,
    override val version: String,
    override val playerOnline: Int,
    override val playerMax: Int,
    override val delay: Int
) : Motd {
    override fun toString() = """
        awa
    """.trimIndent()
}
