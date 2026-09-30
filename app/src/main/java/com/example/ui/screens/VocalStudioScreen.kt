package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Screen
import com.example.ui.components.KeynoteTeleprompterSection
import com.example.ui.components.PitchFormantGauge
import com.example.ui.components.RealtimeWaveformVisualizer
import com.example.ui.theme.VocalisAmber
import com.example.ui.theme.VocalisCardBorder
import com.example.ui.theme.VocalisCrimson
import com.example.ui.theme.VocalisCyan
import com.example.ui.theme.VocalisEmerald
import com.example.ui.theme.VocalisGlassBorder
import com.example.ui.theme.VocalisIndigo
import com.example.ui.theme.VocalisObsidian
import com.example.ui.theme.VocalisSky
import com.example.ui.theme.VocalisSurface
import com.example.ui.theme.VocalisSurfaceElevated
import com.example.ui.theme.VocalisTextMuted
import com.example.ui.theme.VocalisTextPrimary
import com.example.ui.theme.VocalisTextSecondary
import com.example.viewmodel.RehearsalViewModel

private val TestPhrases = listOf(
    "Good morning executive committee. Today, I am proposing a structural architectural shift to eliminate cascading microservice latency across global regions.",
    "Our distributed consensus protocol guarantees strict serializability with sub-millisecond p99 latencies under extreme network partition tests.",
    "I appreciate your pushback on the deployment timeline. Here is our phased risk mitigation strategy to ensure zero customer downtime during the migration."
)

private val PhraseLabels = listOf(
    "Executive Authority",
    "Technical Precision",
    "Composure Under Fire"
)

@Composable
fun VocalStudioScreen(
    viewModel: RehearsalViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.stopStudioRecording()
        viewModel.navigateBack()
    }

    val isRecording by viewModel.isStudioRecording.collectAsState()
    val isSimulated by viewModel.isSimulatedStreamActive.collectAsState()
    val audioRms by viewModel.studioAudioRms.collectAsState()
    val decibels by viewModel.studioDecibels.collectAsState()
    val wpm by viewModel.studioWpm.collectAsState()
    val pitchHz by viewModel.studioPitchHz.collectAsState()
    val jitterPercent by viewModel.studioJitterPercent.collectAsState()
    val resonance by viewModel.vocalResonanceScore.collectAsState()
    val stability by viewModel.diaphragmaticStability.collectAsState()
    val clarity by viewModel.vocalClarityScore.collectAsState()
    val selectedPhraseIdx by viewModel.studioSelectedPhraseIndex.collectAsState()
    val transcript by viewModel.studioTranscript.collectAsState()

    var activeStudioTab by androidx.compose.runtime.remember { androidx.compose.runtime.mutableIntStateOf(0) } // 0: Acoustic DSP Lab, 1: Keynote Teleprompter

    val isAudioActive = isRecording || isSimulated

    val infiniteTransition = rememberInfiniteTransition(label = "studio_halo")
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_scale"
    )

    Scaffold(
        containerColor = VocalisObsidian,
        topBar = {
            StudioTopBar(
                isLive = isAudioActive,
                onBack = {
                    viewModel.stopStudioRecording()
                    viewModel.navigateBack()
                }
            )
        },
        modifier = modifier.testTag("vocal_studio_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Segmented Studio Mode Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VocalisSurfaceElevated)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeStudioTab == 0) VocalisEmerald else Color.Transparent)
                            .clickable { activeStudioTab = 0 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Acoustic DSP Lab",
                            fontSize = 12.sp,
                            fontWeight = if (activeStudioTab == 0) FontWeight.Black else FontWeight.Medium,
                            color = if (activeStudioTab == 0) Color.Black else VocalisTextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeStudioTab == 1) VocalisEmerald else Color.Transparent)
                            .clickable { activeStudioTab = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Keynote Teleprompter",
                            fontSize = 12.sp,
                            fontWeight = if (activeStudioTab == 1) FontWeight.Black else FontWeight.Medium,
                            color = if (activeStudioTab == 1) Color.Black else VocalisTextSecondary
                        )
                    }
                }
            }

            if (activeStudioTab == 0) {
                // Tab 0: Acoustic DSP Lab
                item {
                    // 1. Hero Acoustic Wave Chamber
                    WaveformChamberCard(
                        audioRms = audioRms,
                        isAudioActive = isAudioActive,
                        decibels = decibels
                    )
                }

                item {
                    // 2. Audio Input Source Selector & Live Mic Trigger
                    AudioSourceController(
                        isRecording = isRecording,
                        isSimulated = isSimulated,
                        haloPulse = haloPulse,
                        onToggleMic = { viewModel.toggleStudioRecording() },
                        onToggleSimulated = { viewModel.toggleStudioSimulatedStream() }
                    )
                }

                item {
                    // 3. F0 Fundamental Pitch & Jitter Radar Gauge
                    PitchFormantGauge(
                        pitchHz = pitchHz,
                        jitterPercent = jitterPercent,
                        isAudioActive = isAudioActive
                    )
                }

                item {
                    // 4. Dynamic Real-Time 5-Band Vocal Resonance EQ Spectrum
                    AcousticSpectrumCard(
                        audioRms = audioRms,
                        isAudioActive = isAudioActive
                    )
                }

                item {
                    // 5. Vocal Performance Telemetry Matrix (4 Core Pillars)
                    VocalPerformanceMatrix(
                        resonance = resonance,
                        stability = stability,
                        clarity = clarity,
                        wpm = wpm
                    )
                }

                item {
                    // 6. Guided Vocal Calibration Prompts
                    CalibrationPromptsCard(
                        selectedIndex = selectedPhraseIdx,
                        onSelect = { viewModel.selectStudioTestPhrase(it) },
                        transcript = transcript,
                        isListening = isAudioActive
                    )
                }
            } else {
                // Tab 1: Keynote Teleprompter
                item {
                    KeynoteTeleprompterSection(
                        isAudioRecording = isRecording,
                        onToggleRecording = { viewModel.toggleStudioRecording() }
                    )
                }

                item {
                    // Compact Acoustic Waveform underneath prompter
                    WaveformChamberCard(
                        audioRms = audioRms,
                        isAudioActive = isAudioActive,
                        decibels = decibels
                    )
                }

                item {
                    PitchFormantGauge(
                        pitchHz = pitchHz,
                        jitterPercent = jitterPercent,
                        isAudioActive = isAudioActive
                    )
                }
            }

            item {
                // Action CTAs to launch rehearsal or Astra 6 diagnostic
                StudioActionButtons(
                    onStartRehearsal = {
                        viewModel.stopStudioRecording()
                        viewModel.navigateTo(Screen.Practice)
                    },
                    onOpenAstraDiagnostic = {
                        viewModel.stopStudioRecording()
                        viewModel.openAstraAnalysis()
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun StudioTopBar(
    isLive: Boolean,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VocalisSurface)
                    .border(1.dp, VocalisCardBorder, CircleShape)
                    .testTag("studio_back_button")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = VocalisTextPrimary
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Vocal Studio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = VocalisTextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VocalisCyan.copy(alpha = 0.15f))
                            .border(1.dp, VocalisCyan.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "DSP LAB",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = VocalisCyan
                        )
                    }
                }
                Text(
                    text = "Real-time acoustic analysis & wave telemetry",
                    style = MaterialTheme.typography.labelSmall,
                    color = VocalisTextSecondary
                )
            }
        }

        // Live DSP Pulse Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isLive) VocalisEmerald.copy(alpha = 0.15f) else VocalisSurfaceElevated)
                .border(
                    1.dp,
                    if (isLive) VocalisEmerald.copy(alpha = 0.5f) else VocalisCardBorder,
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isLive) VocalisEmerald else VocalisTextMuted)
                )
                Text(
                    text = if (isLive) "DSP ACTIVE" else "IDLE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isLive) VocalisEmerald else VocalisTextMuted
                )
            }
        }
    }
}

@Composable
private fun WaveformChamberCard(
    audioRms: Float,
    isAudioActive: Boolean,
    decibels: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VocalisSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(
                    if (isAudioActive) VocalisCyan.copy(alpha = 0.5f) else VocalisCardBorder,
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
            // Chamber Sub-header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = VocalisCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "MULTI-HARMONIC ACOUSTIC WAVEFORM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = VocalisCyan
                    )
                }

                Text(
                    text = "44.1 kHz • 24-BIT FLOATING DSP",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = VocalisTextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // The Canvas Wave Visualizer
            RealtimeWaveformVisualizer(
                audioLevel = audioRms,
                isAudioActive = isAudioActive,
                height = 160.dp,
                accentColor = VocalisCyan,
                secondaryColor = VocalisIndigo
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Live Telemetry Bar underneath wave
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // dBFS Readout
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${decibels.toInt()} dBFS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = if (decibels > -6f) VocalisCrimson else if (decibels > -18f) VocalisEmerald else VocalisSky
                    )

                    val statusText = when {
                        !isAudioActive -> "AMBIENT ROOM"
                        decibels > -6f -> "CLIPPING WARNING"
                        decibels > -20f -> "EXECUTIVE PROJECTION"
                        else -> "SOFT INTAKE"
                    }
                    val statusColor = when {
                        !isAudioActive -> VocalisTextMuted
                        decibels > -6f -> VocalisCrimson
                        decibels > -20f -> VocalisEmerald
                        else -> VocalisAmber
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                // RMS Energy Bar Gauge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val segments = 8
                    val filled = ((audioRms.coerceIn(0f, 1f)) * segments).toInt()
                    for (i in 0 until segments) {
                        val isLit = isAudioActive && (i <= filled)
                        val color = if (i >= 6) VocalisCrimson else if (i >= 4) VocalisAmber else VocalisEmerald
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(if (isLit) color else VocalisCardBorder)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioSourceController(
    isRecording: Boolean,
    isSimulated: Boolean,
    haloPulse: Float,
    onToggleMic: () -> Unit,
    onToggleSimulated: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = VocalisSurfaceElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VocalisCardBorder, VocalisCardBorder)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AUDIO INPUT SOURCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VocalisTextMuted
                )

                // Simulated Acoustic Feed Toggle
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSimulated) VocalisIndigo.copy(alpha = 0.2f) else VocalisSurface)
                        .border(1.dp, if (isSimulated) VocalisIndigo else VocalisCardBorder, RoundedCornerShape(8.dp))
                        .clickable { onToggleSimulated() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (isSimulated) VocalisCyan else VocalisTextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (isSimulated) "Simulated Feed: ON" else "Simulate Voice",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSimulated) VocalisCyan else VocalisTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Big Central Studio Microphone Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isRecording) "Recording Studio Stream" else if (isSimulated) "Synthesizing Vocal Field" else "Microphone Standby",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = VocalisTextPrimary
                    )
                    Text(
                        text = if (isRecording) "Tap to pause capture" else "Tap to calibrate voice dynamics",
                        style = MaterialTheme.typography.bodySmall,
                        color = VocalisTextSecondary
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(64.dp)
                ) {
                    // Pulsing halo when active
                    if (isRecording || isSimulated) {
                        Box(
                            modifier = Modifier
                                .size((60 * haloPulse).dp)
                                .clip(CircleShape)
                                .background(VocalisCyan.copy(alpha = 0.25f))
                        )
                    }

                    IconButton(
                        onClick = onToggleMic,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                if (isRecording) Brush.linearGradient(listOf(VocalisCrimson, VocalisAmber))
                                else Brush.linearGradient(listOf(VocalisCyan, VocalisSky))
                            )
                            .testTag("studio_mic_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Toggle Studio Microphone",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AcousticSpectrumCard(
    audioRms: Float,
    isAudioActive: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = VocalisSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VocalisCardBorder, VocalisCardBorder)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = VocalisIndigo, modifier = Modifier.size(16.dp))
                    Text(
                        text = "5-BAND HARMONIC RESONANCE SPECTRUM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VocalisIndigo
                    )
                }
                Text("REAL-TIME FFT", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = VocalisTextMuted)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5 Interactive Frequency Bands
            val bands = listOf(
                Triple("60 Hz", "Sub", 0.45f),
                Triple("250 Hz", "Body", 0.85f),
                Triple("800 Hz", "Warmth", 0.95f),
                Triple("2.5 kHz", "Presence", 0.75f),
                Triple("8 kHz", "Air", 0.55f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                bands.forEachIndexed { i, (freq, name, weight) ->
                    val bandLevel = if (isAudioActive) {
                        ((audioRms * weight + (i * 0.08f)) * 0.9f).coerceIn(0.12f, 1f)
                    } else {
                        0.10f
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height((60 * bandLevel).dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            VocalisCyan,
                                            VocalisIndigo.copy(alpha = 0.5f)
                                        )
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = freq, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = VocalisTextPrimary)
                        Text(text = name, fontSize = 8.sp, color = VocalisTextMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun VocalPerformanceMatrix(
    resonance: Int,
    stability: Int,
    clarity: Int,
    wpm: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "ACOUSTIC PERFORMANCE PILLARS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = VocalisTextMuted
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricPillarCard(
                title = "Vocal Resonance",
                value = "$resonance%",
                descriptor = "Diaphragmatic Depth",
                accentColor = VocalisCyan,
                modifier = Modifier.weight(1f)
            )
            MetricPillarCard(
                title = "Formant Stability",
                value = "$stability%",
                descriptor = "Low Jitter Anchor",
                accentColor = VocalisEmerald,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricPillarCard(
                title = "Speech Cadence",
                value = "$wpm WPM",
                descriptor = "Optimal Executive Band",
                accentColor = VocalisSky,
                modifier = Modifier.weight(1f)
            )
            MetricPillarCard(
                title = "Harmonic Clarity",
                value = "$clarity%",
                descriptor = "Zero Vocal Fry",
                accentColor = VocalisIndigo,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricPillarCard(
    title: String,
    value: String,
    descriptor: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = VocalisSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VocalisCardBorder, VocalisCardBorder)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(text = title, fontSize = 11.sp, color = VocalisTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = descriptor, fontSize = 9.sp, color = VocalisTextMuted)
        }
    }
}

@Composable
private fun CalibrationPromptsCard(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    transcript: String,
    isListening: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = VocalisSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VocalisCardBorder, VocalisCardBorder)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = VocalisAmber, modifier = Modifier.size(16.dp))
                Text(
                    text = "CALIBRATION TEST PROMPTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VocalisAmber
                )
            }
            Text(
                text = "Read aloud into the microphone to measure your resonance curve:",
                fontSize = 12.sp,
                color = VocalisTextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
            )

            // Prompt Selector Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TestPhrases.indices.forEach { index ->
                    val isSelected = selectedIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) VocalisAmber.copy(alpha = 0.2f) else VocalisSurfaceElevated)
                            .border(1.dp, if (isSelected) VocalisAmber else VocalisCardBorder, RoundedCornerShape(8.dp))
                            .clickable { onSelect(index) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = PhraseLabels[index],
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            color = if (isSelected) VocalisAmber else VocalisTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // The Current Prompt to Read
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VocalisObsidian)
                    .border(1.dp, VocalisCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "\"${TestPhrases.getOrElse(selectedIndex) { TestPhrases[0] }}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = VocalisTextPrimary,
                    lineHeight = 20.sp
                )
            }

            // Real-Time Captured Speech Transcript Box
            AnimatedVisibility(
                visible = transcript.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "REAL-TIME TRANSCRIPTION STREAM:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = VocalisEmerald
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VocalisEmerald.copy(alpha = 0.1f))
                            .border(1.dp, VocalisEmerald.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = transcript,
                            style = MaterialTheme.typography.bodySmall,
                            color = VocalisTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioActionButtons(
    onStartRehearsal: () -> Unit,
    onOpenAstraDiagnostic: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onStartRehearsal,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("apply_calibration_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VocalisCyan,
                contentColor = Color.Black
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = "Launch Rehearsal with this Calibration",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }

        Button(
            onClick = onOpenAstraDiagnostic,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("open_astra_from_studio_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VocalisSurfaceElevated,
                contentColor = VocalisTextPrimary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = VocalisIndigo, modifier = Modifier.size(16.dp))
                Text(
                    text = "View 6-Axis Neural Diagnostic Matrix",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
