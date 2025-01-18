plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

version = libs.versions.motd.checker.cli.get()

val generateBuildConfigDir = layout.buildDirectory.file("generated/src").get().asFile
val buildConfigs = mapOf(
    "VERSION" to "\"$version\"",
)

kotlin {
    arrayOf(
        macosArm64(),
        macosX64(),
        linuxArm64(),
        linuxX64(),
        mingwX64(),
    ).forEach{ target ->
        target.binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    sourceSets["commonMain"].kotlin.srcDir(generateBuildConfigDir)
    sourceSets {
        val macosArm64Main by getting
        val macosX64Main by getting
        val linuxArm64Main by getting
        val linuxX64Main by getting
        val mingwX64Main by getting

        val unixMain by creating {
            dependsOn(commonMain.get())
        }
        macosArm64Main.dependsOn(unixMain)
        macosX64Main.dependsOn(unixMain)
        linuxArm64Main.dependsOn(unixMain)
        linuxX64Main.dependsOn(unixMain)

        val mingwMain by creating {
            dependsOn(commonMain.get())
        }
        mingwX64Main.dependsOn(mingwMain)
    }

    compilerOptions.freeCompilerArgs.addAll(
        "-Xexpect-actual-classes",
        "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
    )
}

val generateBuildConfig = tasks.register("generateBuildConfig") {
    doLast {
        generateBuildConfigDir.apply { mkdirs() }
            .resolve("BuildConfig.kt").writeText("""
            |object BuildConfig {
            |    ${buildConfigs.map { (name, value) -> "const val $name = $value" }
                .joinToString("|\n")}
            |}
        """.trimMargin())
    }
}

val currentTarget by lazy {
    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    when {
        hostOs == "Mac OS X" && isArm64 -> "MacosArm64"
        hostOs == "Mac OS X" && !isArm64 -> "MacosX64"
        hostOs == "Linux" && isArm64 -> "LinuxArm64"
        hostOs == "Linux" && !isArm64 -> "LinuxX64"
        isMingwX64 -> "MingwX64"
        else -> error("Host OS is not supported in Kotlin/Native.")
    }
}

tasks.register<Copy>("linkReleaseExecutableCurrentTarget") {
    fun String.lowercaseFirst() = this[0].lowercaseChar() + substring(1)
    dependsOn(generateBuildConfig, "linkReleaseExecutable$currentTarget")
    from("build/bin/${currentTarget.lowercaseFirst()}/releaseExecutable")
    into("build/bin/current/releaseExecutable")
    doLast {
        println("Kotlin/Native application built successfully! [$currentTarget]")
    }
}

tasks.register("runDebugExecutableCurrentTarget") {
    dependsOn(generateBuildConfig, "runDebugExecutable$currentTarget")
}
