package com.mattz.launcher.feature.bento

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.mattz.launcher.utils.path.PathManager
import java.io.File

object ScreenshotGalleryHelper {
    data class ScreenshotItem(val file: File, val fileName: String, val dateString: String)

    fun getScreenshots(): List<ScreenshotItem> {
        val dir = File(PathManager.DIR_GAME_HOME, ".minecraft/screenshots")
        if (!dir.exists() || !dir.isDirectory) return emptyList()

        return dir.listFiles()?.filter { it.isFile && it.name.endsWith(".png") }
            ?.sortedByDescending { it.lastModified() }
            ?.map {
                val date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date(it.lastModified()))
                ScreenshotItem(it, it.name, date)
            } ?: emptyList()
    }

    fun shareScreenshot(context: Context, item: ScreenshotItem) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", item.file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Screenshot"))
    }
}
