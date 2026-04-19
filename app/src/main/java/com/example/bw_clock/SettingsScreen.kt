package com.example.bw_clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class SettingsItem(
    val label: String,
    val valueText: String,
    val onLeft: () -> Unit,
    val onRight: () -> Unit,
    val onSelect: () -> Unit = onRight
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: ClockSettings,
    repository: SettingsRepository,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    rotation: Int = 0
) {
    val isVertical = rotation == 90 || rotation == 270
    val datePositionLabel = if (isVertical) {
        if (settings.datePosition == DatePosition.LEFT) "上" else "下"
    } else {
        settings.datePosition.label
    }

    val items = remember(settings, isVertical) {
        listOf(
            SettingsItem(
                label = "明るさ",
                valueText = "${settings.brightnessPercent}%",
                onLeft = { coroutineScope.launch { repository.updateBrightnessPercent(settings.brightnessPercent - 10) } },
                onRight = { coroutineScope.launch { repository.updateBrightnessPercent(settings.brightnessPercent + 10) } }
            ),
            SettingsItem(
                label = "テーマ",
                valueText = if (settings.isDarkBackground) "黒背景" else "白背景",
                onLeft = { coroutineScope.launch { repository.updateDarkBackground(!settings.isDarkBackground) } },
                onRight = { coroutineScope.launch { repository.updateDarkBackground(!settings.isDarkBackground) } }
            ),
            SettingsItem(
                label = "秒針",
                valueText = if (settings.showSecondHand) "ON" else "OFF",
                onLeft = { coroutineScope.launch { repository.updateShowSecondHand(!settings.showSecondHand) } },
                onRight = { coroutineScope.launch { repository.updateShowSecondHand(!settings.showSecondHand) } }
            ),
            SettingsItem(
                label = "数字",
                valueText = if (settings.showNumbers) "ON" else "OFF",
                onLeft = { coroutineScope.launch { repository.updateShowNumbers(!settings.showNumbers) } },
                onRight = { coroutineScope.launch { repository.updateShowNumbers(!settings.showNumbers) } }
            ),
            SettingsItem(
                label = "目盛り",
                valueText = if (settings.showTickMarks) "ON" else "OFF",
                onLeft = { coroutineScope.launch { repository.updateShowTickMarks(!settings.showTickMarks) } },
                onRight = { coroutineScope.launch { repository.updateShowTickMarks(!settings.showTickMarks) } }
            ),
            SettingsItem(
                label = "外枠",
                valueText = if (settings.showFrame) "ON" else "OFF",
                onLeft = { coroutineScope.launch { repository.updateShowFrame(!settings.showFrame) } },
                onRight = { coroutineScope.launch { repository.updateShowFrame(!settings.showFrame) } }
            ),
            SettingsItem(
                label = "フォント",
                valueText = settings.clockFont.label,
                onLeft = {
                    val fonts = ClockFont.entries
                    val prev = fonts[(settings.clockFont.ordinal - 1 + fonts.size) % fonts.size]
                    coroutineScope.launch { repository.updateClockFont(prev) }
                },
                onRight = {
                    val fonts = ClockFont.entries
                    val next = fonts[(settings.clockFont.ordinal + 1) % fonts.size]
                    coroutineScope.launch { repository.updateClockFont(next) }
                }
            ),
            SettingsItem(
                label = "サイズ",
                valueText = "${settings.clockSizePercent}%",
                onLeft = { coroutineScope.launch { repository.updateClockSizePercent(settings.clockSizePercent - 10) } },
                onRight = { coroutineScope.launch { repository.updateClockSizePercent(settings.clockSizePercent + 10) } }
            ),
            SettingsItem(
                label = "時計 横位置",
                valueText = "${settings.clockOffsetX}%",
                onLeft = { coroutineScope.launch { repository.updateClockOffsetX(settings.clockOffsetX - 5) } },
                onRight = { coroutineScope.launch { repository.updateClockOffsetX(settings.clockOffsetX + 5) } }
            ),
            SettingsItem(
                label = "時計 縦位置",
                valueText = "${settings.clockOffsetY}%",
                onLeft = { coroutineScope.launch { repository.updateClockOffsetY(settings.clockOffsetY - 5) } },
                onRight = { coroutineScope.launch { repository.updateClockOffsetY(settings.clockOffsetY + 5) } }
            ),
            SettingsItem(
                label = "日付",
                valueText = if (settings.showDate) "ON" else "OFF",
                onLeft = { coroutineScope.launch { repository.updateShowDate(!settings.showDate) } },
                onRight = { coroutineScope.launch { repository.updateShowDate(!settings.showDate) } }
            ),
            SettingsItem(
                label = "日付位置",
                valueText = datePositionLabel,
                onLeft = {
                    val positions = DatePosition.entries
                    val prev = positions[(settings.datePosition.ordinal - 1 + positions.size) % positions.size]
                    coroutineScope.launch { repository.updateDatePosition(prev) }
                },
                onRight = {
                    val positions = DatePosition.entries
                    val next = positions[(settings.datePosition.ordinal + 1) % positions.size]
                    coroutineScope.launch { repository.updateDatePosition(next) }
                }
            ),
            SettingsItem(
                label = "日付サイズ",
                valueText = "${settings.dateSizePercent}%",
                onLeft = { coroutineScope.launch { repository.updateDateSizePercent(settings.dateSizePercent - 10) } },
                onRight = { coroutineScope.launch { repository.updateDateSizePercent(settings.dateSizePercent + 10) } }
            ),
            SettingsItem(
                label = "日付 横位置",
                valueText = "${settings.dateOffsetX}%",
                onLeft = { coroutineScope.launch { repository.updateDateOffsetX(settings.dateOffsetX - 5) } },
                onRight = { coroutineScope.launch { repository.updateDateOffsetX(settings.dateOffsetX + 5) } }
            ),
            SettingsItem(
                label = "日付 縦位置",
                valueText = "${settings.dateOffsetY}%",
                onLeft = { coroutineScope.launch { repository.updateDateOffsetY(settings.dateOffsetY - 5) } },
                onRight = { coroutineScope.launch { repository.updateDateOffsetY(settings.dateOffsetY + 5) } }
            ),
            SettingsItem(
                label = "焼付防止",
                valueText = if (settings.burnInPrevention) "ON" else "OFF",
                onLeft = { coroutineScope.launch { repository.updateBurnInPrevention(!settings.burnInPrevention) } },
                onRight = { coroutineScope.launch { repository.updateBurnInPrevention(!settings.burnInPrevention) } }
            ),
            SettingsItem(
                label = "回転",
                valueText = "${settings.rotation}°",
                onLeft = { coroutineScope.launch { repository.updateRotation(settings.rotation - 90) } },
                onRight = { coroutineScope.launch { repository.updateRotation(settings.rotation + 90) } }
            ),
            SettingsItem(
                label = "リセット",
                valueText = "初期状態に戻す",
                onLeft = { coroutineScope.launch { repository.resetToDefaults() } },
                onRight = { coroutineScope.launch { repository.resetToDefaults() } }
            )
        )
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                when (event.key) {
                    Key.DirectionUp -> {
                        selectedIndex = (selectedIndex - 1 + items.size) % items.size
                        true
                    }
                    Key.DirectionDown -> {
                        selectedIndex = (selectedIndex + 1) % items.size
                        true
                    }
                    Key.DirectionLeft -> {
                        items[selectedIndex].onLeft()
                        true
                    }
                    Key.DirectionRight -> {
                        items[selectedIndex].onRight()
                        true
                    }
                    Key.Enter, Key.DirectionCenter -> {
                        items[selectedIndex].onSelect()
                        true
                    }
                    Key.Back, Key.Escape -> {
                        onDismiss()
                        true
                    }
                    else -> false
                }
            },
        contentAlignment = if (isVertical) Alignment.BottomCenter else Alignment.CenterEnd
    ) {
        Column(
            modifier = Modifier
                .width(360.dp)
                .heightIn(max = 500.dp)
                .background(Color.DarkGray.copy(alpha = 0.9f))
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "設定",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) Color.White.copy(alpha = 0.2f)
                            else Color.Transparent
                        )
                        .then(
                            if (isSelected) Modifier.border(1.dp, Color.White)
                            else Modifier
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.label,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "◀ ${item.valueText} ▶",
                        color = if (isSelected) Color.Yellow else Color.LightGray,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Auto-scroll to keep selected item visible
    androidx.compose.runtime.LaunchedEffect(selectedIndex) {
        val itemHeight = 40 // approximate item height in pixels
        val targetScroll = (selectedIndex * itemHeight - 100).coerceAtLeast(0)
        scrollState.animateScrollTo(targetScroll)
    }
}
