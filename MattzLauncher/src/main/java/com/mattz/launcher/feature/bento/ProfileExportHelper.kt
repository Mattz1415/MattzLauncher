package com.mattz.launcher.feature.bento

import com.mattz.launcher.utils.path.PathManager
import java.io.File

object ProfileExportHelper {
    fun exportProfile(): File {
        val backupDir = File(PathManager.DIR_GAME_HOME, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()

        val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
        val exportFile = File(backupDir, "MattzConfig_$timeStamp.json")

        val srcFile = try { PathManager.FILE_SETTINGS } catch (e: Throwable) { File(PathManager.DIR_GAME_HOME, "launcher_settings.json") }
        if (srcFile.exists()) {
            srcFile.copyTo(exportFile, overwrite = true)
        } else {
            exportFile.writeText("{\"launcher\": \"MattzLauncher\", \"version\": 1}")
        }

        return exportFile
    }

    fun importProfile(file: File): Boolean {
        return try {
            val target = try { PathManager.FILE_SETTINGS } catch (e: Throwable) { File(PathManager.DIR_GAME_HOME, "launcher_settings.json") }
            if (file.exists()) {
                file.copyTo(target, overwrite = true)
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }
}
