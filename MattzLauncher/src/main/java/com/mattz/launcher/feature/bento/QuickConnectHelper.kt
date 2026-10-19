package com.mattz.launcher.feature.bento

import android.content.Context

object QuickConnectHelper {
    private const val PREF_NAME = "mattz_quick_connect"
    private const val KEY_LAST_IP = "last_server_ip"
    private const val KEY_LAST_PORT = "last_server_port"

    @JvmOverloads
    fun saveServer(context: Context, ip: String, port: Int = 25565) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_LAST_IP, ip)
            .putInt(KEY_LAST_PORT, port)
            .apply()
    }

    fun getLastServer(context: Context): Pair<String, Int> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val ip = prefs.getString(KEY_LAST_IP, "mc.hypixel.net") ?: "mc.hypixel.net"
        val port = prefs.getInt(KEY_LAST_PORT, 25565)
        return Pair(ip, port)
    }
}
