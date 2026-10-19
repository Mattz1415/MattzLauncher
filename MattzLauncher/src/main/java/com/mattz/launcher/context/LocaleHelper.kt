package com.mattz.launcher.context

import android.content.Context
import android.content.ContextWrapper
import com.mattz.launcher.setting.Settings
import com.mattz.launcher.utils.path.PathManager
import net.kdt.pojavlaunch.prefs.LauncherPreferences

class LocaleHelper(context: Context) : ContextWrapper(context) {
    companion object {
        fun setLocale(context: Context): ContextWrapper {
            //初始化路径
            PathManager.initContextConstants(context)
            //刷新启动器设置
            Settings.refreshSettings()

            LauncherPreferences.loadPreferences()
            return LocaleHelper(context)
        }
    }
}