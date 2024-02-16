package io.vinicius.umd

import com.github.ajalt.mordant.animation.Animation
import com.github.ajalt.mordant.animation.animation
import com.github.ajalt.mordant.animation.textAnimation
import com.github.ajalt.mordant.rendering.TextColors.blue
import com.github.ajalt.mordant.rendering.TextColors.brightCyan
import com.github.ajalt.mordant.rendering.TextColors.magenta
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.rendering.TextStyles.underline
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.progressLayout
import io.vinicius.umd.model.Media
import io.vinicius.umd.model.MediaType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

val t = Terminal()

data class ProgressState(
    val completed: Long,
    val total: Long,
    val elapsedSeconds: Double = 0.0,
    val completedPerSecond: Double = 0.0,
    private val instant: Instant = Clock.System.now(),
) {
    fun updateTotal(completed: Long): ProgressState {
        val elapsed = (Clock.System.now() - instant).inWholeMilliseconds / 1000.0
        return ProgressState(completed, total, elapsed, completed / elapsed)
    }
}

fun createProgressBar(text: String, total: Long): Pair<Animation<ProgressState>, ProgressState> {
    val pb = t.animation<ProgressState> {
        // Stupid bug out of nowhere
        val cps = if (it.completedPerSecond > 0) it.completedPerSecond else 0.0

        progressLayout {
            text(text)
            percentage()
            progressBar(width = 50)
            completed()
            speed(" dl/sec")
            timeRemaining()
        }.build(it.completed, it.total, it.elapsedSeconds, cps)
    }

    return Pair(pb, ProgressState(0, total))
}

fun printMostRecent(padding: Int): Animation<List<Pair<Int, Media>>> {
    return t.textAnimation { list ->
        list.joinToString("\n", "\n") {
            val (index, media) = it
            downloadInfo(index, padding, media)
        }
    }
}

private fun downloadInfo(index: Int, padding: Int, media: Media): String {
    val emoji: String
    val label: String
    val number = bold(blue((index + 1).toString().padStart(padding, '0')))
    val url = underline(brightCyan(media.url))

    if (media.mediaType == MediaType.Image) {
        emoji = "📸"
        label = magenta("image")
    } else {
        emoji = "📹"
        label = yellow("video")
    }

    return "$emoji [$number] Downloading $label $url ..."
}