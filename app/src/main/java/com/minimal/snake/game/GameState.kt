package com.minimal.snake.game

import com.minimal.snake.data.Direction
import com.minimal.snake.data.GridPoint

data class GameState(
    val snake: List<GridPoint> = listOf(
        GridPoint(7, 12),
        GridPoint(6, 12),
        GridPoint(5, 12),
        GridPoint(4, 12),
        GridPoint(3, 12)
    ),
    val food: GridPoint = GridPoint(12, 18),
    val bonusFood: GridPoint? = null,
    val bonusTimeLeftSeconds: Int = 0,
    val direction: Direction = Direction.RIGHT,
    val score: Int = 0,
    val highScore: Int = 0,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
    val isNewHighScore: Boolean = false,
    val gridCols: Int = 15,
    val gridRows: Int = 23,
    val foodEatenCount: Int = 0,
    val movesCount: Int = 0,
    val isReady: Boolean = false
)
