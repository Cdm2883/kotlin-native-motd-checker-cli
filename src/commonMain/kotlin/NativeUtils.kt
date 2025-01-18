import kotlinx.cinterop.*
import platform.posix.CLOCK_REALTIME
import platform.posix.clock_gettime
import platform.posix.timespec

expect class PlatformError(message: String? = null) : Exception

expect fun getSelfExecutableName(): String?

@OptIn(UnsafeNumber::class)
fun getCurrentTimeMillis() = memScoped {
    @Suppress("SpellCheckingInspection")
    val timespec = alloc<timespec>()
    clock_gettime(CLOCK_REALTIME.convert(), timespec.ptr)
    timespec.tv_sec * 1000L + timespec.tv_nsec / 1_000_000L
}
