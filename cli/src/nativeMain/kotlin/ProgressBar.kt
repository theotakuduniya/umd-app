package io.vinicius.umd

import com.github.ajalt.mordant.animation.Animation
import com.github.ajalt.mordant.animation.animation
import com.github.ajalt.mordant.widgets.progressLayout
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class ProgressState(
    val completed: Long,
    val total: Long,
    val elapsedSeconds: Double = 0.0,
    val completedPerSecond: Double = 0.0,
    private val instant: Instant = Clock.System.now()
) {
    fun updateTotal(completed: Long): ProgressState {
        val elapsed = (Clock.System.now() - instant).inWholeMilliseconds / 1000.0
        return ProgressState(completed, total, elapsed, completed / elapsed)
    }
}

fun createProgressBar(text: String, total: Long): Pair<Animation<ProgressState>, ProgressState> {
    val pb = t.animation<ProgressState> {
        progressLayout {
            text(text)
            percentage()
            progressBar()
            completed()
            speed("B/s")
            timeRemaining()
        }.build(it.completed, it.total, it.elapsedSeconds, it.completedPerSecond)
    }

    return Pair(pb, ProgressState(0, total))
}