package com.mattz.launcher.feature.bento

import java.util.Calendar

object GreetingHelper {
    fun getGreeting(username: String?): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val name = username?.ifEmpty { "Penjelajah" } ?: "Penjelajah"

        return when (hour) {
            in 4..11 -> "Selamat Pagi, $name"
            in 12..16 -> "Selamat Siang, $name"
            in 17..20 -> "Selamat Sore, $name"
            else -> "Selamat Malam, $name"
        }
    }
}
