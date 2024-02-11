package io.vinicius.umd

import com.github.ajalt.mordant.rendering.TextColors.brightGreen
import com.github.ajalt.mordant.rendering.TextColors.brightYellow
import com.github.ajalt.mordant.rendering.TextStyles.bold
import io.vinicius.umd.model.Event
import io.vinicius.umd.model.ExtractorType
import kotlinx.coroutines.runBlocking
import okio.Path

val metadata: Metadata = mutableMapOf()

fun main(args: Array<String>) = Cli().main(args)

fun startApp(
    url: String,
    directory: Path,
    parallel: Int?,
    limit: Int?,
    extensions: List<String>
) {
    t.println()

    val umd = Umd(url, metadata) {
        when (it) {
            is Event.OnExtractorFound -> {
                t.print("🌎 Website found: ${brightGreen(it.name)}; ")
            }

            is Event.OnExtractorTypeFound -> {
                t.println("extractor type: ${brightYellow(it.type)}")
                val number = limit?.toString() ?: "all"
                val source = if (it.name == null) "" else " ${bold(it.name!!)}"
                t.print("📝 Collecting ${bold(number)} media from ${it.type}$source ")
            }

            is Event.OnMediaQueried -> {
                t.print(".")
            }

            is Event.OnQueryCompleted -> {
                t.println(" ${it.total} media found")
            }

            else -> {}
        }
    }

    // Fetching media list
    val response = runBlocking { umd.queryMedia(limit ?: Int.MAX_VALUE, extensions) }
    if (!metadata.containsKey(response.extractor) && response.metadata.isNotEmpty()) {
        metadata[response.extractor] = response.metadata
    }

    // Download files
    val fetch = umd.configureFetch()
    val finalParallel = parallel ?: if (response.extractor == ExtractorType.Coomer) 2 else 5
    val downloads = startDownloads(fetch, response.media, directory, finalParallel)

    // Removing duplicates
    removeDuplicates(downloads)

    t.println("\n🌟 Done!")
}
