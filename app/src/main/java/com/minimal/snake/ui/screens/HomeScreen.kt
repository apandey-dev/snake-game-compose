package com.minimal.snake.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimal.snake.data.GameDifficulty
import com.minimal.snake.data.GameSettings
import com.minimal.snake.data.GameStats
import com.minimal.snake.data.ThemeMode
import com.minimal.snake.ui.components.HomeBottomSheet
import com.minimal.snake.ui.theme.LocalSnakeColors
import com.minimal.snake.ui.theme.Mali

@Composable
fun HomeScreen(
    settings: GameSettings,
    stats: GameStats,
    onStartGame: () -> Unit,
    onDifficultySelected: (GameDifficulty) -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onSkinSelected: (String) -> Unit,
    onWallWrapToggled: (Boolean) -> Unit,
    onHapticsToggled: (Boolean) -> Unit,
    onSoundToggled: (Boolean) -> Unit,
    onResetStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snakeColors = LocalSnakeColors.current

    val infiniteTransition = rememberInfiniteTransition(label = "home_anim")
    val snakeMoveProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "snake_move"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (snakeColors.isAmoled) Color.Black else Color(0xFFF7F8FA))
    ) {
        // Main Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 260.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                Text(
                    text = "SNAKE",
                    fontFamily = Mali,
                    fontWeight = FontWeight.Bold,
                    fontSize = 44.sp,
                    color = snakeColors.textPrimary,
                    letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Best Score Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(snakeColors.surfaceCard)
                        .border(1.dp, snakeColors.surfaceCardBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = snakeColors.activeSkin.primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BEST : ${stats.highScore}",
                        fontFamily = Mali,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = snakeColors.textPrimary
                    )
                }
            }

            // Minimalist Decorative Animated Dot Matrix Canvas
            Box(
                modifier = Modifier
                    .size(240.dp, 160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(snakeColors.surfaceCard.copy(alpha = 0.4f))
                    .border(1.dp, snakeColors.surfaceCardBorder.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val dotRadius = 3f
                    val dotSpacing = 24f
                    val cols = (size.width / dotSpacing).toInt()
                    val rows = (size.height / dotSpacing).toInt()
                    val offsetX = (size.width - (cols * dotSpacing)) / 2f
                    val offsetY = (size.height - (rows * dotSpacing)) / 2f

                    // Draw subtle grid dots
                    for (c in 0..cols) {
                        for (r in 0..rows) {
                            drawCircle(
                                color = snakeColors.gridDot,
                                radius = dotRadius,
                                center = Offset(offsetX + c * dotSpacing, offsetY + r * dotSpacing)
                            )
                        }
                    }

                    // Draw cute animated mini snake
                    val snakeRow = rows / 2
                    val step = snakeMoveProgress.toInt() % cols
                    for (i in 0..3) {
                        val segCol = (step - i + cols) % cols
                        val cx = offsetX + segCol * dotSpacing
                        val cy = offsetY + snakeRow * dotSpacing
                        val radius = if (i == 0) 10f else 8.5f

                        drawCircle(
                            color = snakeColors.activeSkin.primaryColor,
                            radius = radius,
                            center = Offset(cx, cy)
                        )

                        // Eyes on head
                        if (i == 0) {
                            val eyeColor = if (snakeColors.isAmoled) Color(0xFF07080A) else Color.White
                            drawCircle(
                                color = eyeColor,
                                radius = 2f,
                                center = Offset(cx + 4f, cy - 3f)
                            )
                            drawCircle(
                                color = eyeColor,
                                radius = 2f,
                                center = Offset(cx + 4f, cy + 3f)
                            )
                        }
                    }

                    // Food dot
                    val foodCol = (step + 3) % cols
                    val foodCx = offsetX + foodCol * dotSpacing
                    val foodCy = offsetY + snakeRow * dotSpacing
                    drawCircle(
                        color = snakeColors.activeSkin.primaryColor,
                        radius = 8.5f,
                        center = Offset(foodCx, foodCy)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Bottom Sheet with Play, Theme, Score tabs
        HomeBottomSheet(
            settings = settings,
            stats = stats,
            onStartGame = onStartGame,
            onDifficultySelected = onDifficultySelected,
            onThemeModeSelected = onThemeModeSelected,
            onSkinSelected = onSkinSelected,
            onWallWrapToggled = onWallWrapToggled,
            onHapticsToggled = onHapticsToggled,
            onSoundToggled = onSoundToggled,
            onResetStats = onResetStats,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
