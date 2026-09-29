package com.minimal.snake.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimal.snake.ui.theme.LocalSnakeColors
import com.minimal.snake.ui.theme.Mali

@Composable
fun GameTopBar(
    score: Int,
    highScore: Int,
    isPaused: Boolean,
    bonusSeconds: Int,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snakeColors = LocalSnakeColors.current
    val pauseInteractionSource = remember { MutableInteractionSource() }
    val isPausePressed by pauseInteractionSource.collectIsPressedAsState()
    val pauseScale by animateFloatAsState(targetValue = if (isPausePressed) 0.94f else 1f, label = "pause_scale")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Horizontal Row for Current Score and Best Score
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(snakeColors.surfaceCard)
                .border(1.dp, snakeColors.surfaceCardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Score
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Score",
                    fontFamily = Mali,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = snakeColors.textSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = score.toString(),
                    fontFamily = Mali,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = snakeColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Dot Separator
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(snakeColors.surfaceCardBorder)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Best Score
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Best",
                    fontFamily = Mali,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = snakeColors.textSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = highScore.toString(),
                    fontFamily = Mali,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = snakeColors.activeSkin.primaryColor
                )
            }
        }

        // Right side: Bonus Timer + Text-based Pause/Resume Button
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(
                visible = bonusSeconds > 0,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFD700).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "★ ${bonusSeconds}s",
                        fontFamily = Mali,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFFFD700)
                    )
                }
            }

            // Text-based Pause / Resume Button
            Box(
                modifier = Modifier
                    .scale(pauseScale)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isPaused) snakeColors.activeSkin.primaryColor else snakeColors.surfaceCard)
                    .border(
                        width = 1.dp,
                        color = if (isPaused) snakeColors.activeSkin.primaryColor else snakeColors.surfaceCardBorder,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable(
                        interactionSource = pauseInteractionSource,
                        indication = null,
                        onClick = onPauseClick
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPaused) "Resume" else "Pause",
                    fontFamily = Mali,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isPaused) Color.White else snakeColors.textPrimary
                )
            }
        }
    }
}
