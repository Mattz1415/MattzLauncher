package com.mattz.launcher.feature.bento

import com.mattz.launcher.utils.path.PathManager
import java.io.File

object CrashLogAnalyzerHelper {
    data class CrashDiagnosis(
        val reportFile: String,
        val summary: String,
        val suspectedCause: String,
        val recommendedSolution: String,
        val rawSnippet: String
    )

    fun analyzeLatestCrash(): CrashDiagnosis {
        val crashDir = File(PathManager.DIR_GAME_HOME, ".minecraft/crash-reports")
        var targetFile: File? = null
        if (crashDir.exists() && crashDir.isDirectory) {
            val list = crashDir.listFiles()?.filter { it.isFile && (it.name.endsWith(".txt") || it.name.endsWith(".log")) }
                ?.sortedByDescending { it.lastModified() }
            if (!list.isNullOrEmpty()) {
                targetFile = list.first()
            }
        }

        // If no crash report found, check launcher_log
        if (targetFile == null) {
            val launcherLogDir = File(PathManager.DIR_GAME_HOME, "launcher_log")
            if (launcherLogDir.exists() && launcherLogDir.isDirectory) {
                val list = launcherLogDir.listFiles()?.filter { it.isFile }?.sortedByDescending { it.lastModified() }
                if (!list.isNullOrEmpty()) {
                    targetFile = list.first()
                }
            }
        }

        if (targetFile == null || !targetFile.exists()) {
            return CrashDiagnosis(
                reportFile = "Tidak ada crash report",
                summary = "Sistem Bersih",
                suspectedCause = "Tidak ditemukan laporan crash baru di .minecraft/crash-reports.",
                recommendedSolution = "Game berjalan normal. Jika mengalami kendala peluncuran, periksa koneksi atau mod yang terpasang.",
                rawSnippet = "Log OK."
            )
        }

        val content = try {
            targetFile.readText()
        } catch (e: Exception) {
            ""
        }

        var cause = "Kesalahan eksekusi umum Java / Game"
        var solution = "Coba restart launcher atau periksa kecocokan versi Fabric/Forge."

        when {
            content.contains("java.lang.OutOfMemoryError", ignoreCase = true) || content.contains("Metaspace", ignoreCase = true) -> {
                cause = "Alokasi Memori RAM Terlalu Rendah (OutOfMemoryError)"
                solution = "Buka menu Optimizer atau Settings, lalu naikkan alokasi RAM minimal 2GB - 4GB."
            }
            content.contains("MixinApplyError", ignoreCase = true) || content.contains("org.spongepowered.asm.mixin", ignoreCase = true) -> {
                cause = "Konflik Antar Mod (Mixin Transformer Conflict)"
                solution = "Ada 2 atau lebih mod yang memodifikasi kode game yang sama. Nonaktifkan mod yang baru dipasang di Mod Manager."
            }
            content.contains("ClassNotFoundException", ignoreCase = true) || content.contains("NoClassDefFoundError", ignoreCase = true) -> {
                cause = "Dependensi / Library Mod Hilang (Missing Dependency)"
                solution = "Mod membutuhkan library pendukung (misal Fabric API, Cloth Config, Architecture API). Unduh mod pendukung tersebut."
            }
            content.contains("libGL.so", ignoreCase = true) || content.contains("glGetString", ignoreCase = true) || content.contains("EGL_BAD", ignoreCase = true) -> {
                cause = "Crash Driver Grafis / Renderer OpenGL"
                solution = "Renderer saat ini tidak kompatibel dengan GPU perangkat. Buka Settings dan ganti renderer ke Holy GL4ES atau VirGL."
            }
            content.contains("NoSuchMethodError", ignoreCase = true) -> {
                cause = "Ketidakcocokan Versi Mod & Game"
                solution = "Mod yang dipasang dibuat untuk versi Minecraft yang berbeda. Pastikan versi mod sama persis dengan versi Minecraft Anda."
            }
        }

        val snippet = if (content.length > 500) content.take(500) + "\n...[truncated]" else content

        return CrashDiagnosis(
            reportFile = targetFile.name,
            summary = "Terdeteksi Masalah di ${targetFile.name}",
            suspectedCause = cause,
            recommendedSolution = solution,
            rawSnippet = snippet
        )
    }
}
