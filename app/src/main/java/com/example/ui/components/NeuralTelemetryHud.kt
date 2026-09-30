package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StressCurveball
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.ScoreAmber
import com.example.ui.theme.ScoreGreen
import com.example.ui.theme.ScoreRed

@Composable
fun NeuralTelemetryHud(
    isListening: Boolean,
    currentWordCount: Int,
    elapsedSeconds: Int,
    fillerCount: Int,
    audioRms: Float,
    activeCurveball: StressCurveball?,
    curveballSecondsLeft: Int,
    modifier: Modifier = Modifier
) {
    val durationSafe = if (elapsedSeconds > 0) elapsedSeconds else 1
    val calculatedWpm = ((currentWordCount.toFloat() / durationSafe.toFloat()) * 60f).toInt()
    val displayWpm = if (isListening && currentWordCount > 2) calculatedWpm else 135

    val (wpmColor, wpmLabel) = when {
        displayWpm in 125..160 -> ScoreGreen to "OPTIMAL PACE"
        displayWpm > 160 -> ScoreRed to "RUSHED / ANXIOUS"
        else -> PolarisBluePrimary to "MEASURED / DELIBERATE"
    }

    // Pulse animation for high tension or curveball
    val infiniteTransition = rememberInfiniteTransition(label = "hud_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0B132B).copy(alpha = 0.92f))
            .border(
                width = if (activeCurveball != null) 2.dp else 1.dp,
                color = if (activeCurveball != null) ScoreRed.copy(alpha = pulseAlpha) else PolarisBluePrimary.copy(alpha = 0.35f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Telemetry Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(if (isListening) ScoreGreen else Color.Gray, CircleShape)
                )
                Text(
                    text = if (isListening) "ASTRA LIVE TELEMETRY [ACTIVE]" else "ASTRA TELEMETRY HUD [STANDBY]",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = if (isListening) ScoreGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${durationSafe}s elapsed",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Curveball Alert Banner
        AnimatedVisibility(visible = activeCurveball != null) {
            activeCurveball?.let { cb ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ScoreRed.copy(alpha = 0.2f))
                        .border(1.5.dp, ScoreRed.copy(alpha = pulseAlpha), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Bolt, contentDescription = "Interruption", tint = ScoreRed, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "SOCRATIC PRESSURE INJECTION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ScoreRed
                                )
                            }
                            Text(
                                text = "${curveballSecondsLeft}s Left!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = ScoreRed
                            )
                        }
                        Text(
                            text = cb.triggerPrompt,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Live Metric Counters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // WPM Gauge
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(8.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Speed, contentDescription = "WPM", tint = wpmColor, modifier = Modifier.size(14.dp))
                        Text(text = "PACING", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        text = "$displayWpm WPM",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = wpmColor
                    )
                    Text(text = wpmLabel, fontSize = 8.sp, color = wpmColor)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Filler Counter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(8.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Fillers",
                            tint = if (fillerCount > 0) ScoreAmber else ScoreGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(text = "FILLERS", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        text = "$fillerCount detected",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = if (fillerCount > 0) ScoreAmber else ScoreGreen
                    )
                    Text(
                        text = if (fillerCount == 0) "Zero Clutter" else "Active Hedges",
                        fontSize = 8.sp,
                        color = if (fillerCount > 0) ScoreAmber else ScoreGreen
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Acoustic Resonance Level
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(8.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Waves, contentDescription = "Acoustics", tint = PolarisBluePrimary, modifier = Modifier.size(14.dp))
                        Text(text = "RESONANCE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        text = "${audioRms.toInt()} dB",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = PolarisBluePrimary
                    )
                    Text(
                        text = if (isListening) "Capturing Audio" else "Idle",
                        fontSize = 8.sp,
                        color = PolarisBluePrimary
                    )
                }
            }
        }
    }
}
