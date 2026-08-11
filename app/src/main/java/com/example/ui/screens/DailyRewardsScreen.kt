package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameUIState
import com.example.model.ScreenRoute
import com.example.ui.components.AdBanner
import com.example.ui.components.GlassCard
import com.example.ui.components.SkyBackgroundCanvas
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TealNeon
import kotlin.random.Random

@Composable
fun DailyRewardsScreen(
    state: GameUIState,
    onNavigate: (ScreenRoute) -> Unit,
    onClaimReward: (Int) -> Unit
) {
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var lastPrizeText by remember { mutableStateOf<String?>(null) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 2500),
        finishedListener = {
            isSpinning = false
            lastPrizeText = "🎉 WON 150 BONUS COINS!"
            onClaimReward(150)
        },
        label = "SpinWheel"
    )

    val rewards7Days = listOf(
        RewardDay("Day 1", "20 Coins", "🪙"),
        RewardDay("Day 2", "30 Coins", "🪙"),
        RewardDay("Day 3", "50 Coins + Shield", "🛡️"),
        RewardDay("Day 4", "80 Coins", "🪙"),
        RewardDay("Day 5", "100 Coins + Magnet", "🧲"),
        RewardDay("Day 6", "150 Coins", "🪙"),
        RewardDay("Day 7", "300 Coins + Free Skin", "🎁")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        SkyBackgroundCanvas(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 40.dp, bottom = 80.dp)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigate(ScreenRoute.START) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "DAILY REWARDS & SPIN",
                    color = GoldAccent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Gift",
                    tint = GoldAccent,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7-Day Streak Calendar
            Text(
                text = "7-DAY LOGIN STREAK",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(rewards7Days) { index, day ->
                    val isClaimed = index < state.dailyRewardStreak
                    val isToday = index == state.dailyRewardStreak

                    GlassCard(
                        modifier = Modifier.width(100.dp),
                        cornerRadius = 16.dp,
                        borderColor = if (isToday) GoldAccent else Color(0x33FFD700)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = day.dayLabel, color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = day.icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = day.rewardText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)

                            if (isToday && state.canClaimDailyReward) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { onClaimReward(50) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(text = "CLAIM", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Interactive Spin Wheel
            Text(
                text = "LUCKY WHEEL OF FORTUNE",
                color = GoldAccent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                // Wheel Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(animatedRotation)
                ) {
                    val colors = listOf(
                        Color(0xFF6C2BD9),
                        GoldAccent,
                        TealNeon,
                        Color(0xFFFF4081),
                        Color(0xFF00B0FF),
                        Color(0xFFFF6D00)
                    )
                    val sweepAngle = 360f / colors.size

                    colors.forEachIndexed { i, color ->
                        drawArc(
                            color = color,
                            startAngle = i * sweepAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            size = Size(size.width, size.height)
                        )
                    }
                }

                // Center Pin Indicator
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎯", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (lastPrizeText != null) {
                Text(
                    text = lastPrizeText!!,
                    color = TealNeon,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (!isSpinning) {
                        isSpinning = true
                        val extraTurns = Random.nextInt(4, 8) * 360f
                        val randomOffset = Random.nextFloat() * 360f
                        rotationAngle += extraTurns + randomOffset
                    }
                },
                enabled = !isSpinning,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (isSpinning) "SPINNING..." else "SPIN WHEEL NOW 🎡",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }
        }

        AdBanner(
            isPremium = state.subscriptionTier != "FREE",
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private data class RewardDay(
    val dayLabel: String,
    val rewardText: String,
    val icon: String
)
