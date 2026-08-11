package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.TealNeon
import kotlinx.coroutines.delay

@Composable
fun SkyBackgroundCanvas(
    modifier: Modifier = Modifier,
    isGameplay: Boolean = false,
    speedMultiplier: Float = 1.0f
) {
    var cloudOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isGameplay) {
        while (true) {
            delay(16L)
            cloudOffset += 0.5f * speedMultiplier
            if (cloudOffset > 2000f) cloudOffset = 0f
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Sky Sunset/Sunrise Gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF281344), // Midnight Purple Top
                    Color(0xFF6C2BD9), // Deep Purple Mid
                    Color(0xFFFF4081), // Sunset Pink
                    Color(0xFFFFD700)  // Gold Horizon
                )
            ),
            size = size
        )

        // 2. Glowing Sun on Horizon
        val sunCenter = Offset(w * 0.5f, h * 0.38f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GoldAccent,
                    GoldAccent.copy(alpha = 0.6f),
                    Color.Transparent
                ),
                center = sunCenter,
                radius = 180f
            ),
            radius = 180f,
            center = sunCenter
        )
        drawCircle(
            color = Color.White,
            radius = 60f,
            center = sunCenter
        )

        // 3. Floating Clouds (Parallax)
        drawParallaxClouds(w, h, cloudOffset)

        if (isGameplay) {
            // 4. Floating Sky Runway Tracks
            drawSkyPlatforms(w, h)
        }
    }
}

private fun DrawScope.drawParallaxClouds(w: Float, h: Float, offset: Float) {
    val cloudColor = Color.White.copy(alpha = 0.25f)
    val horizonY = h * 0.40f

    // Layer 1
    val c1X = ((100f + offset) % (w + 300f)) - 150f
    drawCircle(cloudColor, radius = 50f, center = Offset(c1X, horizonY - 40f))
    drawCircle(cloudColor, radius = 70f, center = Offset(c1X + 40f, horizonY - 50f))
    drawCircle(cloudColor, radius = 45f, center = Offset(c1X + 80f, horizonY - 40f))

    // Layer 2
    val c2X = ((600f + offset * 1.5f) % (w + 400f)) - 200f
    drawCircle(cloudColor, radius = 60f, center = Offset(c2X, horizonY - 80f))
    drawCircle(cloudColor, radius = 80f, center = Offset(c2X + 50f, horizonY - 95f))
    drawCircle(cloudColor, radius = 55f, center = Offset(c2X + 110f, horizonY - 80f))
}

private fun DrawScope.drawSkyPlatforms(w: Float, h: Float) {
    val topY = h * 0.42f
    val botY = h * 1.0f

    val topW = w * 0.35f
    val botW = w * 0.90f

    val topX1 = (w - topW) / 2f
    val topX2 = topX1 + topW
    val botX1 = (w - botW) / 2f
    val botX2 = botX1 + botW

    // Main Runway Platform
    val path = Path().apply {
        moveTo(topX1, topY)
        lineTo(topX2, topY)
        lineTo(botX2, botY)
        lineTo(botX1, botY)
        close()
    }

    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            colors = listOf(
                PurpleDeep.copy(alpha = 0.6f),
                Color(0xFF1E1638).copy(alpha = 0.95f)
            ),
            startY = topY,
            endY = botY
        )
    )

    // Lane Dividers (3 Lanes: Left, Center, Right)
    val topStep = topW / 3f
    val botStep = botW / 3f

    for (i in 1..2) {
        val tx = topX1 + topStep * i
        val bx = botX1 + botStep * i
        drawLine(
            color = GoldAccent.copy(alpha = 0.7f),
            start = Offset(tx, topY),
            end = Offset(bx, botY),
            strokeWidth = 3f
        )
    }

    // Outer Neon Borders
    drawLine(
        color = TealNeon,
        start = Offset(topX1, topY),
        end = Offset(botX1, botY),
        strokeWidth = 6f
    )
    drawLine(
        color = TealNeon,
        start = Offset(topX2, topY),
        end = Offset(botX2, botY),
        strokeWidth = 6f
    )
}
