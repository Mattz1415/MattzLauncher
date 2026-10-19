package com.mattz.launcher.feature.bento

import com.mattz.launcher.utils.path.PathManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object PlayerNotesHelper {
    data class NoteEntry(val title: String, val coordinates: String, val description: String)

    private fun getNotesFile(): File {
        val file = File(PathManager.DIR_GAME_HOME, "player_notes.json")
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            val initial = JSONArray().apply {
                put(JSONObject().apply {
                    put("title", "Main Base")
                    put("coordinates", "X: 120, Y: 68, Z: -340")
                    put("description", "Rumah utama & chest room")
                })
                put(JSONObject().apply {
                    put("title", "Nether Fortress")
                    put("coordinates", "X: -45, Y: 56, Z: 180")
                    put("description", "Blaze rod spawner & wart farm")
                })
            }
            file.writeText(initial.toString(2))
        }
        return file
    }

    fun getNotes(): List<NoteEntry> {
        val file = getNotesFile()
        val list = mutableListOf<NoteEntry>()
        try {
            val arr = JSONArray(file.readText())
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(NoteEntry(
                    title = obj.optString("title", "Catatan"),
                    coordinates = obj.optString("coordinates", "X: 0, Y: 64, Z: 0"),
                    description = obj.optString("description", "")
                ))
            }
        } catch (e: Exception) {
            // Ignore
        }
        return list
    }

    fun addNote(title: String, coords: String, desc: String) {
        val file = getNotesFile()
        val arr = try {
            JSONArray(file.readText())
        } catch (e: Exception) {
            JSONArray()
        }
        arr.put(JSONObject().apply {
            put("title", title)
            put("coordinates", coords)
            put("description", desc)
        })
        file.writeText(arr.toString(2))
    }
}
