fun main(args: Array<String>) {
    println("Hello Kotlin Native!")
    println(args.takeIf { it.isNotEmpty() }?.joinToString(" ") ?: "Non args!")
}
