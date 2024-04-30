package io.vinicius.umd.model

import okio.Path

data class Download(
    val url: String,
    val filePath: Path,
    val output: String,
    val isSuccess: Boolean,
    val hash: String,
)