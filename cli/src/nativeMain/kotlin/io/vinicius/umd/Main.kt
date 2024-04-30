package io.vinicius.umd

import co.touchlab.kermit.Logger
import com.github.ajalt.mordant.animation.Animation
import com.github.ajalt.mordant.rendering.TextColors.brightBlue
import com.github.ajalt.mordant.rendering.TextColors.brightGreen
import com.github.ajalt.mordant.rendering.TextColors.brightRed
import com.github.ajalt.mordant.rendering.TextColors.brightYellow
import com.github.ajalt.mordant.rendering.TextStyles.bold
import io.vinicius.umd.model.Event
import io.vinicius.umd.model.ExtractorType
import io.vinicius.umd.util.removeDuplicates
import kotlinx.coroutines.runBlocking
import okio.Path
import okio.Path.Companion.toPath

val metadata: Metadata = mutableMapOf()

fun main(args: Array<String>) = Cli().main(args)

fun startApp(url: String, directory: Path, parallel: Int?, limit: Int?, extensions: List<String>) {
    var spinner: Animation<Pair<Int, Int>>? = null
    var spin = 1
    var total = 0
    var fullDirectory = directory

    t.cursor.hide(showOnExit = true)
    t.println()

    val umd = Umd(url, metadata) {
        when (it) {
            is Event.OnExtractorFound -> {
                t.print("🌎 Website found: ${brightGreen(it.name)}; ")
            }

            is Event.OnExtractorTypeFound -> {
                t.println("extractor type: ${brightYellow(it.type)}")
                fullDirectory /= "${it.type}-${it.name}".toPath()
                val number = limit?.toString() ?: "all"
                spinner = createSpinner("📝 Collecting ${bold(number)} media from ${it.type} ${bold(it.name)}")
            }

            is Event.OnMediaQueried -> {
                total += it.amount
                spinner?.update(Pair(++spin, total))
            }

            is Event.OnQueryCompleted -> {
                spinner?.update(Pair(0, it.total))
                spinner?.stop()
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
    val finalParallel = parallel ?: if (response.extractor == ExtractorType.Coomer) 3 else 5
    Logger.i(appTag) { "Parallel: $finalParallel" }

    val downloads = startDownloads(response.media, fetch, fullDirectory, finalParallel)

    // Removing duplicates
    removeDuplicates(downloads) {
        onStart = { t.println("\n🚮 Removing duplicated downloads...") }
        onZeroByte = { t.println("[${brightBlue("Z")}] $it") }
        onDuplicate = { t.println("[${brightRed("D")}] $it") }
    }

    t.print("\n🌟 Done!")
}