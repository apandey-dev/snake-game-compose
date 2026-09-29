package com.minimal.snake.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import com.minimal.snake.data.Direction
import com.minimal.snake.data.GridPoint
import com.minimal.snake.game.GameState
import com.minimal.snake.ui.theme.LocalSnakeColors
import kotlin.math.abs

@Composable
fun GameBoardCanvas(
    gameState: GameState,
    onDirectionChange: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    val snakeColors = LocalSnakeColors.current
    val primaryColor = snakeColors.activeSkin.primaryColor
    val glowColor = snakeColors.activeSkin.glowColor
    val dotColor = snakeColors.gridDot
    val eyeColor = if (snakeColors.isAmoled) Color(0xFF0D0E12) else Color.White

    // Food pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val foodPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "food_pulse"
    )
    val bonusPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bonus_pulse"
    )

    // Responsive swipe gesture detection
    var totalDragX by remember { mutableFloatStateOf(0f) }
    var totalDragY by remember { mutableFloatStateOf(0f) }
    var swipeTriggeredInGesture by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(gameState.isGameOver, gameState.isPaused) {
                if (gameState.isGameOver || gameState.isPaused) return@pointerInput

                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        totalDragY = 0f
                        swipeTriggeredInGesture = false
                    },
                    onDragEnd = {
                        totalDragX = 0f
                        totalDragY = 0f
                        swipeTriggeredInGesture = false
                    },
                    onDragCancel = {
                        totalDragX = 0f
                        totalDragY = 0f
                        swipeTriggeredInGesture = false
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y

                        val threshold = 35f // Crisp swipe sensitivity

                        if (!swipeTriggeredInGesture || (abs(totalDragX) > threshold * 2 || abs(totalDragY) > threshold * 2)) {
                            if (abs(totalDragX) > abs(totalDragY)) {
                                if (abs(totalDragX) > threshold) {
                                    if (totalDragX > 0) {
                                        onDirectionChange(Direction.RIGHT)
                                    } else {
                                        onDirectionChange(Direction.LEFT)
                                    }
                                    swipeTriggeredInGesture = true
                                    totalDragX = 0f
                                    totalDragY = 0f
                                }
                            } else {
                                if (abs(totalDragY) > threshold) {
                                    if (totalDragY > 0) {
                                        onDirectionChange(Direction.DOWN)
                                    } else {
                                        onDirectionChange(Direction.UP)
                                    }
                                    swipeTriggeredInGesture = true
                                    totalDragX = 0f
                                    totalDragY = 0f
                                }
                            }
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cols = gameState.gridCols
            val rows = gameState.gridRows

            val cellWidth = size.width / cols
            val cellHeight = size.height / rows
            val cellSize = minOf(cellWidth, cellHeight)

            // Centering grid in canvas
            val startOffsetX = (size.width - (cols * cellSize)) / 2f
            val startOffsetY = (size.height - (rows * cellSize)) / 2f

            // 1. Draw minimal dotted grid pattern (matching reference img.jpg)
            val gridDotRadius = cellSize * 0.055f
            for (col in 0 until cols) {
                for (row in 0 until rows) {
                    val cx = startOffsetX + (col + 0.5f) * cellSize
                    val cy = startOffsetY + (row + 0.5f) * cellSize
                    drawCircle(
                        color = dotColor,
                        radius = gridDotRadius,
                        center = Offset(cx, cy)
                    )
                }
            }

            // 2. Draw regular food (circular glowing dot matching img.jpg)
            val food = gameState.food
            val foodCx = startOffsetX + (food.x + 0.5f) * cellSize
            val foodCy = startOffsetY + (food.y + 0.5f) * cellSize
            val baseFoodRadius = cellSize * 0.44f
            val animatedFoodRadius = baseFoodRadius * foodPulseScale

            // Subtle ambient food glow
            drawCircle(
                color = glowColor,
                radius = animatedFoodRadius * 1.25f,
                center = Offset(foodCx, foodCy)
            )
            // Food solid core
            drawCircle(
                color = primaryColor,
                radius = animatedFoodRadius,
                center = Offset(foodCx, foodCy)
            )

            // 3. Draw bonus food (if active)
            gameState.bonusFood?.let { bonus ->
                val bonusCx = startOffsetX + (bonus.x + 0.5f) * cellSize
                val bonusCy = startOffsetY + (bonus.y + 0.5f) * cellSize
                val bonusRadius = cellSize * 0.48f * bonusPulseScale
                val bonusGoldColor = Color(0xFFFFD700)

                drawCircle(
                    color = Color(0x66FFD700),
                    radius = bonusRadius * 1.4f,
                    center = Offset(bonusCx, bonusCy)
                )
                drawCircle(
                    color = bonusGoldColor,
                    radius = bonusRadius,
                    center = Offset(bonusCx, bonusCy)
                )
            }

            // 4. Draw Snake body and head (as seen in img.jpg)
            val snake = gameState.snake
            val baseRadius = cellSize * 0.44f

            // Draw body segments (from tail to neck)
            for (i in snake.indices.reversed()) {
                val segment = snake[i]
                val cx = startOffsetX + (segment.x + 0.5f) * cellSize
                val cy = startOffsetY + (segment.y + 0.5f) * cellSize

                if (i == 0) {
                    // Head segment
                    val headRadius = cellSize * 0.46f
                    drawCircle(
                        color = primaryColor,
                        radius = headRadius,
                        center = Offset(cx, cy)
                    )
                    // Draw cute eyes on head looking in current direction
                    drawSnakeEyes(
                        cx = cx,
                        cy = cy,
                        headRadius = headRadius,
                        direction = gameState.direction,
                        eyeColor = eyeColor
                    )
                } else {
                    // Body segment with slight gentle taper towards tail
                    val progress = i.toFloat() / snake.size.toFloat()
                    val segmentRadius = baseRadius * (1f - (progress * 0.12f))

                    drawCircle(
                        color = primaryColor,
                        radius = segmentRadius,
                        center = Offset(cx, cy)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawSnakeEyes(
    cx: Float,
    cy: Float,
    headRadius: Float,
    direction: Direction,
    eyeColor: Color
) {
    val eyeRadius = headRadius * 0.17f
    val forwardOffset = headRadius * 0.32f
    val sideOffset = headRadius * 0.34f

    val eye1Center: Offset
    val eye2Center: Offset

    when (direction) {
        Direction.RIGHT -> {
            eye1Center = Offset(cx + forwardOffset, cy - sideOffset)
            eye2Center = Offset(cx + forwardOffset, cy + sideOffset)
        }
        Direction.LEFT -> {
            eye1Center = Offset(cx - forwardOffset, cy - sideOffset)
            eye2Center = Offset(cx - forwardOffset, cy + sideOffset)
        }
        Direction.UP -> {
            eye1Center = Offset(cx - sideOffset, cy - forwardOffset)
            eye2Center = Offset(cx + sideOffset, cy - forwardOffset)
        }
        Direction.DOWN -> {
            eye1Center = Offset(cx - sideOffset, cy + forwardOffset)
            eye2Center = Offset(cx + sideOffset, cy + forwardOffset)
        }
    }

    drawCircle(
        color = eyeColor,
        radius = eyeRadius,
        center = eye1Center
    )
    drawCircle(
        color = eyeColor,
        radius = eyeRadius,
        center = eye2Center
    )
}
