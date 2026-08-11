package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.GameUIState
import com.example.model.RewardedAdType
import com.example.model.ScreenRoute
import com.example.model.Skin
import com.example.ui.components.AdBanner
import com.example.ui.components.GlassCard
import com.example.ui.components.SkyBackgroundCanvas
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PurpleCard
import com.example.ui.theme.TealNeon

@Composable
fun ShopScreen(
    state: GameUIState,
    onNavigate: (ScreenRoute) -> Unit,
    onSelectSkin: (String) -> Unit,
    onPurchaseSkinCoins: (Skin) -> Unit,
    onOpenBilling: (String, String) -> Unit,
    onTriggerRewardedAd: (RewardedAdType) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Character Skins", "Power-Ups", "Subscriptions", "Coins Store")

    Box(modifier = Modifier.fillMaxSize()) {
        SkyBackgroundCanvas(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 70.dp)
        ) {
            // Header Bar
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
                    text = "SURFER SHOP & SKINS",
                    color = GoldAccent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                // Coins Chip
                GlassCard(cornerRadius = 14.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.totalCoins}",
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tabs Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = GoldAccent,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) GoldAccent else Color.Gray,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // 1. Skin Bundle Offer Banner
                        item {
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenBilling("ALL 8 SKINS BUNDLE", "$14.99") },
                                borderColor = GoldAccent,
                                cornerRadius = 20.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "🔥 MEGA SKIN BUNDLE (ALL 8 SKINS)",
                                            color = GoldAccent,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Save 37% + Rainbow Trail Effect + 'Legendary' Title",
                                            color = Color.LightGray,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(GoldAccent)
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "$14.99",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Watch Ad for 24H Skin
                        item {
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTriggerRewardedAd(RewardedAdType.TEMP_SKIN_24H) },
                                cornerRadius = 16.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🎬", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "TRY RANDOM SKIN FOR 24 HOURS",
                                                color = TealNeon,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Watch 1 short rewarded video ad",
                                                color = Color.Gray,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Text(
                                        text = "WATCH",
                                        color = GoldAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // Skins List
                        items(Skin.ALL_SKINS) { skin ->
                            SkinCardItem(
                                skin = skin,
                                isOwned = state.unlockedSkinIds.contains(skin.id),
                                isSelected = state.selectedSkinId == skin.id,
                                totalCoins = state.totalCoins,
                                onSelect = { onSelectSkin(skin.id) },
                                onBuyCoins = { onPurchaseSkinCoins(skin) },
                                onBuyIap = { onOpenBilling(skin.name, skin.priceUsdDisplay) }
                            )
                        }
                    }

                    1 -> {
                        // Power-Up Packs
                        item { PowerUpPackCard("Shield Protection Pack", "Permanent +1 Extra Life per game", "$2.99") { onOpenBilling("Shield Protection Pack", "$2.99") } }
                        item { PowerUpPackCard("Magnet Attraction Pack", "Auto-collects coins in wide radius", "$3.99") { onOpenBilling("Magnet Attraction Pack", "$3.99") } }
                        item { PowerUpPackCard("Hyper Speed Boost Pack", "x1.5 score multiplier & 20% speed", "$2.99") { onOpenBilling("Speed Boost Pack", "$2.99") } }
                    }

                    2 -> {
                        // Subscriptions
                        item { SubscriptionCard("PREMIUM ONE-TIME", "$4.99", "No Ads • Unlimited Continues • Golden Trail", true) { onOpenBilling("PREMIUM", "$4.99") } }
                        item { SubscriptionCard("MONTHLY VIP", "$2.99 / mo", "No Ads • Double Coins • VIP Badge", false) { onOpenBilling("MONTHLY VIP", "$2.99/mo") } }
                        item { SubscriptionCard("YEARLY VIP", "$24.99 / yr", "Save 30% • 3 Free Skins • 1000 Coins", false) { onOpenBilling("YEARLY VIP", "$24.99/yr") } }
                        item { SubscriptionCard("LIFETIME VIP", "$29.99", "All Skins & Power-Ups Unlocked Forever", true) { onOpenBilling("LIFETIME VIP", "$29.99") } }
                    }

                    3 -> {
                        // Coins Store
                        item { CoinPackCard("100 Coins", "🪙 Small Pack", "$0.99") { onOpenBilling("100 Coins", "$0.99") } }
                        item { CoinPackCard("500 Coins", "🪙 Medium Pack", "$3.99") { onOpenBilling("500 Coins", "$3.99") } }
                        item { CoinPackCard("1500 Coins", "🪙 Large Pack", "$7.99") { onOpenBilling("1500 Coins", "$7.99") } }
                        item { CoinPackCard("5000 Coins", "🪙 Mega Pack", "$19.99") { onOpenBilling("5000 Coins", "$19.99") } }
                    }
                }
            }
        }

        // Ad Banner Footer
        AdBanner(
            isPremium = state.subscriptionTier != "FREE",
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun SkinCardItem(
    skin: Skin,
    isOwned: Boolean,
    isSelected: Boolean,
    totalCoins: Int,
    onSelect: () -> Unit,
    onBuyCoins: () -> Unit,
    onBuyIap: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        borderColor = if (isSelected) GoldAccent else Color(0x33FFD700)
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
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(skin.primaryColor)
                        .border(2.dp, skin.secondaryColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = skin.iconEmoji, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = skin.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (skin.isPremiumOnly) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "👑", fontSize = 12.sp)
                        }
                    }
                    Text(
                        text = skin.description,
                        color = Color.Gray,
                        fontSize = 11.sp,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldAccent)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "EQUIPPED",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            } else if (isOwned) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = TealNeon),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "EQUIP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            } else if (skin.priceCoins > 0 && totalCoins >= skin.priceCoins) {
                Button(
                    onClick = onBuyCoins,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "🪙 ${skin.priceCoins}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            } else {
                Button(
                    onClick = onBuyIap,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C2BD9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = skin.priceUsdDisplay.ifEmpty { "$2.99" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun PowerUpPackCard(title: String, desc: String, price: String, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = desc, color = Color.LightGray, fontSize = 12.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(GoldAccent)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = price, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SubscriptionCard(title: String, price: String, benefits: String, isRecommended: Boolean, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 18.dp,
        borderColor = if (isRecommended) GoldAccent else Color(0x33FFD700)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, color = GoldAccent, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    if (isRecommended) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TealNeon)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(text = "BEST VALUE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        }
                    }
                }
                Text(text = benefits, color = Color.LightGray, fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(GoldAccent)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = price, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun CoinPackCard(amount: String, title: String, price: String, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🪙", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "+$amount Instant Coins", color = GoldAccent, fontSize = 12.sp)
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(GoldAccent)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = price, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
