package com.mattz.launcher.feature.bento

import java.net.InetSocketAddress
import java.net.Socket

object ServerPingHelper {
    data class PingResult(
        val host: String,
        val port: Int,
        val isOnline: Boolean,
        val latencyMs: Long,
        val message: String
    )

    fun pingServer(host: String, port: Int = 25565, timeoutMs: Int = 3000): PingResult {
        val start = System.currentTimeMillis()
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(host, port), timeoutMs)
            val latency = System.currentTimeMillis() - start
            socket.close()
            PingResult(
                host = host,
                port = port,
                isOnline = true,
                latencyMs = latency,
                message = "Online (${latency}ms)"
            )
        } catch (e: Exception) {
            PingResult(
                host = host,
                port = port,
                isOnline = false,
                latencyMs = -1,
                message = "Offline (${e.localizedMessage ?: "Timeout"})"
            )
        }
    }
}
