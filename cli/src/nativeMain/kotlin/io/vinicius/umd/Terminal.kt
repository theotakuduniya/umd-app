package io.vinicius.umd

import com.github.ajalt.mordant.animation.Animation
import com.github.ajalt.mordant.animation.textAnimation
import com.github.ajalt.mordant.rendering.TextColors.blue
import com.github.ajalt.mordant.rendering.TextColors.brightCyan
import com.github.ajalt.mordant.rendering.TextColors.magenta
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.rendering.TextStyles.underline
import com.github.ajalt.mordant.terminal.Terminal
import io.vinicius.umd.model.Media
import io.vinicius.umd.model.MediaType

val t = Terminal()

fun createSpinner(message: String): Animation<Pair<Int, Int>> {
    val spinner = listOf("-", "\\", "|", "/")
    return t.textAnimation { (spin, number) ->
        if (spin > 0) {
            val index = (spin - 1) % spinner.size
            "$message ${red(spinner[index])} $number"
        } else {
            "$message ${red("-")} $number media found"
        }
    }
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

    return "$emoji [$number] Downloading $label $url"
}