package com.minimal.snake.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "snake_preferences")

class GamePreferences(private val context: Context) {

    private val KEY_HIGH_SCORE = intPreferencesKey("high_score")
    private val KEY_GAMES_PLAYED = intPreferencesKey("games_played")
    private val KEY_APPLES_EATEN = intPreferencesKey("apples_eaten")
    private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
    private val KEY_SKIN_ID = stringPreferencesKey("skin_id")
    private val KEY_DIFFICULTY = stringPreferencesKey("difficulty")
    private val KEY_WALL_WRAP = booleanPreferencesKey("wall_wrap")
    private val KEY_HAPTICS = booleanPreferencesKey("haptics_enabled")
    private val KEY_SOUND = booleanPreferencesKey("sound_enabled")

    val statsFlow: Flow<GameStats> = context.dataStore.data.map { prefs ->
        GameStats(
            highScore = prefs[KEY_HIGH_SCORE] ?: 0,
            gamesPlayed = prefs[KEY_GAMES_PLAYED] ?: 0,
            applesEaten = prefs[KEY_APPLES_EATEN] ?: 0
        )
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data.map { prefs ->
        val themeStr = prefs[KEY_THEME_MODE] ?: ThemeMode.AMOLED_DARK.name
        val diffStr = prefs[KEY_DIFFICULTY] ?: GameDifficulty.NORMAL.name
        GameSettings(
            difficulty = try { GameDifficulty.valueOf(diffStr) } catch (e: Exception) { GameDifficulty.NORMAL },
            wallWrap = prefs[KEY_WALL_WRAP] ?: false,
            hapticsEnabled = prefs[KEY_HAPTICS] ?: true,
            soundEnabled = prefs[KEY_SOUND] ?: true,
            themeMode = try { ThemeMode.valueOf(themeStr) } catch (e: Exception) { ThemeMode.AMOLED_DARK },
            skinId = prefs[KEY_SKIN_ID] ?: SnakeSkins.ClassicBlue.id
        )
    }

    suspend fun updateHighScore(score: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_HIGH_SCORE] ?: 0
            if (score > current) {
                prefs[KEY_HIGH_SCORE] = score
            }
        }
    }

    suspend fun recordGameFinished(apples: Int) {
        context.dataStore.edit { prefs ->
            val currentGames = prefs[KEY_GAMES_PLAYED] ?: 0
            val currentApples = prefs[KEY_APPLES_EATEN] ?: 0
            prefs[KEY_GAMES_PLAYED] = currentGames + 1
            prefs[KEY_APPLES_EATEN] = currentApples + apples
        }
    }

    suspend fun resetStats() {
        context.dataStore.edit { prefs ->
            prefs[KEY_HIGH_SCORE] = 0
            prefs[KEY_GAMES_PLAYED] = 0
            prefs[KEY_APPLES_EATEN] = 0
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode.name
        }
    }

    suspend fun setSkinId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SKIN_ID] = id
        }
    }

    suspend fun setDifficulty(diff: GameDifficulty) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DIFFICULTY] = diff.name
        }
    }

    suspend fun setWallWrap(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_WALL_WRAP] = enabled
        }
    }

    suspend fun setHaptics(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HAPTICS] = enabled
        }
    }

    suspend fun setSound(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SOUND] = enabled
        }
    }
}
