package com.minimal.snake.data

import androidx.compose.ui.graphics.Color

enum class ThemeMode(val title: String) {
    AMOLED_DARK("AMOLED Dark"),
    LIGHT("Day Light"),
    SYSTEM("System Default")
}

enum class GameDifficulty(val title: String, val speedMs: Long, val pointMultiplier: Int) {
    RELAXED("Relaxed", 170L, 1),
    NORMAL("Normal", 120L, 2),
    BLITZ("Blitz", 80L, 3),
    INSANE("Insane", 55L, 5)
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun isOpposite(other: Direction): Boolean {
        return (this == UP && other == DOWN) ||
                (this == DOWN && other == UP) ||
                (this == LEFT && other == RIGHT) ||
                (this == RIGHT && other == LEFT)
    }
}

data class GridPoint(val x: Int, val y: Int)

data class SnakeSkin(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val glowColor: Color
)

object SnakeSkins {
    val ClassicBlue = SnakeSkin("classic_blue", "Electric Blue", Color(0xFF3B82F6), Color(0x663B82F6))
    val EmeraldGreen = SnakeSkin("emerald_green", "Neon Emerald", Color(0xFF10B981), Color(0x6610B981))
    val SunsetAmber = SnakeSkin("sunset_amber", "Sunset Amber", Color(0xFFF59E0B), Color(0x66F59E0B))
    val NeonViolet = SnakeSkin("neon_violet", "Cyber Violet", Color(0xFF8B5CF6), Color(0x668B5CF6))
    val RosePink = SnakeSkin("rose_pink", "Coral Rose", Color(0xFFF43F5E), Color(0x66F43F5E))
    val CyberCyan = SnakeSkin("cyber_cyan", "Matrix Cyan", Color(0xFF06B6D4), Color(0x6606B6D4))

    val allSkins = listOf(ClassicBlue, EmeraldGreen, SunsetAmber, NeonViolet, RosePink, CyberCyan)

    fun fromId(id: String): SnakeSkin {
        return allSkins.find { it.id == id } ?: ClassicBlue
    }
}

data class GameStats(
    val highScore: Int = 0,
    val gamesPlayed: Int = 0,
    val applesEaten: Int = 0
)

data class GameSettings(
    val difficulty: GameDifficulty = GameDifficulty.NORMAL,
    val wallWrap: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.AMOLED_DARK,
    val skinId: String = SnakeSkins.ClassicBlue.id
)
