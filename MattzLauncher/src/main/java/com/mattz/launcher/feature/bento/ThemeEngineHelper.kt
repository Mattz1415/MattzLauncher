package com.mattz.launcher.feature.bento

import android.content.Context
import android.graphics.Color

object ThemeEngineHelper {
    private const val PREF_NAME = "mattz_theme_engine"
    private const val KEY_ACCENT = "active_accent_color"

    enum class ThemeAccent(val displayName: String, val hexColor: String, val intColor: Int) {
        NEON_CYAN("⚡ Neon Cyan", "#00F0FF", Color.parseColor("#00F0FF")),
        CYBER_PURPLE("🔮 Cyber Purple", "#A855F7", Color.parseColor("#A855F7")),
        MATRIX_EMERALD("🌿 Matrix Emerald", "#10B981", Color.parseColor("#10B981")),
        CRIMSON_RED("🔥 Crimson Red", "#F43F5E", Color.parseColor("#F43F5E")),
        NEON_GOLD("⭐ Neon Gold", "#F59E0B", Color.parseColor("#F59E0B"))
    }

    fun getActiveAccent(context: Context): ThemeAccent {
        val name = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ACCENT, ThemeAccent.NEON_CYAN.name)
        return try {
            ThemeAccent.valueOf(name ?: ThemeAccent.NEON_CYAN.name)
        } catch (e: Exception) {
            ThemeAccent.NEON_CYAN
        }
    }

    fun setAccent(context: Context, accent: ThemeAccent) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_ACCENT, accent.name).apply()
    }
}
