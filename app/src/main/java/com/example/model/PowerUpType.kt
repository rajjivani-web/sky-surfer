package com.example.model

enum class PowerUpType(val title: String, val durationMs: Long, val iconEmoji: String) {
    SHIELD("Energy Shield", 10000L, "🛡️"),
    MAGNET("Coin Magnet", 8000L, "🧲"),
    SPEED_BOOST("Hyper Speed (x2)", 6000L, "⚡")
}
