package com.minimal.snake.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.minimal.snake.data.Direction
import com.minimal.snake.data.GameDifficulty
import com.minimal.snake.data.GamePreferences
import com.minimal.snake.data.GameSettings
import com.minimal.snake.data.GameStats
import com.minimal.snake.data.SnakeSkin
import com.minimal.snake.data.SnakeSkins
import com.minimal.snake.data.ThemeMode
import com.minimal.snake.game.GameEngine
import com.minimal.snake.game.GameState
import com.minimal.snake.game.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SnakeViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application)
    private val soundManager = SoundManager(application)

    val stats: StateFlow<GameStats> = preferences.statsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameStats()
    )

    val settings: StateFlow<GameSettings> = preferences.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameSettings()
    )

    private val engine = GameEngine(
        scope = viewModelScope,
        soundManager = soundManager,
        onGameEnd = { score, apples ->
            viewModelScope.launch {
                preferences.updateHighScore(score)
                preferences.recordGameFinished(apples)
            }
        }
    )

    val gameState: StateFlow<GameState> = engine.gameState

    init {
        viewModelScope.launch {
            stats.collect { currentStats ->
                engine.currentHighScore = currentStats.highScore
            }
        }
        viewModelScope.launch {
            settings.collect { currentSettings ->
                engine.settings = currentSettings
            }
        }
    }

    fun setGridDimensions(cols: Int, rows: Int) {
        engine.setGridDimensions(cols, rows)
    }

    fun startGame() {
        engine.startGame()
    }

    fun pauseGame() {
        engine.pauseGame()
    }

    fun resumeGame() {
        engine.resumeGame()
    }

    fun togglePause() {
        engine.togglePause()
    }

    fun resetGame() {
        engine.resetGame()
    }

    fun changeDirection(direction: Direction) {
        engine.changeDirection(direction)
    }

    fun setDifficulty(difficulty: GameDifficulty) {
        viewModelScope.launch {
            preferences.setDifficulty(difficulty)
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            preferences.setThemeMode(themeMode)
        }
    }

    fun setSkin(skinId: String) {
        viewModelScope.launch {
            preferences.setSkinId(skinId)
        }
    }

    fun setWallWrap(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setWallWrap(enabled)
        }
    }

    fun setHaptics(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setHaptics(enabled)
        }
    }

    fun setSound(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setSound(enabled)
        }
    }

    fun resetStats() {
        viewModelScope.launch {
            preferences.resetStats()
            engine.resetGame()
        }
    }
}
