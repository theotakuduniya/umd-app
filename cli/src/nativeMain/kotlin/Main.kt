package io.vinicius.umd

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath

fun main(args: Array<String>) = Cli().main(args)

class Cli : CliktCommand(
    name = "umd",
    help = "An app to easily download media files hosted on popular websites"
) {
    private val url by argument(help = "URL where the media is hosted")

    private val directory by option(
        "-d", "--dir",
        help = "Directory where the files will be saved"
    ).convert { it.toPath() }.default(".".toPath())

    private val limit by option(
        help = "The maximum number of files to be downloaded",
        envvar = "UMD_LIMIT"
    ).int()

    private val extensions by option(
        help = "Filter the downloads by file extensions, separated by comma",
        envvar = "UMD_EXTENSIONS"
    ).split(",").default(emptyList())

    override fun run() {
        val (pb, state) = createProgressBar("file.jpg", 100)

        runBlocking {
            repeat(state.total.toInt()) {
                pb.update(state.updateTotal(it+1L))
                delay(100)
            }
        }
    }
}