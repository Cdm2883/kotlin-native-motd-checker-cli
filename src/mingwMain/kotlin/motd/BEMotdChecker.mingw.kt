package motd

import MAKEWORD
import PlatformError
import getCurrentTimeMillis
import kotlinx.cinterop.*
import platform.posix.*
import platform.windows.addrinfo
import platform.windows.freeaddrinfo
import platform.windows.getaddrinfo
import platform.windows.htons

actual object BEMotdChecker : BEMotdChecker0() {
    @Suppress("SpellCheckingInspection")
    private fun MemScope.initWinsock() {
        val wsaData = alloc<WSAData>()
        if (WSAStartup(MAKEWORD(2, 2), wsaData.ptr) != 0) {
            throw PlatformError("WSAStartup failed")
        }
    }

    private fun MemScope.resolveHost(host: String): u_long {
        val hints = alloc<addrinfo>()
        val result = alloc<CPointerVar<addrinfo>>()
        hints.ai_family = AF_INET
        hints.ai_socktype = SOCK_DGRAM

        if (getaddrinfo(host, null, hints.ptr, result.ptr) != 0) {
            @Suppress("SpellCheckingInspection")
            throw PlatformError("getaddrinfo failed")
        }

        val addr = result.value?.pointed?.ai_addr?.reinterpret<sockaddr_in>()?.pointed?.sin_addr?.S_un?.S_addr
        freeaddrinfo(result.value)
        return addr ?: throw PlatformError("Resolve IP address failed")
    }

    override fun ping(host: String, port: UShort) = memScoped {
        initWinsock()

        val socket = socket(AF_INET, SOCK_DGRAM, 0)
            .takeIf { it >= 0u } ?: throw PlatformError("Socket creation failed")
        val server = alloc<sockaddr_in>().apply {
            sin_family = AF_INET.convert()
            sin_port = htons(port)
            sin_addr.S_un.S_addr = resolveHost(host)
        }

        val begin = getCurrentTimeMillis()

        @Suppress("SpellCheckingInspection")
        if (sendto(
                socket,
                unconnectedPing.refTo(0),
                unconnectedPing.size,
                0,
                server.ptr.reinterpret(),
                sizeOf<sockaddr_in>().convert()
            ) < 0
        ) throw PlatformError("sendto failed")

        val buffer = ByteArray(4096)
        @Suppress("SpellCheckingInspection")
        val receive = recvfrom(socket, buffer.refTo(0), buffer.size.convert(), 0, null, null)
            .takeIf { it >= 0 } ?: throw PlatformError("recvfrom failed")

        val end = getCurrentTimeMillis()

        val data = buffer.decodeToString(0, receive.convert())
        buildFrom(data, (end - begin).convert())
    }
}
