package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameUIState
import com.example.model.Obstacle
import com.example.model.ScreenRoute
import com.example.model.VisualParticle
import com.example.ui.components.GlassCard
import com.example.ui.components.SkyBackgroundCanvas
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TealNeon
import com.example.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    state: GameUIState,
    onNavigate: (ScreenRoute) -> Unit
) {
    var isPaused by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "SurferBounce")
    val capeWiggle by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CapeWiggle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val screenWidth = size.width
                    if (offset.x < screenWidth / 2f) {
                        viewModel.moveLaneLeft()
                    } else {
                        viewModel.moveLaneRight()
                    }
                }
            }
    ) {
        // 1. Sky Canvas & Runway Background
        SkyBackgroundCanvas(
            modifier = Modifier.fillMaxSize(),
            isGameplay = true,
            speedMultiplier = if (state.hasSpeedBoostActive) 1.8f else 1.0f
        )

        // 2. Active Gameplay Canvas (Obstacles, Coins, Powerups, Player, Particles)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Render Obstacles
            for (obs in viewModel.obstacles) {
                val obsX = when (obs.lane) {
                    0 -> w * 0.22f
                    1 -> w * 0.50f
                    else -> w * 0.78f
                }
                val obsY = h * obs.yPos

                drawContext.canvas.nativeCanvas.drawText(
                    obs.type.iconEmoji,
                    obsX - 30f,
                    obsY + 20f,
                    android.graphics.Paint().apply {
                        textSize = 100f
                    }
                )
            }

            // Render Coins
            for (coin in viewModel.coins) {
                val cx = w * coin.currentXNormalized
                val cy = h * coin.yPos
                drawCircle(
                    color = GoldAccent,
                    radius = 22f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color.White,
                    radius = 12f,
                    center = Offset(cx, cy)
                )
            }

            // Render Power-Ups
            for (pu in viewModel.powerUps) {
                val px = when (pu.lane) {
                    0 -> w * 0.22f
                    1 -> w * 0.50f
                    else -> w * 0.78f
                }
                val py = h * pu.yPos
                drawContext.canvas.nativeCanvas.drawText(
                    pu.type.iconEmoji,
                    px - 28f,
                    py + 20f,
                    android.graphics.Paint().apply {
                        textSize = 90f
                    }
                )
            }

            // Render Particles
            for (p in viewModel.particles) {
                drawCircle(
                    color = Color(p.colorHex).copy(alpha = p.alpha),
                    radius = p.size,
                    center = Offset(w * p.x, h * p.y)
                )
            }

            // Render Player Surfer & Active Power-up Auras
            val playerX = w * viewModel.playerState.currentX
            val playerY = h * viewModel.playerState.yPosNormalized

            // Shield Aura Bubble
            if (viewModel.playerState.isShieldActive) {
                drawCircle(
                    color = TealNeon.copy(alpha = 0.35f),
                    radius = 70f,
                    center = Offset(playerX, playerY - 10f)
                )
                drawCircle(
                    color = TealNeon,
                    radius = 70f,
                    center = Offset(playerX, playerY - 10f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                )
            }

            // Magnet Attraction Ring
            if (viewModel.playerState.isMagnetActive) {
                drawCircle(
                    color = GoldAccent.copy(alpha = 0.25f),
                    radius = 110f,
                    center = Offset(playerX, playerY - 10f)
                )
            }

            // Draw Player Emoji & Cape
            drawContext.canvas.nativeCanvas.drawText(
                state.activeSkin.iconEmoji,
                playerX - 36f,
                playerY + 20f,
                android.graphics.Paint().apply {
                    textSize = 120f
                }
            )
        }

        // 3. Top HUD Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score & Coins Bar
            GlassCard(
                cornerRadius = 16.dp,
                backgroundColor = Color(0x331E1638)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = "SCORE",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.score}",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Column {
                        Text(
                            text = "COINS",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${state.coinsCollectedThisRun}",
                                color = GoldAccent,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Pause Button
            IconButton(
                onClick = { isPaused = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x331E1638))
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color.White
                )
            }
        }

        // Active Power-Up Banner Countdown
        if (state.activePowerUp != null) {
            GlassCard(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 110.dp)
                    .width(220.dp),
                cornerRadius = 14.dp
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.activePowerUp.iconEmoji} ${state.activePowerUp.title}",
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { state.powerUpTimeRemainingRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = TealNeon,
                        trackColor = Color(0x33FFFFFF)
                    )
                }
            }
        }

        // On-Screen Lane Tap Guides (Bottom)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x22FFFFFF))
                    .clickable { viewModel.moveLaneLeft() }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "◀ TAP LEFT",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x22FFFFFF))
                    .clickable { viewModel.moveLaneRight() }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "TAP RIGHT ▶",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // Pause Menu Dialog
        if (isPaused) {
            Dialog(onDismissRequest = { isPaused = false }) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    cornerRadius = 24.dp,
                    borderColor = GoldAccent
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "GAME PAUSED",
                            color = GoldAccent,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { isPaused = false },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "RESUME GAME",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                isPaused = false
                                viewModel.startNewGame()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealNeon),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "RESTART RUN",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                isPaused = false
                                onNavigate(ScreenRoute.START)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C2BD9)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "QUIT TO MENU",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
