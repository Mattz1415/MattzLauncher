package com.mattz.launcher.feature.bento

import android.content.Context
import com.mattz.launcher.utils.path.PathManager
import java.io.File

object JunkCleanerHelper {
    data class CleanResult(val freedBytes: Long, val freedMb: Float)

    fun cleanJunk(context: Context): CleanResult {
        var totalFreed: Long = 0

        // 1. Logs directory
        val logDir = File(PathManager.DIR_GAME_HOME, "launcher_log")
        totalFreed += cleanDirectory(logDir)

        // 2. Cache string and addons cache
        val cacheStr = File(context.cacheDir, "string_cache")
        totalFreed += cleanDirectory(cacheStr)
        val addonsCache = File(context.cacheDir, "addons_info_cache")
        totalFreed += cleanDirectory(addonsCache)

        // 3. Context cache dir
        totalFreed += cleanDirectory(context.cacheDir)
        context.externalCacheDir?.let {
            totalFreed += cleanDirectory(it)
        }

        // 4. Old crash reports exceeding 5 files
        val crashDir = File(PathManager.DIR_GAME_HOME, ".minecraft/crash-reports")
        if (crashDir.exists() && crashDir.isDirectory) {
            val reports = crashDir.listFiles()?.sortedByDescending { it.lastModified() }
            if (reports != null && reports.size > 5) {
                for (i in 5 until reports.size) {
                    val len = reports[i].length()
                    if (reports[i].delete()) totalFreed += len
                }
            }
        }

        val freedMb = totalFreed / (1024f * 1024f)
        return CleanResult(totalFreed, freedMb)
    }

    private fun cleanDirectory(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0
        var freed: Long = 0
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                freed += cleanDirectory(file)
                file.delete()
            } else {
                val len = file.length()
                if (file.delete()) {
                    freed += len
                }
            }
        }
        return freed
    }
}
