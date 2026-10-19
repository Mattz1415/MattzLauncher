package com.mattz.launcher.feature.bento

import com.mattz.launcher.utils.path.PathManager
import java.io.File

object ModManagerHelper {
    data class ModItem(
        val file: File,
        val modName: String,
        val isEnabled: Boolean,
        val sizeFormatted: String
    )

    fun getMods(): List<ModItem> {
        val modsDir = File(PathManager.DIR_GAME_HOME, ".minecraft/mods")
        if (!modsDir.exists() || !modsDir.isDirectory) {
            modsDir.mkdirs()
            return emptyList()
        }

        return modsDir.listFiles()?.filter {
            it.isFile && (it.name.endsWith(".jar") || it.name.endsWith(".jar.disabled"))
        }?.map { file ->
            val isEnabled = file.name.endsWith(".jar")
            val cleanName = file.name.removeSuffix(".disabled").removeSuffix(".jar")
            val sizeMb = String.format("%.2f MB", file.length() / (1024f * 1024f))
            ModItem(file, cleanName, isEnabled, sizeMb)
        }?.sortedBy { it.modName.lowercase() } ?: emptyList()
    }

    fun toggleMod(mod: ModItem): Boolean {
        val parent = mod.file.parentFile ?: return false
        val newFile = if (mod.isEnabled) {
            File(parent, "${mod.file.name}.disabled")
        } else {
            File(parent, mod.file.name.removeSuffix(".disabled"))
        }
        return mod.file.renameTo(newFile)
    }
}
