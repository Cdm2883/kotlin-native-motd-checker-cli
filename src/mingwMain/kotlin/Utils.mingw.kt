import kotlinx.cinterop.*
import platform.posix.*
import platform.posix.WSAStartup
import platform.windows.*

actual class PlatformError actual constructor(message: String?) : Exception("$message (${GetLastError()})")

actual fun getSelfExecutableName(): String? {
    val buffer = UShortArray(MAX_PATH)
    val bufferPointer = buffer.usePinned { it.addressOf(0).reinterpret<UShortVarOf<UShort>>() }
    val length = GetModuleFileNameW(null, bufferPointer, buffer.size.toUInt())
    return if (length > 0u) {
        val path = buildString {
            for (i in 0 until length.toInt())
                append(Char(buffer[i]))
        }
        path.substringAfterLast('\\')
    } else null
}

@Suppress("FunctionName", "SpellCheckingInspection")
fun MAKEWORD(low: Byte, high: Byte) = ((high.toUShort().toInt() shl 8) or low.toUShort().toInt()).toUShort()

@Suppress("SpellCheckingInspection")
fun MemScope.initWinsock() {
    val wsaData = alloc<WSAData>()
    if (WSAStartup(MAKEWORD(2, 2), wsaData.ptr) != 0) throw PlatformError("WSAStartup failed")
}

@Suppress("RemoveRedundantQualifierName")
fun MemScope.resolveHost(host: String): u_long {
    val hints = alloc<addrinfo>()
    val result = alloc<CPointerVar<addrinfo>>()
    hints.ai_family = platform.posix.AF_INET
    hints.ai_socktype = platform.posix.SOCK_DGRAM

    if (getaddrinfo(host, null, hints.ptr, result.ptr) != 0) throw PlatformError("Get address info failed")

    val addr = result.value?.pointed?.ai_addr?.reinterpret<sockaddr_in>()?.pointed?.sin_addr?.S_un?.S_addr
    freeaddrinfo(result.value)
    return addr ?: throw PlatformError("Resolve IP address failed")
}

@Suppress("RemoveRedundantQualifierName")
fun MemScope.sockAddrIn(host: u_long, port: UShort) = alloc<sockaddr_in>().apply {
    sin_family = platform.posix.AF_INET.convert()
    sin_port = htons(port)
    sin_addr.S_un.S_addr = host
}
fun MemScope.sockAddrIn(host: String, port: UShort) = sockAddrIn(
    if (isIpAddress(host)) platform.posix.inet_addr(host) else resolveHost(host),
    port
)

fun isIpAddress(input: String): Boolean {
    val parts = input.split(".")
    if (parts.size != 4) return false
    for (part in parts) {
        val num = part.toIntOrNull() ?: return false
        if (num !in 0..255) return false
    }
    return true
}
