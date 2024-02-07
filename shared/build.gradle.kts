plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ktlint)
}

kotlin {
    // JVM
    jvm()

    // macOS
    macosArm64()
    macosX64()

    // Linux
    linuxArm64()
    linuxX64()

    // Windows
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.kotlin.datetime)
            implementation(libs.okio)
            implementation(libs.umd.lib)
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