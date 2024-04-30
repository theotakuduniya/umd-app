package io.vinicius.umd.util

import io.vinicius.umd.ktx.delete
import io.vinicius.umd.ktx.exists
import io.vinicius.umd.ktx.size
import io.vinicius.umd.model.Download

class RemoveEvent {
    var onStart: () -> Unit = {}
    var onZeroByte: (filePath: String) -> Unit = {}
    var onDuplicate: (filePath: String) -> Unit = {}
}

fun removeDuplicates(downloads: List<Download>, events: RemoveEvent.() -> Unit): Int {
    var numDeleted = 0
    val ev = RemoveEvent().apply(events)
    if (downloads.isNotEmpty()) ev.onStart()

    // Removing 0-byte files
    downloads.forEach {
        if (it.filePath.exists() && it.filePath.size() == 0L) {
            numDeleted++
            ev.onZeroByte(it.filePath.name)
            it.filePath.delete()
        }
    }

    // Removing duplicates
    downloads.groupBy { it.hash }.values.forEach {
        it.drop(1).forEach { download ->
            if (download.filePath.exists()) {
                numDeleted++
                ev.onDuplicate(download.filePath.name)
                download.filePath.delete()
            }
        }
    }

    return numDeleted
}