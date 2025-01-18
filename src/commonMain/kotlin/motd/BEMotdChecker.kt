package motd

import cli.formatTable
import cli.toAnsiColorFormat

abstract class BEMotdChecker0 : MotdChecker<BEMotd> {
    val unconnectedPing = "0100000000240D12D300FFFF00FEFEFEFEFDFDFDFD12345678"
        .chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    fun buildFrom(response: String, delay: Int) = response.split(";").let {
        BEMotd(
            it[1],
            it[2].toInt(),
            it[3],
            it[4].toInt(),
            it[5].toInt(),
            it[7],
            it[8],
            it[9].toInt(),
            it[10].toUShort(),
            it[11].toUShort(),
            delay
        )
    }
}

expect object BEMotdChecker : BEMotdChecker0

class BEMotd(
    override val motd: String,
    override val protocol: Int,
    override val version: String,
    override val playerOnline: Int,
    override val playerMax: Int,
    private val world: String,
    private val mode: String,
    private val modeNumber: Int,
    private val portV4: UShort,
    private val portV6: UShort,
    override val delay: Int
) : Motd {
    override fun toString() = formatTable(
        "MOTD" to motd.toAnsiColorFormat(),
        "Version" to "$version ($protocol)",
        "Players" to "$playerOnline / $playerMax",
        "World" to world.toAnsiColorFormat(),
        "GameMode" to "$mode ($modeNumber)",
        "Port" to "${portV4}::v4 ${portV6}::v6",
        "Delay" to "${delay}ms",
    )
}
