package io.vinicius.umd

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import io.vinicius.umd.logger.FileWriter
import io.vinicius.umd.logger.TerminalWriter
import okio.Path.Companion.toPath

class Cli : CliktCommand(
    name = "umd",
    help = "An app to easily download media files hosted on popular websites"
) {
    private val url by argument(help = "URL where the media is hosted")

    private val directory by option(
        "-d", "--dir",
        help = "Directory where the files will be saved"
    ).convert { it.toPath() }.default(".".toPath())

    private val parallel by option(
        "-p", "--parallel",
        help = "The number of downloads to be done in parallel",
        envvar = "UMD_PARALLEL"
    ).int().check("The number of parallel downloads must be between 1-10") {
        it in 1..10
    }

    private val limit by option(
        "-l", "--limit",
        help = "The maximum number of files to be downloaded",
        envvar = "UMD_LIMIT"
    ).int().check("The limit of files to download must be greater than 0") {
        it > 0
    }

    private val extensions by option(
        "-e", "--extensions",
        help = "Filter the downloads by file extensions, separated by comma",
        envvar = "UMD_EXTENSIONS"
    ).split(",").default(emptyList())

    private val verbose by option(
        "-v", "--verbose",
        help = "Print detailed debug information",
        envvar = "UMD_VERBOSE"
    ).choice("file", "terminal", "all").convert {
        when (it) {
            "file" -> listOf(FileWriter())
            "terminal" -> listOf(TerminalWriter())
            "all" -> listOf(TerminalWriter(), FileWriter())
            else -> emptyList()
        }
    }

    override fun run() {
        Logger.setMinSeverity(Severity.Assert)

        // Setup logs
        verbose?.let {
            Logger.setLogWriters(it)
            Logger.setMinSeverity(Severity.Verbose)
        }

        Logger.i("Umd-App") { "URL: $url" }
        Logger.i("Umd-App") { "Directory: $directory" }
        Logger.i("Umd-App") { "Limit: $limit" }
        Logger.i("Umd-App") { "Extensions: ${extensions.joinToString(",")}" }

        startApp(url, directory, parallel, limit, extensions)
    }
}