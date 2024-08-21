import kotlinx.cinterop.*
import platform.windows.GetLastError
import platform.windows.GetModuleFileNameW
import platform.windows.MAX_PATH

actual class PlatformError actual constructor(message: String?) : Exception("$message (${GetLastError()})")

actual fun getSelfExecutableName(): String? {
    val buffer = UShortArray(MAX_PATH)
    val bufferPointer = buffer.usePinned { it.addressOf(0).reinterpret<UShortVarOf<UShort>>() }
    val length = GetModuleFileNameW(null, bufferPointer, buffer.size.toUInt())
    return if (length > 0u) {
        val path = buildString {
            for (i in 0 until length.toInt()) {
                append(Char(buffer[i]))
            }
        }
        path.substringAfterLast('\\')
    } else null
}

@Suppress("FunctionName", "SpellCheckingInspection")
fun MAKEWORD(low: Byte, high: Byte) = ((high.toUShort().toInt() shl 8) or low.toUShort().toInt()).toUShort()
