import platform.posix.perror

actual class PlatformError actual constructor(message: String?) : Exception(message) {
    init {
        perror(message)
    }
}

actual fun getSelfExecutableName(): String? {
    TODO("Not yet implemented")
}
