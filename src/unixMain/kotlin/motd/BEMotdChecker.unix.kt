package motd

actual object BEMotdChecker : BEMotdChecker0() {
    override fun ping(host: String, port: UShort): BEMotd {
        TODO("Not yet implemented")
    }
}
