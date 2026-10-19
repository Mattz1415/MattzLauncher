package com.mattz.launcher.feature.bento

import com.mattz.launcher.utils.path.PathManager
import java.io.File

object PackManagerHelper {
    data class PackInfo(val name: String, val isShader: Boolean, val sizeFormatted: String)

    fun getPacks(): List<PackInfo> {
        val list = mutableListOf<PackInfo>()

        val resDir = File(PathManager.DIR_GAME_HOME, ".minecraft/resourcepacks")
        if (resDir.exists() && resDir.isDirectory) {
            resDir.listFiles()?.forEach {
                val sizeMb = String.format("%.1f MB", it.length() / (1024f * 1024f))
                list.add(PackInfo(it.name, false, sizeMb))
            }
        }

        val shaderDir = File(PathManager.DIR_GAME_HOME, ".minecraft/shaderpacks")
        if (shaderDir.exists() && shaderDir.isDirectory) {
            shaderDir.listFiles()?.forEach {
                val sizeMb = String.format("%.1f MB", it.length() / (1024f * 1024f))
                list.add(PackInfo(it.name, true, sizeMb))
            }
        }

        return list
    }
}
