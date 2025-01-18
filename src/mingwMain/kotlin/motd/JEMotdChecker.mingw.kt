package motd

import PlatformError
import getCurrentTimeMillis
import initWinsock
import kotlinx.cinterop.*
import platform.posix.*
import sockAddrIn

actual object JEMotdChecker : JEMotdChecker0() {
    override fun ping(host: String, port: UShort) = memScoped {
        initWinsock()

        val socket = socket(AF_INET, SOCK_STREAM, 0)
            .takeIf { it >= 0u } ?: throw PlatformError("Socket creation failed")
        val server = sockAddrIn(host, port)

        val begin = getCurrentTimeMillis()

        if (connect(socket, server.ptr.reinterpret(), sizeOf<sockaddr_in>().convert()) == -1) {
            close(socket)
            throw PlatformError("Connect failed")
        }

        val handshakePacket = buildHandshakePacket(host, port)
        if (send(socket, handshakePacket.refTo(0), handshakePacket.size.convert(), 0) == SOCKET_ERROR) {
            close(socket)
            throw PlatformError("Handshake failed")
        }

        run {
            val response = ByteArray(1024)
            recv(socket, response.refTo(0), response.size.convert(), 0)
            val data = response.decodeToString()
            println(data)
        }

        if (send(socket, requestStatusPacket.refTo(0), requestStatusPacket.size.convert(), 0) == SOCKET_ERROR) {
            close(socket)
            throw PlatformError("Request status failed")
        }

        val response = ByteArray(1024)
        if (recv(socket, response.refTo(0), response.size.convert(), 0) == SOCKET_ERROR) {
            close(socket)
            throw PlatformError("Receive response failed")
        }

        close(socket)

        val end = getCurrentTimeMillis()

        val data = response.decodeToString()
        println(data)
        buildFrom(data, (end - begin).convert())
    }
    private fun close(socket: SOCKET) {
        closesocket(socket)
        WSACleanup()
    }
}
