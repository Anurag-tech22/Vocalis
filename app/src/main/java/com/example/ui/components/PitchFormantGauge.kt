package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Executive Acoustic F0 Fundamental Pitch & Formant Gauge.
 * Visualizes voice pitch frequency (80 Hz - 240 Hz), jitter stability, and diaphragmatic warmth.
 */
@Composable
fun PitchFormantGauge(
    pitchHz: Int,
    jitterPercent: Float,
    isAudioActive: Boolean,
    modifier: Modifier = Modifier
) {
    val targetAngle = when {
        !isAudioActive -> 180f
        else -> {
            val normalized = ((pitchHz - 80).toFloat() / (240 - 80)).coerceIn(0f, 1f)
            180f + (normalized * 180f)
        }
    }

    val animatedAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 280f),
        label = "pitch_needle_angle"
    )

    val (rangeLabel, rangeColor) = when {
        !isAudioActive -> "AMBIENT ROOM" to VocalisTextMuted
        pitchHz < 125 -> "DEEP GRAVITAS / CHEST RESONANCE" to VocalisAmber
        pitchHz in 125..185 -> "EXECUTIVE CLARITY / OPTIMAL BAND" to VocalisEmerald
        pitchHz in 186..225 -> "DYNAMIC PROJECTION / EMPHASIS" to VocalisIndigo
        else -> "ELEVATED STRAIN / TENSION" to VocalisCrimson
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pitch_formant_gauge_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = VocalisSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(VocalisCardBorder, VocalisCardBorder))
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = VocalisEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "F0 FUNDAMENTAL PITCH & JITTER RADAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VocalisEmerald
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(rangeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = rangeLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = rangeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Gauge Arc and Needle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(240.dp, 130.dp)) {
                    val width = size.width
                    val height = size.height
                    val center = Offset(width / 2f, height * 0.88f)
                    val radius = height * 0.76f

                    // Background Arc Track (180 to 360 degrees)
                    val arcRect = Size(radius * 2f, radius * 2f)
                    val topLeft = Offset(center.x - radius, center.y - radius)

                    // 4 Colored Zone Arcs
                    // Zone 1: 80 - 125 Hz (Deep Chest: Amber) -> 180 to 225 deg
                    drawArc(
                        color = VocalisAmber.copy(alpha = if (isAudioActive) 0.85f else 0.25f),
                        startAngle = 180f,
                        sweepAngle = 50f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcRect,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Zone 2: 125 - 185 Hz (Executive Optimal: Emerald) -> 230 to 295 deg
                    drawArc(
                        color = VocalisEmerald.copy(alpha = if (isAudioActive) 0.95f else 0.35f),
                        startAngle = 232f,
                        sweepAngle = 64f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcRect,
                        style = Stroke(width = 11.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Zone 3: 185 - 225 Hz (Dynamic Accent: Amethyst) -> 298 to 338 deg
                    drawArc(
                        color = VocalisIndigo.copy(alpha = if (isAudioActive) 0.85f else 0.25f),
                        startAngle = 298f,
                        sweepAngle = 40f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcRect,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Zone 4: >225 Hz (Vocal Strain: Crimson) -> 340 to 360 deg
                    drawArc(
                        color = VocalisCrimson.copy(alpha = if (isAudioActive) 0.85f else 0.25f),
                        startAngle = 340f,
                        sweepAngle = 20f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcRect,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Needle Calculation
                    val rad = (animatedAngle * PI / 180f).toFloat()
                    val needleLen = radius * 0.82f
                    val needleEnd = Offset(
                        center.x + needleLen * cos(rad),
                        center.y + needleLen * sin(rad)
                    )

                    // Needle Line
                    drawLine(
                        color = if (isAudioActive) Color.White else VocalisTextMuted,
                        start = center,
                        end = needleEnd,
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Pivot Center Dot
                    drawCircle(
                        color = if (isAudioActive) VocalisEmerald else VocalisTextMuted,
                        radius = 6.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = VocalisObsidian,
                        radius = 2.5.dp.toPx(),
                        center = center
                    )
                }

                // Center Digital Readout
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isAudioActive) "$pitchHz Hz" else "-- Hz",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isAudioActive) VocalisTextPrimary else VocalisTextMuted
                    )
                    Text(
                        text = "Vocal Pitch (F0)",
                        fontSize = 9.sp,
                        color = VocalisTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-metrics (Jitter %, Vocal Warmth, Pitch Target)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VocalisSurfaceElevated)
                        .padding(8.dp)
                ) {
                    Column {
                        Text("Vocal Jitter", fontSize = 9.sp, color = VocalisTextMuted)
                        Text(
                            text = if (isAudioActive) "${jitterPercent}%" else "0.38%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (jitterPercent < 1.0f) VocalisEmerald else VocalisAmber
                        )
                        Text("< 1.0% Presidential", fontSize = 8.sp, color = VocalisTextSecondary)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VocalisSurfaceElevated)
                        .padding(8.dp)
                ) {
                    Column {
                        Text("Harmonic Formant", fontSize = 9.sp, color = VocalisTextMuted)
                        Text(
                            text = if (isAudioActive) "2.8 kHz" else "2.6 kHz",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = VocalisIndigo
                        )
                        Text("Singer's Ring / Cut", fontSize = 8.sp, color = VocalisTextSecondary)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VocalisSurfaceElevated)
                        .padding(8.dp)
                ) {
                    Column {
                        Text("Target Zone", fontSize = 9.sp, color = VocalisTextMuted)
                        Text(
                            text = "125-180 Hz",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = VocalisAmber
                        )
                        Text("Executive Center", fontSize = 8.sp, color = VocalisTextSecondary)
                    }
                }
            }
        }
    }
}
