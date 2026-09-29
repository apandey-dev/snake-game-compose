package com.minimal.snake.game

import com.minimal.snake.data.Direction
import com.minimal.snake.data.GameDifficulty
import com.minimal.snake.data.GameSettings
import com.minimal.snake.data.GridPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.ArrayDeque
import kotlin.random.Random

class GameEngine(
    private val scope: CoroutineScope,
    private val soundManager: SoundManager,
    private val onGameEnd: (score: Int, apples: Int) -> Unit
) {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var gameLoopJob: Job? = null
    private val inputQueue = ArrayDeque<Direction>()

    var settings: GameSettings = GameSettings()
    var currentHighScore: Int = 0

    private val cols = 17
    private val rows = 27

    init {
        resetGame(cols, rows)
    }

    fun setGridDimensions(newCols: Int, newRows: Int) {
        if (newCols > 4 && newRows > 4 && (newCols != _gameState.value.gridCols || newRows != _gameState.value.gridRows)) {
            _gameState.update { it.copy(gridCols = newCols, gridRows = newRows) }
            resetGame(newCols, newRows)
        }
    }

    fun startGame() {
        if (_gameState.value.isGameOver) {
            resetGame(_gameState.value.gridCols, _gameState.value.gridRows)
        }
        _gameState.update { it.copy(isPaused = false, isReady = true) }
        soundManager.playStartSound(settings.soundEnabled, settings.hapticsEnabled)
        startGameLoop()
    }

    fun pauseGame() {
        _gameState.update { it.copy(isPaused = true) }
        gameLoopJob?.cancel()
    }

    fun resumeGame() {
        if (_gameState.value.isGameOver) return
        _gameState.update { it.copy(isPaused = false) }
        startGameLoop()
    }

    fun togglePause() {
        if (_gameState.value.isPaused) {
            resumeGame()
        } else {
            pauseGame()
        }
    }

    fun resetGame(gridCols: Int = _gameState.value.gridCols, gridRows: Int = _gameState.value.gridRows) {
        gameLoopJob?.cancel()
        inputQueue.clear()

        val startX = gridCols / 2
        val startY = gridRows / 2

        val initialSnake = listOf(
            GridPoint(startX, startY),
            GridPoint(startX - 1, startY),
            GridPoint(startX - 2, startY),
            GridPoint(startX - 3, startY),
            GridPoint(startX - 4, startY)
        )

        val food = generateRandomFood(initialSnake, gridCols, gridRows)

        _gameState.value = GameState(
            snake = initialSnake,
            food = food,
            bonusFood = null,
            bonusTimeLeftSeconds = 0,
            direction = Direction.RIGHT,
            score = 0,
            highScore = currentHighScore,
            isGameOver = false,
            isPaused = false,
            isNewHighScore = false,
            gridCols = gridCols,
            gridRows = gridRows,
            foodEatenCount = 0,
            movesCount = 0,
            isReady = true
        )
    }

    fun changeDirection(newDirection: Direction) {
        if (_gameState.value.isGameOver || _gameState.value.isPaused) return

        // Check if queue has pending direction or use current direction
        val lastDirection = if (inputQueue.isNotEmpty()) inputQueue.last else _gameState.value.direction

        if (newDirection != lastDirection && !newDirection.isOpposite(lastDirection)) {
            if (inputQueue.size < 2) {
                inputQueue.add(newDirection)
                soundManager.playTurnSound(settings.soundEnabled, settings.hapticsEnabled)
            }
        }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = scope.launch(Dispatchers.Default) {
            var bonusCounterMs = 0L
            while (isActive && !_gameState.value.isGameOver && !_gameState.value.isPaused) {
                val speed = settings.difficulty.speedMs
                delay(speed)

                tick()

                // Handle bonus food countdown
                if (_gameState.value.bonusFood != null) {
                    bonusCounterMs += speed
                    if (bonusCounterMs >= 1000) {
                        bonusCounterMs = 0
                        _gameState.update { state ->
                            val newTime = state.bonusTimeLeftSeconds - 1
                            if (newTime <= 0) {
                                state.copy(bonusFood = null, bonusTimeLeftSeconds = 0)
                            } else {
                                state.copy(bonusTimeLeftSeconds = newTime)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun tick() {
        val currentState = _gameState.value
        if (currentState.isGameOver || currentState.isPaused) return

        // Get next direction from input queue if present
        val currentDirection = if (inputQueue.isNotEmpty()) {
            inputQueue.poll() ?: currentState.direction
        } else {
            currentState.direction
        }

        val head = currentState.snake.first()
        var newHeadX = head.x
        var newHeadY = head.y

        when (currentDirection) {
            Direction.UP -> newHeadY -= 1
            Direction.DOWN -> newHeadY += 1
            Direction.LEFT -> newHeadX -= 1
            Direction.RIGHT -> newHeadX += 1
        }

        val cols = currentState.gridCols
        val rows = currentState.gridRows

        // Handle wall collision vs wrap-around
        if (settings.wallWrap) {
            newHeadX = (newHeadX + cols) % cols
            newHeadY = (newHeadY + rows) % rows
        } else {
            if (newHeadX < 0 || newHeadX >= cols || newHeadY < 0 || newHeadY >= rows) {
                triggerGameOver()
                return
            }
        }

        val newHead = GridPoint(newHeadX, newHeadY)

        // Self-collision check (excluding tail if it's going to move away and not eating food)
        val isEatingFood = newHead == currentState.food
        val isEatingBonus = currentState.bonusFood != null && newHead == currentState.bonusFood
        val bodyToCheck = if (isEatingFood || isEatingBonus) {
            currentState.snake
        } else {
            currentState.snake.dropLast(1)
        }

        if (bodyToCheck.contains(newHead)) {
            triggerGameOver()
            return
        }

        // Build new snake body
        val newSnake = mutableListOf(newHead)
        newSnake.addAll(currentState.snake)

        var newScore = currentState.score
        var newFood = currentState.food
        var newBonusFood = currentState.bonusFood
        var newBonusTime = currentState.bonusTimeLeftSeconds
        var newEatenCount = currentState.foodEatenCount
        var isNewHigh = currentState.isNewHighScore

        if (isEatingFood) {
            val pointsGained = 10 * settings.difficulty.pointMultiplier
            newScore += pointsGained
            newEatenCount += 1
            newFood = generateRandomFood(newSnake, cols, rows)
            soundManager.playEatSound(settings.soundEnabled, settings.hapticsEnabled)

            // Spawn bonus golden food every 5 food items
            if (newEatenCount % 5 == 0 && newBonusFood == null) {
                newBonusFood = generateRandomFood(newSnake + listOf(newFood), cols, rows)
                newBonusTime = 6 // 6 seconds to grab bonus
            }
        } else if (isEatingBonus) {
            val bonusPoints = 30 * settings.difficulty.pointMultiplier
            newScore += bonusPoints
            newBonusFood = null
            newBonusTime = 0
            soundManager.playEatSound(settings.soundEnabled, settings.hapticsEnabled)
        } else {
            newSnake.removeAt(newSnake.size - 1)
        }

        if (newScore > currentState.highScore) {
            isNewHigh = true
        }

        _gameState.update {
            it.copy(
                snake = newSnake,
                food = newFood,
                bonusFood = newBonusFood,
                bonusTimeLeftSeconds = newBonusTime,
                direction = currentDirection,
                score = newScore,
                highScore = maxOf(newScore, currentState.highScore),
                isNewHighScore = isNewHigh,
                foodEatenCount = newEatenCount,
                movesCount = it.movesCount + 1
            )
        }
    }

    private fun triggerGameOver() {
        soundManager.playGameOverSound(settings.soundEnabled, settings.hapticsEnabled)
        _gameState.update { it.copy(isGameOver = true) }
        gameLoopJob?.cancel()
        onGameEnd(_gameState.value.score, _gameState.value.foodEatenCount)
    }

    private fun generateRandomFood(snake: List<GridPoint>, cols: Int, rows: Int): GridPoint {
        val emptyPoints = mutableListOf<GridPoint>()
        val snakeSet = snake.toHashSet()
        for (x in 0 until cols) {
            for (y in 0 until rows) {
                val pt = GridPoint(x, y)
                if (!snakeSet.contains(pt)) {
                    emptyPoints.add(pt)
                }
            }
        }
        return if (emptyPoints.isNotEmpty()) {
            emptyPoints[Random.nextInt(emptyPoints.size)]
        } else {
            GridPoint(0, 0)
        }
    }
}
