package motd

import cli.NoColor

const val SS = "§"

val McFormattingColors = mapOf(
    '0' to 0x000000,  // black
    '1' to 0x0000AA,  // dark_blue
    '2' to 0x00AA00,  // dark_green
    '3' to 0x00AAAA,  // dark_aqua
    '4' to 0xAA0000,  // dark_red
    '5' to 0xAA00AA,  // dark_purple
    '6' to 0xFFAA00,  // gold
    '7' to 0xAAAAAA,  // gray
    '8' to 0x555555,  // dark_gray
    '9' to 0x5555FF,  // blue (5455FF)
    'a' to 0x55FF55,  // green (55FF56)
    'b' to 0x55FFFF,  // aqua
    'c' to 0xFF5555,  // red
    'd' to 0xFF55FE,  // light_purple
    'e' to 0xFFFF55,  // yellow
    'f' to 0xFFFFFF,  // white
    'g' to 0xEECF15,  // minecoin_gold
    'h' to 0xE3D4D1,  // material_quartz
    'i' to 0xCECACA,  // material_iron
    'j' to 0x443A3B,  // material_netherite
//    'm' to 0x971607,  // material_redstone
//    'n' to 0xB4684D,  // material_copper
    'p' to 0xDEB12D,  // material_gold
    'q' to 0x47A036,  // material_emerald
    's' to 0x2CBAA8,  // material_diamond
    't' to 0x21497B,  // material_lapis
    'u' to 0x9A5CC6,  // material_amethyst
)

val McFormattingAnsiExtras = mapOf(
    'k' to "\u001B[5m",  // obfuscated
    'l' to "\u001B[1m",  // bold
    'm' to "\u001B[9m",  // strikethrough
    'n' to "\u001B[4m",  // underline
    'o' to "\u001B[3m",  // italic
    'r' to "\u001B[0m",  // clear
)
fun hex2ansi(hex: Int): String {
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
            val color = McFormattingColors[code]
            val extra = McFormattingAnsiExtras[code]

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

    result += McFormattingAnsiExtras['r']
    return result
}
