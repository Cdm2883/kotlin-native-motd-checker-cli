package motd

interface MotdChecker<T : Motd> {
    fun ping(host: String, port: UShort): T

    companion object {
        fun ping(host: String, port: UShort) = try {
            BEMotdChecker.ping(host, port)  // TODO Timeout
        } catch (_: Exception) {
            JEMotdChecker.ping(host, port)
        }
    }
}
