package io.vinicius.umd.ktx

import io.vinicius.umd.util.fs
import okio.HashingSink
import okio.Path
import okio.blackholeSink
import okio.buffer
import okio.use

fun Path.sha1(): String {
    val sink = HashingSink.sha1(blackholeSink())
    fs.source(this).buffer().use {
        it.readAll(sink)
    }

    return sink.hash.hex()
}

fun Path.delete() = fs.delete(this)

fun Path.exists() = fs.exists(this)

fun Path.size() = fs.metadata(this).size ?: 0