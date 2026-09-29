package com.minimal.snake.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.minimal.snake.data.Direction
import com.minimal.snake.data.GameSettings
import com.minimal.snake.game.GameState
import com.minimal.snake.ui.components.GameBoardCanvas
import com.minimal.snake.ui.components.GameOverBottomSheet
import com.minimal.snake.ui.components.GameTopBar
import com.minimal.snake.ui.components.PauseBottomSheet
import com.minimal.snake.ui.theme.LocalSnakeColors

@Composable
fun GameScreen(
    gameState: GameState,
    settings: GameSettings,
    onDirectionChange: (Direction) -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onRestartClick: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snakeColors = LocalSnakeColors.current

    BackHandler {
        if (!gameState.isGameOver && !gameState.isPaused) {
            onPauseClick()
        } else {
            onGoHome()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (snakeColors.isAmoled) Color.Black else Color(0xFFF7F8FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Minimal Top Bar (Score & Best in single row, text-based Pause button, no back icon)
            GameTopBar(
                score = gameState.score,
                highScore = gameState.highScore,
                isPaused = gameState.isPaused,
                bonusSeconds = gameState.bonusTimeLeftSeconds,
                onPauseClick = onPauseClick
            )

            // Enlarged Game Board Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                GameBoardCanvas(
                    gameState = gameState,
                    onDirectionChange = onDirectionChange,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Custom Game Over Bottom Sheet
        GameOverBottomSheet(
            visible = gameState.isGameOver,
            score = gameState.score,
            highScore = gameState.highScore,
            isNewHighScore = gameState.isNewHighScore,
            foodEaten = gameState.foodEatenCount,
            onPlayAgain = onRestartClick,
            onGoHome = onGoHome
        )

        // Custom Pause Bottom Sheet
        PauseBottomSheet(
            visible = gameState.isPaused && !gameState.isGameOver,
            score = gameState.score,
            onResume = onResumeClick,
            onRestart = onRestartClick,
            onGoHome = onGoHome
        )
    }
}
