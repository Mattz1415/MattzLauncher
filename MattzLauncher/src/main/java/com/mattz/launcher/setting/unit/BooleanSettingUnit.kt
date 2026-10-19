package com.mattz.launcher.setting.unit

import com.mattz.launcher.setting.Settings.Manager

class BooleanSettingUnit(key: String, defaultValue: Boolean) : AbstractSettingUnit<Boolean>(key, defaultValue) {
    override fun getValue() = Manager.getValue(key, defaultValue) { it.toBoolean() }
}