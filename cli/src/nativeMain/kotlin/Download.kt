package io.vinicius.umd

import io.vinicius.umd.ktx.byteString
import io.vinicius.umd.ktx.exists
import io.vinicius.umd.ktx.format
import io.vinicius.umd.model.Media
import io.vinicius.umd.util.Fetch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

@OptIn(ExperimentalCoroutinesApi::class)
fun startDownloads(media: List<Media>, directory: Path, parallel: Int): List<Download> {
    val dispatcher = Dispatchers.IO.limitedParallelism(parallel)
    val downloads = mutableListOf<Download>()
    val (pb, state) = createProgressBar("Downloading", media.size.toLong())

    // Create the directory if it doesn't exist
    if (!directory.exists()) {
        FileSystem.SYSTEM.createDirectories(directory)
    }

    t.println()

    runBlocking {
        val jobs = media.mapIndexed { index, m ->
            launch(dispatcher) {
                downloads.add(downloadMedia(m, directory, index + 1))
                pb.update(state.updateTotal(downloads.size.toLong()))
            }
        }

        jobs.joinAll()
        pb.stop()
    }

    return downloads
}

private val fetch = Fetch()

private suspend fun downloadMedia(media: Media, directory: Path, index: Int): Download {
    val created = media.metadata["created"] as? String
    val dateTime = if (created == null) Clock.System.now().toLocalDateTime(TimeZone.UTC) else LocalDateTime.parse(created)
    val name = media.metadata["name"] as String
    val extension = media.extension ?: "mp4"
    val filePath = "${dateTime.format()}-$name-$index.$extension".toPath()
    val fullPath = directory / filePath

    val output = try {
        fetch.downloadFile(media.url, fullPath.toString())
        ""
    } catch (e: Exception) {
        e.message ?: "Unknown"
    }

    return Download(
        url = media.url,
        filePath = fullPath,
        output = output,
        isSuccess = output.isEmpty(),
        hash = if (fullPath.exists()) fullPath.byteString().sha1().hex() else ""
    )
}