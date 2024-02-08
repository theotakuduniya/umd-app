package io.vinicius.umd.ktx

import okio.ByteString
import okio.FileSystem
import okio.Path
import okio.buffer

fun Path.byteString(): ByteString {
    return FileSystem.SYSTEM.source(this).buffer().readByteString()
}

fun Path.delete() {
    FileSystem.SYSTEM.delete(this)
}

fun Path.exists(): Boolean {
    return FileSystem.SYSTEM.exists(this)
}

fun Path.size(): Long {
    return FileSystem.SYSTEM.metadata(this).size ?: 0
}