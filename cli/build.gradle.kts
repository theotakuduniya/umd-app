plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ktlint)
}

group = "io.vinicius.umd"
version = "1.0.0"

kotlin {
    val hostOs = System.getenv("KTOS") ?: System.getProperty("os.name")
    val isArm64 = System.getenv("KTARCH").equals("arm64", true) || System.getProperty("os.arch") == "aarch64"
    val isMac = hostOs.contains("mac", true)
    val isLinux = hostOs.contains("linux", true)
    val isWindows = hostOs.contains("win", true)

    val nativeTarget = when {
        isMac && isArm64 -> macosArm64()
        isMac && !isArm64 -> macosX64()
        isLinux && isArm64 -> linuxArm64()
        isLinux && !isArm64 -> linuxX64()
        isWindows -> mingwX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "io.vinicius.umd.main"
                baseName = "umd"
            }
        }
    }

    sourceSets {
        nativeMain.dependencies {
            implementation(libs.clikt)
            implementation(libs.mordant)
            implementation(projects.shared)
        }
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    additionalEditorconfig.set(
        mapOf("ktlint_code_style" to "intellij_idea"),
    )
    filter {
        exclude {
            it.file.path.contains("generated")
        }
    }
}