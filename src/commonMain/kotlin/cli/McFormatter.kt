package cli

import NoColor

private const val SS = "§"

private inline fun getFormatColor(code: Char) = when(code) {
    '0' -> 0x000000  // black
    '1' -> 0x0000AA  // dark_blue
    '2' -> 0x00AA00  // dark_green
    '3' -> 0x00AAAA  // dark_aqua
    '4' -> 0xAA0000  // dark_red
    '5' -> 0xAA00AA  // dark_purple
    '6' -> 0xFFAA00  // gold
    '7' -> 0xAAAAAA  // gray
    '8' -> 0x555555  // dark_gray
    '9' -> 0x5555FF  // blue (5455FF)
    'a' -> 0x55FF55  // green (55FF56)
    'b' -> 0x55FFFF  // aqua
    'c' -> 0xFF5555  // red
    'd' -> 0xFF55FE  // light_purple
    'e' -> 0xFFFF55  // yellow
    'f' -> 0xFFFFFF  // white
    'g' -> 0xEECF15  // minecoin_gold
    'h' -> 0xE3D4D1  // material_quartz
    'i' -> 0xCECACA  // material_iron
    'j' -> 0x443A3B  // material_netherite
//    'm' -> 0x971607  // material_redstone
//    'n' -> 0xB4684D  // material_copper
    'p' -> 0xDEB12D  // material_gold
    'q' -> 0x47A036  // material_emerald
    's' -> 0x2CBAA8  // material_diamond
    't' -> 0x21497B  // material_lapis
    'u' -> 0x9A5CC6  // material_amethyst
    else -> null
}

const val BOLD_ANSI = "\u001B[1m"
const val CLEAR_ANSI = "\u001B[0m"
private inline fun getFormatAnsiExtras(code: Char) = when(code) {
    'k' -> "\u001B[5m"  // obfuscated
    'l' -> BOLD_ANSI
    'm' -> "\u001B[9m"  // strikethrough
    'n' -> "\u001B[4m"  // underline
    'o' -> "\u001B[3m"  // italic
    'r' -> CLEAR_ANSI
    else -> null
}

private inline fun hex2ansi(hex: Int): String {
    val r = (hex shr 16) and 0xFF
    val g = (hex shr 8) and 0xFF
    val b = hex and 0xFF
    return "\u001B[38;2;${r};${g};${b}m"
}

fun String.toAnsiColorFormat(): String {
    if (NoColor) return this

    var result = ""
    var i = 0
    while (i < this.length) {
        if (this[i] == SS[0] && i + 1 < this.length) {
            val code = this[i + 1]
            val color = getFormatColor(code)
            val extra = getFormatAnsiExtras(code)

            if (color != null) {
                result += hex2ansi(color)
            } else if (extra != null) {
                result += extra
            } else {
                result += SS
                result += code
            }

            i += 2
        } else {
            result += this[i]
            i++
        }
    }

    result += CLEAR_ANSI
    return result
}
