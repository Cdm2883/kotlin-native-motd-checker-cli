package motd

abstract class JEMotdChecker0 : MotdChecker<JEMotd> {
    fun buildHandshakePacket(host: String, port: UShort) = byteArrayOf(
        0x00,  // Packet ID for Handshake
        0x47,  // Protocol version (VarInt)
//        *host.map { it.code.toByte() }.toByteArray(),
//        (port.toInt() ushr 8).toByte(), port.toByte(),  // Server port
        0x01  // Next state (1 for status)
    )
    val requestStatusPacket = byteArrayOf(0x01, 0x00)

    fun buildFrom(response: String, delay: Int): JEMotd = TODO()
}
