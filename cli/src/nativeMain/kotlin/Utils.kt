package io.vinicius.umd

import com.github.ajalt.mordant.rendering.TextColors.brightBlue
import com.github.ajalt.mordant.rendering.TextColors.brightRed
import io.vinicius.umd.ktx.delete
import io.vinicius.umd.ktx.exists
import io.vinicius.umd.ktx.size

fun removeDuplicates(downloads: List<Download>): Int {
    var numDeleted = 0
    t.println("\n🚮 Removing duplicated downloads...")

    // Removing 0-byte files
    downloads.forEach {
        if (it.filePath.exists() && it.filePath.size() == 0L) {
            numDeleted++
            t.println("[${brightBlue("Z")}] ${it.filePath.name}")
            it.filePath.delete()
        }
    }

    // Removing duplicates
    downloads.groupBy { it.hash }.values.forEach {
        it.drop(1).forEach { download ->
            if (download.filePath.exists()) {
                numDeleted++
                t.println("[${brightRed("D")}] ${download.filePath.name}")
                download.filePath.delete()
            }
        }
    }

    return numDeleted
}