package motd

import cli.printTable

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
    override fun toString() = printTable(
        "MOTD" to motd,
        "Version" to "$version ($protocol)",
        "Players" to "$playerOnline / $playerMax",
        "World" to world,
        "GameMode" to "$mode ($modeNumber)",
        "Port" to "${portV4}_v4 ${portV6}_v6",
        "Delay" to "${delay}ms",
    )
}
