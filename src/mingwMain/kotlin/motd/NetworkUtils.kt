package motd

import MAKEWORD
import PlatformError
import kotlinx.cinterop.*
import platform.posix.WSAData
import platform.posix.WSAStartup
import platform.posix.sockaddr_in
import platform.posix.u_long
import platform.windows.addrinfo
import platform.windows.freeaddrinfo
import platform.windows.getaddrinfo
import platform.windows.htons

@Suppress("SpellCheckingInspection")
fun MemScope.initWinsock() {
    val wsaData = alloc<WSAData>()
    if (WSAStartup(MAKEWORD(2, 2), wsaData.ptr) != 0) throw PlatformError("WSAStartup failed")
}

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
