package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import kotlinx.coroutines.delay

@Composable
fun AdBanner(
    isPremium: Boolean,
    modifier: Modifier = Modifier,
    onAdClick: () -> Unit = {}
) {
    if (isPremium) return // Ads hidden permanently for Premium users!

    var adIndex by remember { mutableIntStateOf(0) }
    val ads = listOf(
        AdContent("🚀 Sky Surfer Pro", "Unlock Golden Trail & Unlimited Continues!", "INSTALL"),
        AdContent("🎮 Pixel Quest 3D", "The #1 Retro RPG Game of the Year", "PLAY NOW"),
        AdContent("🛡️ Cyber Shield VPN", "Fast & Secure Cloud Protection", "GET 80% OFF"),
        AdContent("⚡ Energy Boost Drink", "Fuel your high score runs!", "SHOP NOW")
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(15000L) // Auto-refresh ad every 15 seconds
            adIndex = (adIndex + 1) % ads.size
        }
    }

    val currentAd = ads[adIndex]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF161226))
            .border(1.dp, Color(0x33FFD700), RoundedCornerShape(12.dp))
            .clickable { onAdClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // "Ad" badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(GoldAccent)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Ad",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentAd.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                        Text(
                            text = " • ${currentAd.subtitle}",
                            color = Color(0xFFA594C9),
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Action button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF6C2BD9))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = currentAd.buttonText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private data class AdContent(
    val title: String,
    val subtitle: String,
    val buttonText: String
)
