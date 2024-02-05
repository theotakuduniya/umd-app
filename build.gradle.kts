plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

detekt {
    config.setFrom("$rootDir/config/detekt.yml")
    source.setFrom(
        "$rootDir/cli/src/nativeMain/kotlin",
        "$rootDir/gui/src/desktopMain/kotlin",
        "$rootDir/shared/src/commonMain/kotlin",
        "$rootDir/shared/src/jvmMain/kotlin",
    )
}