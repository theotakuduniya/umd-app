package io.vinicius.umd.ktx

fun Int.padLeft(length: Int): String {
    val numberAsString = this.toString()
    val paddingSize = length - numberAsString.length
    val padding = if (paddingSize > 0) "0".repeat(paddingSize) else ""
    return "$padding$numberAsString"
}