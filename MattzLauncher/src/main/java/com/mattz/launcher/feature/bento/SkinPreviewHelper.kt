package com.mattz.launcher.feature.bento

import com.mattz.launcher.utils.path.PathManager
import java.io.File
import java.net.URL

object SkinPreviewHelper {
    fun getAvatarUrl(username: String): String {
        val clean = username.trim().ifEmpty { "Steve" }
        return "https://minotar.net/armor/bust/$clean/200.png"
    }

    fun downloadSkin(username: String): File? {
        val clean = username.trim().ifEmpty { "Steve" }
        return try {
            val url = URL("https://minotar.net/skin/$clean")
            val targetDir = try { PathManager.DIR_USER_SKIN } catch (e: Throwable) { File(PathManager.DIR_GAME_HOME, "user_skin") }
            if (!targetDir.exists()) targetDir.mkdirs()
            val targetFile = File(targetDir, "${clean}_skin.png")
            url.openStream().use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            targetFile
        } catch (e: Exception) {
            null
        }
    }
}
