package com.mattz.launcher.setting.unit

import com.mattz.launcher.setting.Settings.Manager

class LongSettingUnit(key: String, defaultValue: Long) : AbstractSettingUnit<Long>(key, defaultValue) {
    override fun getValue() = Manager.getValue(key, defaultValue) { it.toLongOrNull() }
}