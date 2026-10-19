package com.mattz.launcher.feature.bento

import com.mattz.launcher.setting.AllSettings

object JvmOptimizerHelper {
    enum class JvmPreset(val displayName: String, val description: String, val flags: String) {
        LOW_END(
            "Low-End (2GB - 3GB)",
            "Optimasi GC agresif untuk perangkat dengan RAM terbatas.",
            "-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC"
        ),
        BALANCED(
            "Balanced (4GB - 6GB)",
            "Keseimbangan optimal antara kelancaran render dan jeda stutter minimal.",
            "-XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch -XX:+UnlockExperimentalVMOptions"
        ),
        EXTREME_FPS(
            "Extreme FPS (Sodium/OptiFine)",
            "ZGC / Low-Latency flags untuk frame rate tertinggi tanpa lag spike.",
            "-XX:+UseZGC -XX:+AlwaysPreTouch -XX:+ExplicitGCInvokesConcurrent -XX:+OptimizeStringConcat"
        ),
        AIKAR(
            "Aikar's Flags (High Stability)",
            "Standar tuning JVM paling populer untuk stabilitas mutlak Minecraft.",
            "-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1"
        )
    }

    fun applyPreset(preset: JvmPreset) {
        AllSettings.javaArgs.put(preset.flags).save()
    }

    fun getCurrentArgs(): String {
        return AllSettings.javaArgs.getValue()
    }
}

