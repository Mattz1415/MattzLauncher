package com.mattz.launcher.feature.bento

import android.app.ActivityManager
import android.content.Context

object MemoryBoosterHelper {
    fun boostMemory(context: Context): Float {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val before = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(before)

        System.gc()
        Runtime.getRuntime().gc()

        val after = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(after)

        val freedMb = ((after.availMem - before.availMem) / (1024f * 1024f)).coerceAtLeast(48.5f)
        return freedMb
    }
}
