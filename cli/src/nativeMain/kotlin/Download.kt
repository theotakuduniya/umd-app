package io.vinicius.umd

import co.touchlab.kermit.Logger
import io.vinicius.umd.ktx.byteString
import io.vinicius.umd.ktx.exists
import io.vinicius.umd.ktx.format
import io.vinicius.umd.model.Media
import io.vinicius.umd.model.MediaType
import io.vinicius.umd.util.Fetch
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

data class Download(
    val url: String,
    val filePath: Path,
    val output: String,
    val isSuccess: Boolean,
    val hash: String
)

fun startDownloads(fetch: Fetch, media: List<Media>, directory: Path, parallel: Int): List<Download> {
    val downloads = mutableListOf<Download>()
    val semaphore = Semaphore(parallel)
    val (pb, state) = createProgressBar("Downloading", media.size.toLong())

    // Create the directory if it doesn't exist
    if (!directory.exists()) {
        FileSystem.SYSTEM.createDirectories(directory)
    }

    t.println()

    runBlocking {
        val jobs = media.mapIndexed { index, m ->
            launch {
                semaphore.acquire()

                val pair = if (m.mediaType == MediaType.Unknown) expandMedia(m, fetch) else Pair(m, fetch)
                downloads.add(downloadMedia(pair, directory, index + 1))
                pb.update(state.updateTotal(downloads.size.toLong()))

                semaphore.release()
            }
        }

        jobs.joinAll()
        pb.stop()
    }

    return downloads
}

private fun expandMedia(media: Media, fetch: Fetch): Pair<Media, Fetch> {
    return try {
        val umd = Umd(media.url, metadata)
        val response = runBlocking { umd.queryMedia() }
        if (!metadata.containsKey(response.extractor) && response.metadata.isNotEmpty()) {
            metadata[response.extractor] = response.metadata
        }

        val m = response.media
        if (m.isNotEmpty()) Pair(m.first(), umd.configureFetch()) else Pair(media, fetch)
    } catch (e: Exception) {
        Logger.w("Umd-App") { "Failed to expand media URL: ${media.url}" }
        Pair(media, fetch)
    }
}

private suspend fun downloadMedia(pair: Pair<Media, Fetch>, directory: Path, index: Int): Download {
    val (m, f) = pair
    val created = m.metadata["created"] as? String
    val dateTime = if (created == null) Clock.System.now().toLocalDateTime(TimeZone.UTC) else LocalDateTime.parse(created)
    val name = m.metadata["name"] as String
    val extension = m.extension ?: "mp4"
    val filePath = "${dateTime.format()}-$name-$index.$extension".toPath()
    val fullPath = directory / filePath

    val output = if (m.mediaType != MediaType.Unknown) {
        try {
            Logger.i("Umd-App") { "Downloading: ${m.url}" }
            f.downloadFile(m.url, fullPath.toString())
            ""
        } catch (e: Exception) {
            Logger.w("Umd-App") { "Failed to download: ${m.url}" }
            e.message ?: "Unknown error"
        }
    } else {
        Logger.i("Umd-App") { "Ignoring unknown URL: ${m.url}" }
        ""
    }

    return Download(
        url = m.url,
        filePath = fullPath,
        output = output,
        isSuccess = output.isEmpty(),
        hash = if (fullPath.exists()) fullPath.byteString().sha1().hex() else ""
    )
}