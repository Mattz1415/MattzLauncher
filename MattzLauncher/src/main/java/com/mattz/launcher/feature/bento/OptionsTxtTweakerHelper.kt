package com.mattz.launcher.feature.bento

import com.mattz.launcher.utils.path.PathManager
import java.io.File

object OptionsTxtTweakerHelper {
    private fun getOptionsFile(): File {
        val file = File(PathManager.DIR_GAME_HOME, ".minecraft/options.txt")
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.writeText("gamma:1.0\nfov:0.0\nrenderDistance:8\nenableVsync:false\nguiScale:0\n")
        }
        return file
    }

    fun isFullbrightEnabled(): Boolean {
        return try {
            val file = getOptionsFile()
            file.readLines().any { it.startsWith("gamma:") && (it.substringAfter("gamma:").toDoubleOrNull() ?: 1.0) >= 9.0 }
        } catch (e: Exception) {
            false
        }
    }

    fun toggleFullbright(): Boolean {
        val file = getOptionsFile()
        val lines = file.readLines().toMutableList()
        val currentlyOn = isFullbrightEnabled()
        val newGamma = if (currentlyOn) "1.0" else "10.0"

        var found = false
        for (i in lines.indices) {
            if (lines[i].startsWith("gamma:")) {
                lines[i] = "gamma:$newGamma"
                found = true
                break
            }
        }
        if (!found) {
            lines.add("gamma:$newGamma")
        }
        file.writeText(lines.joinToString("\n") + "\n")
        return !currentlyOn
    }

    fun setQuickPresets(fovVal: Float = 0.5f, chunks: Int = 8, vsync: Boolean = false) {
        val file = getOptionsFile()
        val map = mutableMapOf<String, String>()
        file.readLines().forEach { line ->
            if (line.contains(":")) {
                val k = line.substringBefore(":")
                val v = line.substringAfter(":")
                map[k] = v
            }
        }
        map["fov"] = fovVal.toString()
        map["renderDistance"] = chunks.toString()
        map["enableVsync"] = vsync.toString()

        val text = map.entries.joinToString("\n") { "${it.key}:${it.value}" } + "\n"
        file.writeText(text)
    }
}
