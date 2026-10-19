package com.mattz.launcher.setting.unit

import com.mattz.launcher.setting.Settings.Manager

class FloatSettingUnit(key: String, defaultValue: Float) : AbstractSettingUnit<Float>(key, defaultValue) {
    override fun getValue() = Manager.getValue(key, defaultValue) { it.toFloatOrNull() }
}