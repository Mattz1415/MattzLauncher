package com.mattz.launcher.feature.download.enums

import com.mattz.launcher.feature.download.platform.AbstractPlatformHelper
import com.mattz.launcher.feature.download.platform.curseforge.CurseForgeHelper
import com.mattz.launcher.feature.download.platform.modrinth.ModrinthHelper

enum class Platform(val pName: String, val helper: AbstractPlatformHelper) {
    MODRINTH("Modrinth", ModrinthHelper()),
    CURSEFORGE("CurseForge", CurseForgeHelper())
}