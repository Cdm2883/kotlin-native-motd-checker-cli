package motd

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
