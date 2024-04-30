package io.vinicius.umd.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import com.github.ajalt.mordant.rendering.TextColors
import io.vinicius.umd.t
import kotlinx.datetime.Clock

class TerminalWriter : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val timestamp = Clock.System.now().toString()
        val level = severity.toString().first().uppercase()

        val color = when (severity) {
            Severity.Debug -> TextColors.brightBlue
            Severity.Info -> TextColors.brightGreen
            Severity.Warn -> TextColors.brightYellow
            Severity.Error -> TextColors.brightRed
            else -> TextColors.white
        }

        t.println(color("$timestamp $level/$tag: $message"))
        throwable?.let { t.println(color(it.stackTraceToString())) }
    }
}