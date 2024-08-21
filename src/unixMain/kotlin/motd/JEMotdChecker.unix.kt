package motd

actual object JEMotdChecker : JEMotdChecker0() {
    override fun ping(host: String, port: UShort): JEMotd {
        TODO("Not yet implemented")
    }
}
