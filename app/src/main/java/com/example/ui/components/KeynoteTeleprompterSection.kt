package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeynoteScript
import com.example.data.model.KeynoteScriptCatalog
import com.example.ui.theme.VocalisAmber
import com.example.ui.theme.VocalisCardBorder
import com.example.ui.theme.VocalisCrimson
import com.example.ui.theme.VocalisEmerald
import com.example.ui.theme.VocalisIndigo
import com.example.ui.theme.VocalisObsidian
import com.example.ui.theme.VocalisSky
import com.example.ui.theme.VocalisSurface
import com.example.ui.theme.VocalisSurfaceElevated
import com.example.ui.theme.VocalisTextMuted
import com.example.ui.theme.VocalisTextPrimary
import com.example.ui.theme.VocalisTextSecondary
import kotlinx.coroutines.delay

/**
 * World-Class Keynote Executive Teleprompter & Delivery Pacing Coach.
 * Autoscrolls script with pacing rhythm, cue guidance, and real-time WPM calibration.
 */
@Composable
fun KeynoteTeleprompterSection(
    isAudioRecording: Boolean,
    onToggleRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedScriptIndex by remember { mutableIntStateOf(0) }
    val currentScript = KeynoteScriptCatalog.allScripts[selectedScriptIndex]

    var targetWpm by remember { mutableFloatStateOf(currentScript.targetWpm.toFloat()) }
    var currentLineIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }

    // Update target WPM when script changes
    LaunchedEffect(selectedScriptIndex) {
        targetWpm = currentScript.targetWpm.toFloat()
        currentLineIndex = 0
        isPlaying = false
    }

    // Auto-advance loop when playing
    LaunchedEffect(isPlaying, currentLineIndex, targetWpm) {
        if (isPlaying && currentLineIndex < currentScript.lines.size) {
            val line = currentScript.lines[currentLineIndex]
            val wordCount = line.text.split("\\s+".toRegex()).size
            // Duration derived from target WPM: (words / WPM) * 60 seconds
            val computedDurationMs = ((wordCount.toFloat() / targetWpm) * 60_000L).toLong().coerceIn(2800L, 10000L)
            delay(computedDurationMs)
            if (currentLineIndex < currentScript.lines.size - 1) {
                currentLineIndex++
            } else {
                isPlaying = false
            }
        }
    }

    val progress = if (currentScript.lines.isNotEmpty()) {
        (currentLineIndex + 1).toFloat() / currentScript.lines.size
    } else 0f

    val animatedProgress by animateFloatAsState(targetValue = progress, label = "teleprompter_progress")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("keynote_teleprompter_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VocalisSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(
                    if (isPlaying) VocalisEmerald.copy(alpha = 0.6f) else VocalisCardBorder,
                    VocalisCardBorder
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(VocalisEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = VocalisEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "KEYNOTE ORATOR TELEPROMPTER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = VocalisEmerald
                        )
                        Text(
                            text = "Executive pacing rhythm & live delivery cues",
                            fontSize = 9.sp,
                            color = VocalisTextMuted
                        )
                    }
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isPlaying) VocalisEmerald.copy(alpha = 0.2f) else VocalisSurfaceElevated)
                        .border(1.dp, if (isPlaying) VocalisEmerald else VocalisCardBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isPlaying) "TELEPROMPTER ROLLING" else "STANDBY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPlaying) VocalisEmerald else VocalisTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Script Selector Carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(KeynoteScriptCatalog.allScripts) { index, script ->
                    val isSelected = selectedScriptIndex == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) VocalisEmerald.copy(alpha = 0.18f) else VocalisSurfaceElevated)
                            .border(1.dp, if (isSelected) VocalisEmerald else VocalisCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                selectedScriptIndex = index
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = script.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) VocalisEmerald else VocalisTextPrimary
                            )
                            Text(
                                text = script.speaker,
                                fontSize = 9.sp,
                                color = if (isSelected) VocalisTextPrimary else VocalisTextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Big High-Focus Teleprompter Screen Chamber
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(VocalisObsidian)
                    .border(1.dp, if (isPlaying) VocalisEmerald.copy(alpha = 0.4f) else VocalisCardBorder, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                val currentLine = currentScript.lines.getOrNull(currentLineIndex) ?: currentScript.lines[0]

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Executive Delivery Cue Banner
                    if (currentLine.cue != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (currentLine.isPause) VocalisAmber.copy(alpha = 0.2f) else VocalisIndigo.copy(alpha = 0.2f))
                                .border(1.dp, if (currentLine.isPause) VocalisAmber else VocalisIndigo, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "💡 CUE: ${currentLine.cue}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (currentLine.isPause) VocalisAmber else VocalisIndigo
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Main High-Visibility Line Text
                    Text(
                        text = "\"${currentLine.text}\"",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = VocalisTextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )

                    // Upcoming Next Line Sneak-Peek
                    val nextLine = currentScript.lines.getOrNull(currentLineIndex + 1)
                    Text(
                        text = if (nextLine != null) "NEXT: \"${nextLine.text.take(65)}...\"" else "FINALE: Conclude with firm eye contact",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = VocalisTextMuted,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar & Step Counter
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Line ${currentLineIndex + 1} of ${currentScript.lines.size}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = VocalisTextSecondary
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% Delivered",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = VocalisEmerald
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = VocalisEmerald,
                    trackColor = VocalisSurfaceElevated
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pacing Controller (Target WPM Slider)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = VocalisAmber, modifier = Modifier.size(16.dp))
                    Text(
                        text = "CADENCE TEMPO: ${targetWpm.toInt()} WPM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VocalisAmber
                    )
                }

                Text(
                    text = when {
                        targetWpm < 125 -> "Measured / Heavy Gravitas"
                        targetWpm <= 155 -> "Executive Gold Standard"
                        else -> "High Energy Urgency"
                    },
                    fontSize = 10.sp,
                    color = VocalisTextSecondary
                )
            }

            Slider(
                value = targetWpm,
                onValueChange = { targetWpm = it },
                valueRange = 110f..185f,
                steps = 14,
                colors = SliderDefaults.colors(
                    thumbColor = VocalisEmerald,
                    activeTrackColor = VocalisEmerald,
                    inactiveTrackColor = VocalisSurfaceElevated
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Teleprompter Playback & Mic Control Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause Prompter Button
                Button(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("teleprompter_play_toggle_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) VocalisAmber else VocalisEmerald,
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isPlaying) "Pause Autoscroll" else "Start Teleprompter",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }

                // Reset Line Button
                IconButton(
                    onClick = {
                        isPlaying = false
                        currentLineIndex = 0
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VocalisSurfaceElevated)
                        .border(1.dp, VocalisCardBorder, RoundedCornerShape(12.dp))
                        .testTag("teleprompter_reset_button")
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Restart Script",
                        tint = VocalisTextPrimary
                    )
                }

                // Microphone Toggle for Simultaneous Voice Capture
                IconButton(
                    onClick = onToggleRecording,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isAudioRecording) VocalisCrimson else VocalisSurfaceElevated)
                        .border(1.dp, if (isAudioRecording) VocalisCrimson else VocalisCardBorder, RoundedCornerShape(12.dp))
                        .testTag("teleprompter_mic_button")
                ) {
                    Icon(
                        imageVector = if (isAudioRecording) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Toggle Recording",
                        tint = if (isAudioRecording) Color.White else VocalisEmerald
                    )
                }
            }
        }
    }
}
