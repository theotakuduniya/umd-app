package io.vinicius.umd.ktx

import kotlinx.datetime.LocalDateTime

fun LocalDateTime.format(): String {
    val date = "$year${monthNumber.padLeft(2)}${dayOfMonth.padLeft(2)}"
    val time = "${hour.padLeft(2)}${minute.padLeft(2)}${second.padLeft(2)}"
    return "$date-$time"
}