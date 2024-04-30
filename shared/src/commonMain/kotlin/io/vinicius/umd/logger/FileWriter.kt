package io.vinicius.umd.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import kotlinx.datetime.Clock
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.buffer
import okio.use

class FileWriter : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val timestamp = Clock.System.now().toString()
        val level = severity.toString().first().uppercase()
        val filePath = "umd-logs.txt".toPath()

        FileSystem.SYSTEM.appendingSink(filePath).buffer().use {
            it.writeUtf8("$timestamp $level/$tag: $message\n")
            throwable?.let { error -> it.writeUtf8(error.stackTraceToString() + "\n") }
        }
    }
}