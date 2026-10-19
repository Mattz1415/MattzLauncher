package com.mattz.launcher.feature.unpack

import android.content.Context
import com.mattz.launcher.feature.log.Logging.e
import com.mattz.launcher.utils.CopyDefaultFromAssets.Companion.copyFromAssets
import com.mattz.launcher.utils.path.PathManager
import net.kdt.pojavlaunch.Tools

class UnpackSingleFilesTask(val context: Context) : AbstractUnpackTask() {
    override fun isNeedUnpack(): Boolean = true

    override fun run() {
        runCatching {
            copyFromAssets(context)
            Tools.copyAssetFile(context, "resolv.conf", PathManager.DIR_DATA, false)
        }.getOrElse { e("AsyncAssetManager", "Failed to unpack critical components !") }
    }
}