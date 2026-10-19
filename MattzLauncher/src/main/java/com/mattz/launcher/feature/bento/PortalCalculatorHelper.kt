package com.mattz.launcher.feature.bento

object PortalCalculatorHelper {
    data class Coords(val x: Int, val y: Int, val z: Int)

    fun overworldToNether(x: Int, y: Int, z: Int): Coords {
        return Coords(Math.floorDiv(x, 8), y, Math.floorDiv(z, 8))
    }

    fun netherToOverworld(x: Int, y: Int, z: Int): Coords {
        return Coords(x * 8, y, z * 8)
    }
}
