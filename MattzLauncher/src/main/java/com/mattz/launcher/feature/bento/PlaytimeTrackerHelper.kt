package com.mattz.launcher.feature.bento

import android.content.Context
import android.content.SharedPreferences

object PlaytimeTrackerHelper {
    private const val PREF_NAME = "mattz_playtime_pref"
    private const val KEY_TOTAL_MS = "total_playtime_ms"
    private const val KEY_SESSIONS = "session_count"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun recordSessionLaunch(context: Context) {
        val prefs = getPrefs(context)
        val sessions = prefs.getInt(KEY_SESSIONS, 0) + 1
        prefs.edit().putInt(KEY_SESSIONS, sessions).apply()
    }

    fun addPlaytime(context: Context, durationMs: Long) {
        val prefs = getPrefs(context)
        val current = prefs.getLong(KEY_TOTAL_MS, 0L)
        prefs.edit().putLong(KEY_TOTAL_MS, current + durationMs).apply()
    }

    fun getStatsFormatted(context: Context): Pair<String, Int> {
        val prefs = getPrefs(context)
        val totalMs = prefs.getLong(KEY_TOTAL_MS, 0L)
        val sessions = prefs.getInt(KEY_SESSIONS, 1)

        val totalMinutes = totalMs / (1000 * 60)
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60

        val timeStr = if (hours > 0) "${hours}j ${mins}m" else "${mins} menit"
        return Pair(timeStr, sessions)
    }
}
