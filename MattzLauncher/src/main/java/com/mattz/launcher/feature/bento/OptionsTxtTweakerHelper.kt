package com.mattz.launcher.feature.bento

import com.mattz.launcher.utils.path.PathManager
import java.io.File

object OptionsTxtTweakerHelper {
    private fun getOptionsFile(): File {
        val file = File(PathManager.DIR_GAME_HOME, ".minecraft/options.txt")
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.writeText("gamma:1.0\nfov:0.0\nrenderDistance:8\nenableVsync:false\nguiScale:0\nao:true\nrenderClouds:true\nparticles:0\nentityShadows:true\nautoJump:false\n")
        }
        return file
    }

    private fun loadMap(): MutableMap<String, String> {
        val file = getOptionsFile()
        val map = mutableMapOf<String, String>()
        file.readLines().forEach { line ->
            if (line.contains(":")) {
                val k = line.substringBefore(":").trim()
                val v = line.substringAfter(":").trim()
                map[k] = v
            }
        }
        return map
    }

    private fun saveMap(map: Map<String, String>) {
        val file = getOptionsFile()
        val text = map.entries.joinToString("\n") { "${it.key}:${it.value}" } + "\n"
        file.writeText(text)
    }

    fun isFullbrightEnabled(): Boolean {
        return try {
            val map = loadMap()
            (map["gamma"]?.toDoubleOrNull() ?: 1.0) >= 9.0
        } catch (e: Exception) {
            false
        }
    }

    fun toggleFullbright(): Boolean {
        val map = loadMap()
        val currentlyOn = isFullbrightEnabled()
        map["gamma"] = if (currentlyOn) "1.0" else "10.0"
        saveMap(map)
        return !currentlyOn
    }

    fun setOption(key: String, value: String) {
        val map = loadMap()
        map[key] = value
        saveMap(map)
    }

    fun getOption(key: String, defaultVal: String = ""): String {
        val map = loadMap()
        return map[key] ?: defaultVal
    }

    fun getBooleanOption(key: String, defaultVal: Boolean = false): Boolean {
        val v = getOption(key)
        return if (v.isEmpty()) defaultVal else v.toBoolean()
    }

    fun getIntOption(key: String, defaultVal: Int = 0): Int {
        return getOption(key).toIntOrNull() ?: defaultVal
    }

    fun setQuickPresets(fovVal: Float = 0.5f, chunks: Int = 8, vsync: Boolean = false) {
        val map = loadMap()
        map["fov"] = fovVal.toString()
        map["renderDistance"] = chunks.toString()
        map["enableVsync"] = vsync.toString()
        saveMap(map)
    }
}
