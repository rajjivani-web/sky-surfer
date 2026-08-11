package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.TealNeon

enum class ParticleEffectType {
    DEFAULT_WIND,
    FLAME_FIRE,
    ICE_CRYSTAL,
    STAR_AURA,
    FEATHER_FLY,
    GHOST_TRANSLUCENT,
    LIGHTNING_BOLT,
    PUMPKIN_GLOW,
    SNOWFLAKE,
    GOLDEN_TRAIL
}

data class Skin(
    val id: String,
    val name: String,
    val description: String,
    val priceCoins: Int = 0,
    val priceUsdDisplay: String = "",
    val primaryColor: Color,
    val secondaryColor: Color,
    val particleEffect: ParticleEffectType,
    val isPremiumOnly: Boolean = false,
    val iconEmoji: String
) {
    companion object {
        val ALL_SKINS = listOf(
            Skin(
                id = "default_surfer",
                name = "Sky Surfer",
                description = "Classic aerial adventurer with classic cape.",
                priceCoins = 0,
                primaryColor = Color(0xFF00B0FF),
                secondaryColor = GoldAccent,
                particleEffect = ParticleEffectType.DEFAULT_WIND,
                iconEmoji = "🏄"
            ),
            Skin(
                id = "dragon_skin",
                name = "Fire Dragon",
                description = "Fire breathing surfer leaving a scorching flame trail behind.",
                priceCoins = 500,
                priceUsdDisplay = "$2.99",
                primaryColor = OrangeFlame,
                secondaryColor = Color(0xFFFFD54F),
                particleEffect = ParticleEffectType.FLAME_FIRE,
                iconEmoji = "🔥"
            ),
            Skin(
                id = "ice_king",
                name = "Ice King",
                description = "Frozen trail effect with sub-zero ice crystals.",
                priceCoins = 500,
                priceUsdDisplay = "$2.99",
                primaryColor = Color(0xFF00E5FF),
                secondaryColor = Color(0xFFE0F7FA),
                particleEffect = ParticleEffectType.ICE_CRYSTAL,
                iconEmoji = "❄️"
            ),
            Skin(
                id = "star_guardian",
                name = "Star Guardian",
                description = "Glowing celestial star aura for stellar flights.",
                priceCoins = 800,
                priceUsdDisplay = "$2.99",
                primaryColor = Color(0xFFFF4081),
                secondaryColor = GoldAccent,
                particleEffect = ParticleEffectType.STAR_AURA,
                iconEmoji = "🌟"
            ),
            Skin(
                id = "eagle_skin",
                name = "Sky Eagle",
                description = "Feather effects while flying high through storm clouds.",
                priceCoins = 800,
                priceUsdDisplay = "$2.99",
                primaryColor = Color(0xFFFF9800),
                secondaryColor = Color(0xFF795548),
                particleEffect = ParticleEffectType.FEATHER_FLY,
                iconEmoji = "🦅"
            ),
            Skin(
                id = "ghost_skin",
                name = "Phantasm Ghost",
                description = "Translucent ethereal glow drifting through obstacles.",
                priceCoins = 1000,
                priceUsdDisplay = "$2.99",
                primaryColor = Color(0xFFB388FF),
                secondaryColor = TealNeon,
                particleEffect = ParticleEffectType.GHOST_TRANSLUCENT,
                iconEmoji = "👻"
            ),
            Skin(
                id = "thunder_skin",
                name = "Thunder Lord",
                description = "Electrifying lightning bolt sparks with high energy.",
                priceCoins = 1200,
                priceUsdDisplay = "$2.99",
                primaryColor = Color(0xFFFFEB3B),
                secondaryColor = PurpleDeep,
                particleEffect = ParticleEffectType.LIGHTNING_BOLT,
                iconEmoji = "⚡"
            ),
            Skin(
                id = "halloween_skin",
                name = "Pumpkin Jack",
                description = "Glowing pumpkin head with spooky autumn flare.",
                priceCoins = 1000,
                priceUsdDisplay = "$2.99",
                primaryColor = Color(0xFFFF6D00),
                secondaryColor = Color(0xFF388E3C),
                particleEffect = ParticleEffectType.PUMPKIN_GLOW,
                iconEmoji = "🎃"
            ),
            Skin(
                id = "christmas_skin",
                name = "Santa Surfer",
                description = "Festive Santa hat with gentle swirling snowflakes.",
                priceCoins = 1000,
                priceUsdDisplay = "$2.99",
                primaryColor = Color(0xFFD50000),
                secondaryColor = Color(0xFFFFFFFF),
                particleEffect = ParticleEffectType.SNOWFLAKE,
                iconEmoji = "🎄"
            ),
            Skin(
                id = "premium_gold",
                name = "Golden VIP Surfer",
                description = "Exclusive Golden trail effect with royal aura. Unlocked with Premium.",
                priceCoins = 0,
                priceUsdDisplay = "VIP",
                primaryColor = GoldAccent,
                secondaryColor = Color(0xFFFFF59D),
                particleEffect = ParticleEffectType.GOLDEN_TRAIL,
                isPremiumOnly = true,
                iconEmoji = "👑"
            )
        )

        fun getSkinById(id: String): Skin {
            return ALL_SKINS.find { it.id == id } ?: ALL_SKINS.first()
        }
    }
}
