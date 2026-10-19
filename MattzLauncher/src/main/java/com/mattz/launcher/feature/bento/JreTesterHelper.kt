package com.mattz.launcher.feature.bento

import com.mattz.launcher.utils.path.PathManager
import java.io.File

object JreTesterHelper {
    data class BenchmarkResult(val scoreMs: Long, val details: String)

    fun runBenchmark(): BenchmarkResult {
        val start = System.currentTimeMillis()
        var dummy: Long = 0
        for (i in 0 until 2_000_000) {
            dummy += (i * 3) xor (i shr 2)
        }
        val elapsed = (System.currentTimeMillis() - start).coerceAtLeast(1)

        val rtDir = try { File(PathManager.DIR_MULTIRT_HOME) } catch (e: Throwable) { File(PathManager.DIR_GAME_HOME, "runtimes") }
        val installedRuntimes = rtDir.listFiles()?.filter { it.isDirectory }?.map { it.name } ?: emptyList()

        return BenchmarkResult(
            scoreMs = elapsed,
            details = "Performa Komputasi JVM: ${elapsed}ms | JRE Terpasang: ${installedRuntimes.joinToString(", ").ifEmpty { "Default Runtime" }}"
        )
    }
}
