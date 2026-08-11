package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ScreenRoute
import com.example.ui.components.GooglePlayBillingModal
import com.example.ui.components.InterstitialAdModal
import com.example.ui.components.RewardedAdModal
import com.example.ui.screens.DailyRewardsScreen
import com.example.ui.screens.GameOverScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.StartScreen
import com.example.viewmodel.GameViewModel

@Composable
fun SkySurferApp(
    viewModel: GameViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (state.currentRoute) {
            ScreenRoute.START -> {
                StartScreen(
                    state = state,
                    onNavigate = { viewModel.navigateTo(it) },
                    onOpenBilling = { name, price -> viewModel.openBillingModal(name, price) }
                )
            }
            ScreenRoute.GAME -> {
                GameScreen(
                    viewModel = viewModel,
                    state = state,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
            ScreenRoute.GAME_OVER -> {
                GameOverScreen(
                    state = state,
                    onReplay = { viewModel.startNewGame() },
                    onNavigate = { viewModel.navigateTo(it) },
                    onTriggerRewardedAd = { viewModel.triggerRewardedAd(it) },
                    onContinueFree = { viewModel.continueGameFromRewardedAd() }
                )
            }
            ScreenRoute.SHOP -> {
                ShopScreen(
                    state = state,
                    onNavigate = { viewModel.navigateTo(it) },
                    onSelectSkin = { viewModel.selectSkin(it) },
                    onPurchaseSkinCoins = { viewModel.purchaseSkinWithCoins(it) },
                    onOpenBilling = { name, price -> viewModel.openBillingModal(name, price) },
                    onTriggerRewardedAd = { viewModel.triggerRewardedAd(it) }
                )
            }
            ScreenRoute.LEADERBOARD -> {
                LeaderboardScreen(
                    viewModel = viewModel,
                    state = state,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
            ScreenRoute.DAILY_REWARDS -> {
                DailyRewardsScreen(
                    state = state,
                    onNavigate = { viewModel.navigateTo(it) },
                    onClaimReward = { viewModel.claimDailyReward(it) }
                )
            }
            ScreenRoute.SETTINGS -> {
                SettingsScreen(
                    state = state,
                    onNavigate = { viewModel.navigateTo(it) },
                    onToggleSound = { viewModel.toggleSound(it) },
                    onToggleHaptics = { viewModel.toggleHaptics(it) },
                    onSetDifficulty = { viewModel.setDifficulty(it) }
                )
            }
            else -> {
                StartScreen(
                    state = state,
                    onNavigate = { viewModel.navigateTo(it) },
                    onOpenBilling = { name, price -> viewModel.openBillingModal(name, price) }
                )
            }
        }

        // Interstitial Ad Modal Dialog
        if (state.isInterstitialAdShowing) {
            InterstitialAdModal(
                onDismiss = { viewModel.dismissInterstitialAd() }
            )
        }

        // Rewarded Video Ad Modal Dialog
        if (state.isRewardedAdShowing && state.rewardedAdType != null) {
            RewardedAdModal(
                type = state.rewardedAdType!!,
                onRewardEarned = { viewModel.onRewardedAdCompleted() },
                onDismiss = { viewModel.dismissRewardedAd() }
            )
        }

        // Google Play Billing In-App Purchase Modal Dialog
        if (state.isBillingModalShowing) {
            GooglePlayBillingModal(
                itemName = state.selectedBillingItemName,
                itemPrice = state.selectedBillingItemPrice,
                onConfirmPurchase = { viewModel.completePurchase(state.selectedBillingItemName) },
                onDismiss = { viewModel.dismissBillingModal() }
            )
        }
    }
}
