package com.mattz.launcher.feature.bento

import com.mattz.launcher.utils.path.PathManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object WorldBackupHelper {
    data class WorldItem(val folder: File, val name: String, val lastPlayed: String)

    fun listWorlds(): List<WorldItem> {
        val savesDir = File(PathManager.DIR_GAME_HOME, ".minecraft/saves")
        if (!savesDir.exists() || !savesDir.isDirectory) return emptyList()

        return savesDir.listFiles()?.filter { it.isDirectory }?.map {
            val date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(it.lastModified()))
            WorldItem(it, it.name, date)
        } ?: emptyList()
    }

    fun backupWorld(world: WorldItem): File {
        val backupDir = File(PathManager.DIR_GAME_HOME, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()

        val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
        val zipFile = File(backupDir, "${world.name}_backup_$timeStamp.zip")

        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            zipFolder(world.folder, world.folder.name, zos)
        }

        return zipFile
    }

    private fun zipFolder(folder: File, parentPath: String, zos: ZipOutputStream) {
        folder.listFiles()?.forEach { file ->
            val path = "$parentPath/${file.name}"
            if (file.isDirectory) {
                zipFolder(file, path, zos)
            } else {
                zos.putNextEntry(ZipEntry(path))
                FileInputStream(file).use { fis ->
                    fis.copyTo(zos)
                }
                zos.closeEntry()
            }
        }
    }
}
