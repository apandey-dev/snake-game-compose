package com.minimal.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.minimal.snake.data.SnakeSkins
import com.minimal.snake.ui.screens.GameScreen
import com.minimal.snake.ui.screens.HomeScreen
import com.minimal.snake.ui.theme.SnakeGameTheme
import com.minimal.snake.ui.viewmodel.SnakeViewModel

enum class Screen {
    HOME, GAME
}

class MainActivity : ComponentActivity() {

    private val viewModel: SnakeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val settings by viewModel.settings.collectAsState()
            val stats by viewModel.stats.collectAsState()
            val gameState by viewModel.gameState.collectAsState()

            val currentSkin = remember(settings.skinId) {
                SnakeSkins.fromId(settings.skinId)
            }

            SnakeGameTheme(
                themeMode = settings.themeMode,
                skin = currentSkin
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    var currentScreen by remember { mutableStateOf(Screen.HOME) }

                    Crossfade(
                        targetState = currentScreen,
                        animationSpec = tween(300),
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            Screen.HOME -> HomeScreen(
                                settings = settings,
                                stats = stats,
                                onStartGame = {
                                    viewModel.resetGame()
                                    viewModel.startGame()
                                    currentScreen = Screen.GAME
                                },
                                onDifficultySelected = { viewModel.setDifficulty(it) },
                                onThemeModeSelected = { viewModel.setThemeMode(it) },
                                onSkinSelected = { viewModel.setSkin(it) },
                                onWallWrapToggled = { viewModel.setWallWrap(it) },
                                onHapticsToggled = { viewModel.setHaptics(it) },
                                onSoundToggled = { viewModel.setSound(it) },
                                onResetStats = { viewModel.resetStats() }
                            )
                            Screen.GAME -> GameScreen(
                                gameState = gameState,
                                settings = settings,
                                onDirectionChange = { viewModel.changeDirection(it) },
                                onPauseClick = { viewModel.togglePause() },
                                onResumeClick = { viewModel.resumeGame() },
                                onRestartClick = {
                                    viewModel.resetGame()
                                    viewModel.startGame()
                                },
                                onGoHome = {
                                    viewModel.pauseGame()
                                    currentScreen = Screen.HOME
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
