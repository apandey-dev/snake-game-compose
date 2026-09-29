package com.minimal.snake.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimal.snake.ui.theme.LocalSnakeColors
import com.minimal.snake.ui.theme.Mali

@Composable
fun GameOverBottomSheet(
    visible: Boolean,
    score: Int,
    highScore: Int,
    isNewHighScore: Boolean,
    foodEaten: Int,
    onPlayAgain: () -> Unit,
    onGoHome: () -> Unit
) {
    val snakeColors = LocalSnakeColors.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(snakeColors.surfaceCard)
                    .border(
                        width = 1.dp,
                        color = snakeColors.surfaceCardBorder,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Minimal Drag Handle
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(snakeColors.surfaceCardBorder)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isNewHighScore) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(snakeColors.activeSkin.primaryColor.copy(alpha = 0.15f))
                            .border(1.dp, snakeColors.activeSkin.primaryColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.EmojiEvents,
                                contentDescription = null,
                                tint = snakeColors.activeSkin.primaryColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NEW HIGH SCORE!",
                                fontFamily = Mali,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = snakeColors.activeSkin.primaryColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = if (isNewHighScore) "Brilliant Run!" else "Game Over",
                    fontFamily = Mali,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = snakeColors.textPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Score Overview Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (snakeColors.isAmoled) Color(0xFF07080A) else Color(0xFFF1F3F7))
                        .border(1.dp, snakeColors.surfaceCardBorder, RoundedCornerShape(16.dp))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SCORE",
                            fontFamily = Mali,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = snakeColors.textSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = score.toString(),
                            fontFamily = Mali,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = snakeColors.textPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(snakeColors.surfaceCardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "BEST",
                            fontFamily = Mali,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = snakeColors.textSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = highScore.toString(),
                            fontFamily = Mali,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = snakeColors.activeSkin.primaryColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(snakeColors.surfaceCardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "APPLES",
                            fontFamily = Mali,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = snakeColors.textSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = foodEaten.toString(),
                            fontFamily = Mali,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = snakeColors.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        MinimalOutlineButton(
                            text = "Home",
                            icon = Icons.Rounded.Home,
                            onClick = onGoHome
                        )
                    }
                    Box(modifier = Modifier.weight(1.3f)) {
                        MinimalPrimaryButton(
                            text = "Restart",
                            icon = Icons.Rounded.Replay,
                            onClick = onPlayAgain
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PauseBottomSheet(
    visible: Boolean,
    score: Int,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onGoHome: () -> Unit
) {
    val snakeColors = LocalSnakeColors.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(180)),
        exit = fadeOut(tween(180))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onResume),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(snakeColors.surfaceCard)
                    .border(
                        width = 1.dp,
                        color = snakeColors.surfaceCardBorder,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Minimal Drag Handle
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(snakeColors.surfaceCardBorder)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Game Paused",
                    fontFamily = Mali,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = snakeColors.textPrimary
                )

                Text(
                    text = "Current Score : $score",
                    fontFamily = Mali,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = snakeColors.textSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Resume Primary Button
                MinimalPrimaryButton(
                    text = "Resume",
                    icon = Icons.Rounded.PlayArrow,
                    onClick = onResume
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        MinimalOutlineButton(
                            text = "Home",
                            icon = Icons.Rounded.Home,
                            onClick = onGoHome
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MinimalOutlineButton(
                            text = "Restart",
                            icon = Icons.Rounded.Replay,
                            onClick = onRestart
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResetConfirmationBottomSheet(
    visible: Boolean,
    title: String = "Reset Statistics",
    message: String = "Are you sure you want to reset your high score and game stats?",
    confirmText: String = "Reset",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val snakeColors = LocalSnakeColors.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(180)),
        exit = fadeOut(tween(180))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(snakeColors.surfaceCard)
                    .border(
                        width = 1.dp,
                        color = snakeColors.surfaceCardBorder,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Minimal Drag Handle
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(snakeColors.surfaceCardBorder)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    fontFamily = Mali,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = snakeColors.textPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = message,
                    fontFamily = Mali,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = snakeColors.textSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        MinimalOutlineButton(
                            text = dismissText,
                            onClick = onDismiss
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MinimalPrimaryButton(
                            text = confirmText,
                            onClick = onConfirm
                        )
                    }
                }
            }
        }
    }
}
