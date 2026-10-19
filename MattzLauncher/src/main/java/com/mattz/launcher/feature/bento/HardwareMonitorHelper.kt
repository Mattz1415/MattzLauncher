package com.mattz.launcher.feature.bento

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import java.io.File

object HardwareMonitorHelper {
    data class HardwareStats(
        val ramUsedGb: Float,
        val ramTotalGb: Float,
        val ramPercent: Int,
        val cpuCores: Int,
        val storageFreeGb: Float,
        val storageTotalGb: Float,
        val batteryTempCelsius: Float,
        val batteryPercent: Int
    )

    fun getLiveStats(context: Context): HardwareStats {
        // RAM
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        val totalRam = memInfo.totalMem / (1024f * 1024f * 1024f)
        val availRam = memInfo.availMem / (1024f * 1024f * 1024f)
        val usedRam = totalRam - availRam
        val ramPercent = if (totalRam > 0) ((usedRam / totalRam) * 100).toInt() else 0

        // CPU
        val cpuCores = Runtime.getRuntime().availableProcessors()

        // Storage
        val storagePath = Environment.getExternalStorageDirectory().path
        val stat = StatFs(storagePath)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availBlocks = stat.availableBlocksLong
        val storageFree = (availBlocks * blockSize) / (1024f * 1024f * 1024f)
        val storageTotal = (totalBlocks * blockSize) / (1024f * 1024f * 1024f)

        // Battery
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val rawTemp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val batteryTemp = rawTemp / 10.0f
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPercent = if (level >= 0 && scale > 0) (level * 100) / scale else 50

        return HardwareStats(
            ramUsedGb = usedRam,
            ramTotalGb = totalRam,
            ramPercent = ramPercent,
            cpuCores = cpuCores,
            storageFreeGb = storageFree,
            storageTotalGb = storageTotal,
            batteryTempCelsius = batteryTemp,
            batteryPercent = batteryPercent
        )
    }
}
