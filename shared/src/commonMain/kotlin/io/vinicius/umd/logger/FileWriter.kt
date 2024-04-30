package io.vinicius.umd.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import io.vinicius.umd.ktx.exists
import io.vinicius.umd.util.fs
import kotlinx.datetime.Clock
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

class FileWriter(private val directory: Path) : LogWriter() {
    private var firstWrite = true

    init {
        // Create the directory if it doesn't exist
        if (!directory.exists()) {
            fs.createDirectories(directory)
        }
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val timestamp = Clock.System.now().toString()
        val level = severity.toString().first().uppercase()
        val filePath = directory / "umd-logs.txt".toPath()

        if (firstWrite) {
            fs.appendingSink(filePath).buffer().use { it.writeUtf8("---\n") }
            firstWrite = false
        }

        fs.appendingSink(filePath).buffer().use {
            it.writeUtf8("$timestamp $level/$tag: $message\n")
            throwable?.let { error -> it.writeUtf8(error.stackTraceToString() + "\n") }
        }
    }
}