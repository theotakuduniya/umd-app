plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ktlint)
}

group = "io.vinicius.umd"
version = "1.0.0"

kotlin {
    val targets = listOf(
        macosArm64(),
        macosX64(),
        linuxX64(),
        mingwX64(),
    )

    targets.forEach {
        it.binaries {
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