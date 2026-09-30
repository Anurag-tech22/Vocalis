package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.VoiceWaveVisualizer
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.PolarisDarkBorder
import com.example.ui.theme.PolarisIndigoSecondary
import com.example.ui.theme.ScoreAmber
import com.example.ui.theme.ScoreGreen
import com.example.ui.theme.ScoreRed
import com.example.viewmodel.RehearsalViewModel

@Composable
fun GameRoundScreen(
    viewModel: RehearsalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showQuitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showQuitDialog = true
    }

    val challenge by viewModel.activeChallenge.collectAsState()
    val secondsRemaining by viewModel.gameSecondsRemaining.collectAsState()
    val isGameRunning by viewModel.isGameRunning.collectAsState()
    val liveTranscript by viewModel.gameLiveTranscript.collectAsState()
    val fillersCount by viewModel.gameFillersCount.collectAsState()
    val composure by viewModel.gameComposure.collectAsState()
    val showResult by viewModel.showGameResult.collectAsState()
    val gameResult by viewModel.gameResult.collectAsState()
    val audioRms by viewModel.speechManager.audioRms.collectAsState()
    val isSpeaking by viewModel.speechManager.isSpeaking.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startGameLiveSession()
        }
    }

    val handleStartStop = {
        if (isGameRunning) {
            viewModel.finishGameRound()
        } else {
            val hasPerm = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPerm) {
                viewModel.startGameLiveSession()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // Dynamic timer color
    val timerColor by animateColorAsState(
        targetValue = when {
            secondsRemaining <= 7 -> ScoreRed
            secondsRemaining <= 15 -> ScoreAmber
            else -> PolarisBluePrimary
        },
        label = "timer_color"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Game HUD: Back, Category, Timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { showQuitDialog = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quit Round",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = challenge?.title ?: "Charisma Challenge",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Opponent: ${challenge?.opponentName ?: "Opponent"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PolarisAmberGold
                )
            }

            // Big Timer Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(timerColor.copy(alpha = 0.2f))
                    .border(1.5.dp, timerColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${secondsRemaining}s",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = timerColor
                )
            }
        }

        // Live Composure & Filler HUD Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Composure
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$composure%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (composure >= 70) ScoreGreen else if (composure >= 45) ScoreAmber else ScoreRed
                    )
                    Text(
                        text = "Vocal Composure",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }

                // Fillers Detected Live Alarm
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (fillersCount > 0) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = ScoreRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }
                        Text(
                            text = "$fillersCount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (fillersCount == 0) ScoreGreen else ScoreRed
                        )
                    }
                    Text(
                        text = "Fillers ('um', 'like')",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }

                // Tempo
                val wordCount = liveTranscript.split("\\s+".toRegex()).count { it.isNotBlank() }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$wordCount words",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PolarisBluePrimary
                    )
                    Text(
                        text = "Volume",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Opponent Challenge Prompt Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(PolarisAmberGold.copy(alpha = 0.5f), PolarisBluePrimary.copy(alpha = 0.3f)))
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PolarisAmberGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PolarisAmberGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "${challenge?.opponentName} says:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PolarisAmberGold
                        )
                    }

                    IconButton(
                        onClick = {
                            challenge?.promptQuestion?.let { viewModel.speechManager.speak(it) }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Replay Question",
                            tint = PolarisBluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "\"${challenge?.promptQuestion}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Humor cue: ${challenge?.scenarioHumor}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        // Live Voice Capture & Speech Waveform
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    if (isGameRunning) listOf(PolarisAmberGold, PolarisBluePrimary)
                    else listOf(PolarisDarkBorder, PolarisDarkBorder)
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isGameRunning) "🎙️ Speaking Live — Timer is Ticking!" else "Tap Start to Begin Speaking",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isGameRunning) PolarisAmberGold else MaterialTheme.colorScheme.onSurface
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(12.dp)
                ) {
                    if (liveTranscript.isNotBlank()) {
                        Text(
                            text = liveTranscript,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Text(
                            text = if (isGameRunning) "Speak continuously! Maintain flow and avoid pauses or fillers..." else "When ready, hit START and answer the challenge out loud.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                VoiceWaveVisualizer(
                    isActive = isGameRunning,
                    isListening = isGameRunning,
                    audioRms = audioRms,
                    modifier = Modifier.fillMaxWidth()
                )

                // Big Action Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = handleStartStop,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isGameRunning) ScoreRed else PolarisAmberGold,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .size(56.dp)
                            .testTag("game_mic_button")
                    ) {
                        Icon(
                            imageVector = if (isGameRunning) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Button(
                        onClick = { viewModel.finishGameRound() },
                        enabled = isGameRunning || liveTranscript.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PolarisBluePrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Finish & Score Round", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Quit Round Confirmation
    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showQuitDialog = false
                        viewModel.stopGameRound()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Quit Round")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showQuitDialog = false }) {
                    Text("Keep Playing")
                }
            },
            title = { Text("Leave Game Round?") },
            text = { Text("Are you sure you want to quit this challenge? Progress for this round will be lost.") }
        )
    }

    // Game Over & Victory Celebration Dialog
    if (showResult && gameResult != null) {
        val result = gameResult!!

        AlertDialog(
            onDismissRequest = { viewModel.dismissGameResult() },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissGameResult() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PolarisAmberGold, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().testTag("close_game_result_button")
                ) {
                    Text("Back to Arena (+${result.xpEarned} XP Claimed)", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Star Rating
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < result.stars) PolarisAmberGold else PolarisDarkBorder,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (result.stars >= 2) "VICTORY ACHIEVED!" else "CHALLENGE COMPLETED",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (result.stars >= 2) ScoreGreen else PolarisAmberGold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // XP Reward Pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PolarisAmberGold.copy(alpha = 0.2f))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${result.xpEarned} XP EARNED ⚡",
                            fontWeight = FontWeight.ExtraBold,
                            color = PolarisAmberGold,
                            fontSize = 14.sp
                        )
                    }

                    // Key Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${result.composureScore}%",
                                fontWeight = FontWeight.Bold,
                                color = if (result.composureScore >= 70) ScoreGreen else ScoreAmber,
                                fontSize = 16.sp
                            )
                            Text("Composure", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${result.fillerCount}",
                                fontWeight = FontWeight.Bold,
                                color = if (result.fillerCount == 0) ScoreGreen else ScoreRed,
                                fontSize = 16.sp
                            )
                            Text("Fillers", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${result.wordsPerMinute} WPM",
                                fontWeight = FontWeight.Bold,
                                color = PolarisBluePrimary,
                                fontSize = 16.sp
                            )
                            Text("Speech Tempo", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                        }
                    }

                    // Badge Unlocked if any
                    result.badgeUnlocked?.let { badge ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ScoreGreen.copy(alpha = 0.15f))
                                .border(1.dp, ScoreGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = badge,
                                fontWeight = FontWeight.Bold,
                                color = ScoreGreen,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Tactical & Humorous Feedback
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Judge Feedback",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PolarisAmberGold
                            )
                            Text(
                                text = "\"${result.feedback}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
