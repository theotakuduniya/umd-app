package io.vinicius.umd.util

import io.vinicius.umd.model.Download
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

fun createReport(directory: Path, downloads: List<Download>) {
    val filePath = directory / "_report.md".toPath()
    val totalFailed = downloads.count { !it.isSuccess }

    fs.sink(filePath).buffer().use { file ->
        file.writeUtf8("# UMD - Download Report\n\n")
        file.writeUtf8("## Failed Downloads\n")
        file.writeUtf8("- Total: $totalFailed\n")

        downloads
            .filter { !it.isSuccess }
            .forEach {
                file.writeUtf8("### 🔗 Link: ${it.url} - ❌ **Failure**\n")
                file.writeUtf8("### 📝 Output:\n")
                file.writeUtf8("```\n")
                file.writeUtf8("${it.output}\n")
                file.writeUtf8("```\n")
                file.writeUtf8("---\n")
            }
    }
}