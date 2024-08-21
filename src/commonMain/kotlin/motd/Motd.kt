package motd

interface Motd {
    val motd: String
    val protocol: Int
    val version: String
    val playerOnline: Int
    val playerMax: Int
    val delay: Int

    companion object {
        const val TIME_OUT = 3000
    }
}
