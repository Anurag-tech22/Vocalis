package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeOff
import com.example.ui.components.ExecutiveCameraMirror
import com.example.ui.components.ExecutiveRedlineInspector
import com.example.ui.theme.VocalisCyan
import com.example.ui.theme.VocalisEmerald
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.NeuralTelemetryHud
import com.example.ui.components.ScoreBadge
import com.example.ui.components.VoiceWaveVisualizer
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.PolarisDarkBorder
import com.example.ui.theme.PolarisIndigoSecondary
import com.example.ui.theme.ScoreGreen
import com.example.ui.theme.ScoreRed
import com.example.viewmodel.RehearsalViewModel

@Composable
fun PracticeScreen(
    viewModel: RehearsalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }

    // Intercept back navigation
    BackHandler {
        showExitDialog = true
    }

    val currentSetup by viewModel.currentSetup.collectAsState()
    val turns by viewModel.turns.collectAsState()
    val turnIndex by viewModel.currentTurnIndex.collectAsState()
    val maxTurns = viewModel.maxTurns
    val currentAiPrompt by viewModel.currentAiPrompt.collectAsState()
    val userInputText by viewModel.userInputText.collectAsState()
    val isEvaluating by viewModel.isEvaluating.collectAsState()
    val isSpeaking by viewModel.speechManager.isSpeaking.collectAsState()
    val isListening by viewModel.speechManager.isListening.collectAsState()
    val audioRms by viewModel.speechManager.audioRms.collectAsState()
    val speechError by viewModel.speechManager.speechError.collectAsState()
    val showScoreCard by viewModel.showScoreCard.collectAsState()
    val finalScoreCard by viewModel.finalScoreCard.collectAsState()
    val selectedTone by viewModel.selectedTone.collectAsState()

    val isStressModeActive by viewModel.isStressModeActive.collectAsState()
    val activeCurveball by viewModel.activeCurveball.collectAsState()
    val curveballSecondsLeft by viewModel.curveballSecondsLeft.collectAsState()
    val elapsedSeconds by viewModel.sessionSpeechDurationSeconds.collectAsState()
    val liveFillerCount by viewModel.liveFillerCount.collectAsState()

    val isBoardroomModeActive by viewModel.isBoardroomModeActive.collectAsState()
    val boardroomPanel by viewModel.boardroomPanel.collectAsState()
    val activePanelistIndex by viewModel.activePanelistIndex.collectAsState()
    val isCameraMirrorExpanded by viewModel.isCameraMirrorExpanded.collectAsState()
    val latestTurnRedline by viewModel.latestTurnRedline.collectAsState()

    var isEditingTranscript by remember { mutableStateOf(false) }

    // Permission launcher for Microphone
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        } else {
            // Permission denied - user can still type manually
        }
    }

    val handleMicToggle = {
        if (isListening) {
            viewModel.stopListening()
        } else {
            if (isSpeaking) {
                viewModel.stopSpeaking() // Natural human conversational barge-in
            }
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                viewModel.startListening()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Bar: Back button, Persona, Turn Counter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier.testTag("practice_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column {
                    Text(
                        text = currentSetup?.personaName ?: "Vocalis Counterpart",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = currentSetup?.personaRole ?: currentSetup?.scenarioType ?: "Examiner",
                        style = MaterialTheme.typography.labelSmall,
                        color = PolarisBluePrimary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Boardroom Panel Mode Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isBoardroomModeActive) VocalisCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f))
                        .border(1.dp, if (isBoardroomModeActive) VocalisCyan.copy(alpha = 0.6f) else Color.Transparent, RoundedCornerShape(16.dp))
                        .clickable { viewModel.toggleBoardroomMode() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Boardroom Mode",
                            tint = if (isBoardroomModeActive) VocalisCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isBoardroomModeActive) "Panel ON" else "Solo",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBoardroomModeActive) VocalisCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Socratic Stress Interrupter toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isStressModeActive) ScoreRed.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f))
                        .border(1.dp, if (isStressModeActive) ScoreRed.copy(alpha = 0.6f) else Color.Transparent, RoundedCornerShape(16.dp))
                        .clickable { viewModel.toggleStressMode() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Stress Mode",
                            tint = if (isStressModeActive) ScoreRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isStressModeActive) "Stress ON" else "Stress OFF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isStressModeActive) ScoreRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Exchange progress pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(PolarisBluePrimary.copy(alpha = 0.15f))
                        .border(1.dp, PolarisBluePrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$turnIndex / $maxTurns",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolarisBluePrimary
                    )
                }
            }
        }

        // Virtual Boardroom Seating Table (When Panel Mode Active)
        AnimatedVisibility(visible = isBoardroomModeActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0A101D))
                    .border(1.dp, VocalisCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                boardroomPanel.forEachIndexed { index, panelist ->
                    val isInterrogator = index == activePanelistIndex
                    val accentCol = Color(panelist.accentColorHex)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isInterrogator) accentCol else accentCol.copy(alpha = 0.15f))
                                .border(
                                    width = if (isInterrogator) 2.dp else 1.dp,
                                    color = if (isInterrogator) Color.White else accentCol.copy(alpha = 0.4f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = panelist.avatarInitials,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isInterrogator) Color.Black else accentCol
                            )
                        }
                        Text(
                            text = panelist.name.split(" ").lastOrNull() ?: panelist.name,
                            fontSize = 9.sp,
                            fontWeight = if (isInterrogator) FontWeight.Bold else FontWeight.Normal,
                            color = if (isInterrogator) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isInterrogator) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(accentCol)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("SPEAKING", fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        // Persona Voice & Prompt Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(PolarisBluePrimary.copy(alpha = 0.4f), PolarisIndigoSecondary.copy(alpha = 0.2f)))
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val personaInitials = currentSetup?.personaName
                        ?.split(" ")
                        ?.mapNotNull { it.firstOrNull()?.uppercase() }
                        ?.take(2)
                        ?.joinToString("")
                        ?: "EX"

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSpeaking) PolarisBluePrimary
                                    else PolarisBluePrimary.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = personaInitials,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSpeaking) Color.Black else PolarisBluePrimary
                            )
                        }

                        Column {
                            Text(
                                text = currentSetup?.personaName ?: "Executive Interlocutor",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentSetup?.personaRole ?: "${selectedTone.displayName} Evaluator",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // TTS Speak/Mute toggle button
                    IconButton(
                        onClick = {
                            if (isSpeaking) viewModel.stopSpeaking()
                            else viewModel.speakCurrentAiPrompt()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = if (isSpeaking) "Stop voice" else "Read prompt aloud",
                            tint = if (isSpeaking) PolarisAmberGold else PolarisBluePrimary
                        )
                    }
                }

                // AI Spoken Prompt Text
                Text(
                    text = "\"$currentAiPrompt\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Realistic Interlocutor Behavioral Status Pill
                val (statusText, statusColor) = when {
                    isSpeaking -> "Speaking with authentic executive inflection..." to PolarisBluePrimary
                    isEvaluating -> "Analyzing logic structure, composure & cadence..." to PolarisAmberGold
                    isListening -> "Listening attentively to your response..." to ScoreGreen
                    else -> "Awaiting your spoken response" to MaterialTheme.colorScheme.onSurfaceVariant
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(statusColor))
                    Text(text = statusText, fontSize = 10.sp, color = statusColor, fontWeight = FontWeight.Medium)
                }

                // Voice Wave Visualizer when TTS is reading
                VoiceWaveVisualizer(
                    isActive = isSpeaking,
                    isListening = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Executive Front Camera Mirror & Gaze Tracking HUD
        ExecutiveCameraMirror(
            isCameraExpanded = isCameraMirrorExpanded,
            onToggleExpand = { viewModel.toggleCameraMirror() },
            isListening = isListening
        )

        // Live Astra Acoustic & Cognitive Telemetry HUD
        val wordsSpoken = userInputText.split("\\s+".toRegex()).count { it.isNotBlank() }
        NeuralTelemetryHud(
            isListening = isListening,
            currentWordCount = wordsSpoken,
            elapsedSeconds = elapsedSeconds,
            fillerCount = liveFillerCount,
            audioRms = audioRms,
            activeCurveball = activeCurveball,
            curveballSecondsLeft = curveballSecondsLeft,
            modifier = Modifier.fillMaxWidth()
        )

        // Turn-by-Turn Executive Redline & AI Gold Standard Rewrite (When Available)
        latestTurnRedline?.let { redline ->
            ExecutiveRedlineInspector(
                redline = redline,
                onSpeakRewrite = { viewModel.speakExecutiveRewrite(it) }
            )
        }

        // Live Speech Capture / Response Area
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    if (isListening) listOf(PolarisAmberGold, PolarisBluePrimary)
                    else listOf(PolarisDarkBorder, PolarisDarkBorder)
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isListening) "Listening to your voice..." else "Your Spoken Response",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isListening) PolarisAmberGold else MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { isEditingTranscript = !isEditingTranscript },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isEditingTranscript) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = "Edit text",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Transcript display or Editable TextField
                if (isEditingTranscript) {
                    OutlinedTextField(
                        value = userInputText,
                        onValueChange = { viewModel.userInputText.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("manual_response_input"),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Speak or type your answer here...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PolarisBluePrimary,
                            unfocusedBorderColor = PolarisDarkBorder
                        )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(12.dp)
                    ) {
                        if (userInputText.isNotBlank()) {
                            Text(
                                text = userInputText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(
                                text = if (isListening) "Speak out loud now — transcribing in real time..." else "Tap the microphone below and speak out loud, or tap Edit to type.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Voice waveform while user is speaking
                VoiceWaveVisualizer(
                    isActive = isListening,
                    isListening = true,
                    audioRms = audioRms,
                    modifier = Modifier.fillMaxWidth()
                )

                // Microphone Control & Submit Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Big Microphone Button
                    Button(
                        onClick = handleMicToggle,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isListening) PolarisAmberGold else PolarisBluePrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .size(54.dp)
                            .testTag("mic_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isListening) "Stop recording" else "Start recording",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Submit Answer Button
                    Button(
                        onClick = { viewModel.submitTurnResponse() },
                        enabled = userInputText.isNotBlank() && !isEvaluating,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PolarisBluePrimary,
                            contentColor = Color(0xFF0F172A)
                        ),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("submit_response_button")
                    ) {
                        if (isEvaluating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Evaluating Turn...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (turnIndex >= maxTurns) "Submit & Complete" else "Send Answer",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Speech error notice if any
                AnimatedVisibility(visible = speechError != null) {
                    Text(
                        text = speechError ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = PolarisAmberGold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Live Exchange Feedback History Card (Previous turns & tips)
        val completedTurns = turns.filter { it.userSpeechText.isNotBlank() }
        if (completedTurns.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Turn Feedback & In-Character Reactions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                completedTurns.takeLast(2).forEach { turn ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Exchange #${turn.turnIndex}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PolarisBluePrimary
                                )
                                turn.score?.let { score ->
                                    ScoreBadge(score = score)
                                }
                            }

                            Text(
                                text = "Your response: \"${turn.userSpeechText}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            turn.reactionText?.let { reaction ->
                                Text(
                                    text = "Persona reaction: \"$reaction\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            turn.tip?.let { tip ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = PolarisAmberGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = tip,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PolarisAmberGold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Early finish button
        OutlinedButton(
            onClick = { viewModel.finishSessionEarly() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Finish Rehearsal Early & View Scorecard")
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        viewModel.stopSpeaking()
                        viewModel.stopListening()
                        viewModel.navigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Exit Rehearsal")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExitDialog = false }) {
                    Text("Keep Practicing")
                }
            },
            title = { Text("Exit Rehearsal Session?") },
            text = { Text("Are you sure you want to exit? Your ongoing rehearsal progress will be lost unless you complete it.") }
        )
    }

    // Final Score Card Modal / Sheet
    if (showScoreCard && finalScoreCard != null) {
        val scoreCard = finalScoreCard!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissScoreCardAndGoHome() },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.openAstraAnalysis() },
                        colors = ButtonDefaults.buttonColors(containerColor = PolarisIndigoSecondary, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("launch_astra_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PolarisAmberGold, modifier = Modifier.size(18.dp))
                            Text("Astra 6 Neural Diagnostic", fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.dismissScoreCardAndGoHome() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("close_scorecard_button")
                    ) {
                        Text("Done & Save to History")
                    }
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(PolarisAmberGold, PolarisBluePrimary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text("Rehearsal Scorecard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(scoreCard.readinessLevel, style = MaterialTheme.typography.labelSmall, color = ScoreGreen)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Big Score Ring Centerpiece
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "${scoreCard.overallScore}",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PolarisBluePrimary
                            )
                            Text(
                                text = "OUT OF 10",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Verdict Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"${scoreCard.verdict}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Strengths
                    Text("Key Strengths:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = ScoreGreen)
                    scoreCard.strengths.forEach { str ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("✓", color = ScoreGreen, fontWeight = FontWeight.Bold)
                            Text(str, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Areas for Improvement
                    Text("Actionable Improvements:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PolarisAmberGold)
                    scoreCard.areasForImprovement.forEach { tip ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("•", color = PolarisAmberGold, fontWeight = FontWeight.Bold)
                            Text(tip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
