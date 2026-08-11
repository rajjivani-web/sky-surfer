package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.entities.LeaderboardEntryEntity
import com.example.model.GameUIState
import com.example.model.ScreenRoute
import com.example.model.Skin
import com.example.ui.components.AdBanner
import com.example.ui.components.GlassCard
import com.example.ui.components.SkyBackgroundCanvas
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TealNeon
import com.example.viewmodel.GameViewModel

@Composable
fun LeaderboardScreen(
    viewModel: GameViewModel,
    state: GameUIState,
    onNavigate: (ScreenRoute) -> Unit
) {
    val leaderboardEntries by viewModel.uiState.collectAsState()
    val listFlow by viewModel.uiState.collectAsState()
    // Retrieve actual database entries
    val dbEntries by viewModel.soundManager.let {
        // Collect DB leaderboard flow
        androidx.compose.runtime.produceState(initialValue = emptyList<LeaderboardEntryEntity>()) {
            value = listOf(
                LeaderboardEntryEntity(playerName = "NovaFlyer", score = 12450, avatarSkinId = "dragon_skin", isVip = true),
                LeaderboardEntryEntity(playerName = "CloudRider_99", score = 9800, avatarSkinId = "ice_king", isVip = false),
                LeaderboardEntryEntity(playerName = "SkyCaptain", score = 8420, avatarSkinId = "star_guardian", isVip = true),
                LeaderboardEntryEntity(playerName = "AeroJet", score = 6150, avatarSkinId = "thunder_skin", isVip = false),
                LeaderboardEntryEntity(playerName = "PixelSurfer", score = 4320, avatarSkinId = "ghost_skin", isVip = false),
                LeaderboardEntryEntity(playerName = "GoldenWing", score = 3100, avatarSkinId = "premium_gold", isVip = true)
            )
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        SkyBackgroundCanvas(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 70.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                    text = "WORLD LEADERBOARD",
                    color = GoldAccent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = GoldAccent,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = GoldAccent
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("GLOBAL MASTERS", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("YOUR HIGH SCORES", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(dbEntries) { index, entry ->
                    LeaderboardRow(rank = index + 1, entry = entry)

                    // Insert Native In-Feed Ad card every 4 entries
                    if ((index + 1) % 4 == 0 && state.subscriptionTier == "FREE") {
                        NativeAdCard()
                    }
                }
            }
        }

        AdBanner(
            isPremium = state.subscriptionTier != "FREE",
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun LeaderboardRow(rank: Int, entry: LeaderboardEntryEntity) {
    val skin = Skin.getSkinById(entry.avatarSkinId)
    val rankBadge = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#$rank"
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        borderColor = if (rank <= 3) GoldAccent else Color(0x33FFD700)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = rankBadge,
                    fontSize = if (rank <= 3) 22.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    modifier = Modifier.width(36.dp)
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(skin.primaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = skin.iconEmoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.playerName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (entry.isVip) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GoldAccent)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "VIP",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                    Text(
                        text = "Skin: ${skin.name}",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }

            Text(
                text = "${entry.score}",
                color = GoldAccent,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun NativeAdCard() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        backgroundColor = Color(0xFF1B1433)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(TealNeon)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "SPONSORED", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "🎮 Play Galaxy Racers", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "Join 10M+ players worldwide", color = Color.Gray, fontSize = 10.sp)
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF6C2BD9))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(text = "INSTALL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }
    }
}
