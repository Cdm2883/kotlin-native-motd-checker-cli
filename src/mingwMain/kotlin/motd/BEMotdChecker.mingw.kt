package motd

import PlatformError
import getCurrentTimeMillis
import initWinsock
import kotlinx.cinterop.*
import platform.posix.*
import sockAddrIn

actual object BEMotdChecker : BEMotdChecker0() {
    override fun ping(host: String, port: UShort) = memScoped {
        initWinsock()

        val socket = socket(AF_INET, SOCK_DGRAM, 0)
            .takeIf { it >= 0u } ?: throw PlatformError("Socket creation failed")
        val server = sockAddrIn(host, port)

        val begin = getCurrentTimeMillis()

        if (sendto(
                socket,
                unconnectedPing.refTo(0),
                unconnectedPing.size,
                0,
                server.ptr.reinterpret(),
                sizeOf<sockaddr_in>().convert()
            ) < 0
        ) throw PlatformError("Send unconnected ping failed")

        val buffer = ByteArray(4096)
        val receive = recvfrom(socket, buffer.refTo(0), buffer.size.convert(), 0, null, null)
            .takeIf { it >= 0 } ?: throw PlatformError("Receive response failed")

        val end = getCurrentTimeMillis()

        val data = buffer.decodeToString(0, receive.convert())
        buildFrom(data, (end - begin).convert())
    }
}
