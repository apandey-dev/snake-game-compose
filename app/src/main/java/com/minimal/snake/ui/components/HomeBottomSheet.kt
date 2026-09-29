package com.minimal.snake.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.automirrored.rounded.WrapText
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SettingsBrightness
import androidx.compose.material.icons.rounded.SportsScore
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimal.snake.data.GameDifficulty
import com.minimal.snake.data.GameSettings
import com.minimal.snake.data.GameStats
import com.minimal.snake.data.SnakeSkin
import com.minimal.snake.data.SnakeSkins
import com.minimal.snake.data.ThemeMode
import com.minimal.snake.ui.theme.LocalSnakeColors
import com.minimal.snake.ui.theme.Mali

enum class HomeTab(val title: String, val icon: ImageVector) {
    PLAY("Play", Icons.Rounded.PlayArrow),
    THEME("Theme", Icons.Rounded.Palette),
    SCORE("Scores", Icons.Rounded.SportsScore)
}

@Composable
fun HomeBottomSheet(
    settings: GameSettings,
    stats: GameStats,
    onStartGame: () -> Unit,
    onDifficultySelected: (GameDifficulty) -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onSkinSelected: (String) -> Unit,
    onWallWrapToggled: (Boolean) -> Unit,
    onHapticsToggled: (Boolean) -> Unit,
    onSoundToggled: (Boolean) -> Unit,
    onResetStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snakeColors = LocalSnakeColors.current
    var selectedTab by remember { mutableStateOf(HomeTab.PLAY) }
    var showResetDialog by remember { mutableStateOf(false) }

    ResetConfirmationBottomSheet(
        visible = showResetDialog,
        title = "Reset Statistics",
        message = "Are you sure you want to reset your high score and all game statistics?",
        confirmText = "Reset",
        dismissText = "Cancel",
        onConfirm = {
            showResetDialog = false
            onResetStats()
        },
        onDismiss = { showResetDialog = false }
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(snakeColors.surfaceCard)
            .border(
                width = 1.dp,
                color = snakeColors.surfaceCardBorder,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Drag indicator bar (minimal)
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(snakeColors.surfaceCardBorder)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Segmented Tab Selector (Play / Theme / Score)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (snakeColors.isAmoled) Color(0xFF07080A) else Color(0xFFF1F3F7))
                .border(1.dp, snakeColors.surfaceCardBorder, RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HomeTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) snakeColors.activeSkin.primaryColor else Color.Transparent)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { selectedTab = tab },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) Color.White else snakeColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tab.title,
                            fontFamily = Mali,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (isSelected) Color.White else snakeColors.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Animated Tab Content with tight zero-waste spacing
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) {
                    (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                }
            },
            label = "tab_content"
        ) { tab ->
            when (tab) {
                HomeTab.PLAY -> PlayTabContent(
                    difficulty = settings.difficulty,
                    wallWrap = settings.wallWrap,
                    onDifficultySelected = onDifficultySelected,
                    onWallWrapToggled = onWallWrapToggled,
                    onStartGame = onStartGame
                )
                HomeTab.THEME -> ThemeTabContent(
                    currentTheme = settings.themeMode,
                    currentSkinId = settings.skinId,
                    onThemeModeSelected = onThemeModeSelected,
                    onSkinSelected = onSkinSelected
                )
                HomeTab.SCORE -> ScoreTabContent(
                    stats = stats,
                    soundEnabled = settings.soundEnabled,
                    hapticsEnabled = settings.hapticsEnabled,
                    onSoundToggled = onSoundToggled,
                    onHapticsToggled = onHapticsToggled,
                    onResetClick = { showResetDialog = true }
                )
            }
        }
    }
}

@Composable
private fun PlayTabContent(
    difficulty: GameDifficulty,
    wallWrap: Boolean,
    onDifficultySelected: (GameDifficulty) -> Unit,
    onWallWrapToggled: (Boolean) -> Unit,
    onStartGame: () -> Unit
) {
    val snakeColors = LocalSnakeColors.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "DIFFICULTY SPEED",
            fontFamily = Mali,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = snakeColors.textSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GameDifficulty.values().forEach { diff ->
                val isSelected = difficulty == diff
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) snakeColors.activeSkin.primaryColor.copy(alpha = 0.15f)
                            else if (snakeColors.isAmoled) Color(0xFF07080A) else Color(0xFFF1F3F7)
                        )
                        .border(
                            1.dp,
                            if (isSelected) snakeColors.activeSkin.primaryColor else snakeColors.surfaceCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onDifficultySelected(diff) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = diff.title,
                        fontFamily = Mali,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (isSelected) snakeColors.activeSkin.primaryColor else snakeColors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Wall Mode Toggle Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (snakeColors.isAmoled) Color(0xFF07080A) else Color(0xFFF1F3F7))
                .border(1.dp, snakeColors.surfaceCardBorder, RoundedCornerShape(14.dp))
                .clickable { onWallWrapToggled(!wallWrap) }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.WrapText,
                    contentDescription = null,
                    tint = if (wallWrap) snakeColors.activeSkin.primaryColor else snakeColors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (wallWrap) "Portal Wrap (Pass Through Walls)" else "Classic Walls (Hit = Death)",
                        fontFamily = Mali,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = snakeColors.textPrimary
                    )
                }
            }

            Switch(
                checked = wallWrap,
                onCheckedChange = onWallWrapToggled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = snakeColors.activeSkin.primaryColor,
                    uncheckedThumbColor = snakeColors.textSecondary,
                    uncheckedTrackColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Primary Play Button
        MinimalPrimaryButton(
            text = "PLAY NOW",
            icon = Icons.Rounded.PlayArrow,
            onClick = onStartGame
        )
    }
}

@Composable
private fun ThemeTabContent(
    currentTheme: ThemeMode,
    currentSkinId: String,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onSkinSelected: (String) -> Unit
) {
    val snakeColors = LocalSnakeColors.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "APPEARANCE THEME",
            fontFamily = Mali,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = snakeColors.textSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Theme Options (AMOLED / Light / System)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeMode.values().forEach { mode ->
                val isSelected = currentTheme == mode
                val icon = when (mode) {
                    ThemeMode.AMOLED_DARK -> Icons.Rounded.DarkMode
                    ThemeMode.LIGHT -> Icons.Rounded.LightMode
                    ThemeMode.SYSTEM -> Icons.Rounded.SettingsBrightness
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) snakeColors.activeSkin.primaryColor.copy(alpha = 0.15f)
                            else if (snakeColors.isAmoled) Color(0xFF07080A) else Color(0xFFF1F3F7)
                        )
                        .border(
                            1.dp,
                            if (isSelected) snakeColors.activeSkin.primaryColor else snakeColors.surfaceCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onThemeModeSelected(mode) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) snakeColors.activeSkin.primaryColor else snakeColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (mode) {
                                ThemeMode.AMOLED_DARK -> "AMOLED"
                                ThemeMode.LIGHT -> "Light"
                                ThemeMode.SYSTEM -> "System"
                            },
                            fontFamily = Mali,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (isSelected) snakeColors.activeSkin.primaryColor else snakeColors.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "SNAKE COLOR PALETTE",
            fontFamily = Mali,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = snakeColors.textSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Snake Skin Swatches
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(SnakeSkins.allSkins) { skin ->
                val isSelected = currentSkinId == skin.id
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(skin.primaryColor)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onSkinSelected(skin.id) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreTabContent(
    stats: GameStats,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    onSoundToggled: (Boolean) -> Unit,
    onHapticsToggled: (Boolean) -> Unit,
    onResetClick: () -> Unit
) {
    val snakeColors = LocalSnakeColors.current

    Column(modifier = Modifier.fillMaxWidth()) {
        // Stats Cards Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (snakeColors.isAmoled) Color(0xFF07080A) else Color(0xFFF1F3F7))
                .border(1.dp, snakeColors.surfaceCardBorder, RoundedCornerShape(16.dp))
                .padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "BEST SCORE",
                    fontFamily = Mali,
                    fontWeight = FontWeight.Medium,
                    fontSize = 9.sp,
                    color = snakeColors.textSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = stats.highScore.toString(),
                    fontFamily = Mali,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = snakeColors.activeSkin.primaryColor
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(snakeColors.surfaceCardBorder)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "GAMES",
                    fontFamily = Mali,
                    fontWeight = FontWeight.Medium,
                    fontSize = 9.sp,
                    color = snakeColors.textSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = stats.gamesPlayed.toString(),
                    fontFamily = Mali,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = snakeColors.textPrimary
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(snakeColors.surfaceCardBorder)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "APPLES",
                    fontFamily = Mali,
                    fontWeight = FontWeight.Medium,
                    fontSize = 9.sp,
                    color = snakeColors.textSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = stats.applesEaten.toString(),
                    fontFamily = Mali,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = snakeColors.textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sound & Haptic Quick Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sound Toggle Pill
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (snakeColors.isAmoled) Color(0xFF07080A) else Color(0xFFF1F3F7))
                    .border(1.dp, snakeColors.surfaceCardBorder, RoundedCornerShape(12.dp))
                    .clickable { onSoundToggled(!soundEnabled) }
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
                        contentDescription = null,
                        tint = if (soundEnabled) snakeColors.activeSkin.primaryColor else snakeColors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (soundEnabled) "Audio On" else "Muted",
                        fontFamily = Mali,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (soundEnabled) snakeColors.textPrimary else snakeColors.textSecondary
                    )
                }
            }

            // Haptics Toggle Pill
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (snakeColors.isAmoled) Color(0xFF07080A) else Color(0xFFF1F3F7))
                    .border(1.dp, snakeColors.surfaceCardBorder, RoundedCornerShape(12.dp))
                    .clickable { onHapticsToggled(!hapticsEnabled) }
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Vibration,
                        contentDescription = null,
                        tint = if (hapticsEnabled) snakeColors.activeSkin.primaryColor else snakeColors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (hapticsEnabled) "Vibration On" else "Vibration Off",
                        fontFamily = Mali,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (hapticsEnabled) snakeColors.textPrimary else snakeColors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Reset Stats Outline Button
        MinimalOutlineButton(
            text = "Reset All Stats",
            icon = Icons.Rounded.DeleteOutline,
            onClick = onResetClick
        )
    }
}
