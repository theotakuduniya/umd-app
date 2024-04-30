package io.vinicius.umd.ktx

import okio.FileSystem
import okio.HashingSink
import okio.Path
import okio.SYSTEM
import okio.blackholeSink
import okio.buffer
import okio.use

fun Path.sha1(): String {
    val sink = HashingSink.sha1(blackholeSink())
    FileSystem.SYSTEM.source(this).buffer().use {
        it.readAll(sink)
    }

    return sink.hash.hex()
}

fun Path.delete() = FileSystem.SYSTEM.delete(this)

fun Path.exists() = FileSystem.SYSTEM.exists(this)

fun Path.size() = FileSystem.SYSTEM.metadata(this).size ?: 0