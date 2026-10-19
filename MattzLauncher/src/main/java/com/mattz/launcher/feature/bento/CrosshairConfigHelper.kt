package com.mattz.launcher.feature.bento

import android.content.Context

object CrosshairConfigHelper {
    private const val PREF_NAME = "mattz_crosshair_config"
    private const val KEY_CROSSHAIR = "crosshair_style"

    enum class CrosshairStyle(val displayName: String, val symbol: String) {
        CLASSIC_CROSS("Classic Cross", "┼"),
        MINIMAL_DOT("Minimal Dot", "•"),
        CIRCLE_DOT("Circle Dot", "⦿"),
        CYBER_CROSS("Cyber Cross", "✛"),
        CHEVRON("Chevron Angle", "∧")
    }

    fun getStyle(context: Context): CrosshairStyle {
        val name = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CROSSHAIR, CrosshairStyle.CLASSIC_CROSS.name)
        return try {
            CrosshairStyle.valueOf(name ?: CrosshairStyle.CLASSIC_CROSS.name)
        } catch (e: Exception) {
            CrosshairStyle.CLASSIC_CROSS
        }
    }

    fun setStyle(context: Context, style: CrosshairStyle) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_CROSSHAIR, style.name).apply()
    }
}
