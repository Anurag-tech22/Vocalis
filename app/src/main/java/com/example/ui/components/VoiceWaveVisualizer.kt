package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.VocalisAmber
import com.example.ui.theme.VocalisCardBorder
import com.example.ui.theme.VocalisCyan
import com.example.ui.theme.VocalisGlassBorder
import com.example.ui.theme.VocalisIndigo
import com.example.ui.theme.VocalisObsidian
import com.example.ui.theme.VocalisSky
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

/**
 * Modern fluid multi-harmonic acoustic waveform visualizer for real-time speech capture.
 * Automatically adapts between ambient idle breathing and dynamic speech resonance.
 */
@Composable
fun VoiceWaveVisualizer(
    isActive: Boolean,
    isListening: Boolean,
    audioRms: Float = 0f,
    modifier: Modifier = Modifier,
    barCount: Int = 7,
    maxHeight: Dp = 42.dp
) {
    val effectiveLevel = when {
        isListening && audioRms > 0.01f -> audioRms.coerceIn(0f, 1f)
        isActive -> 0.18f
        else -> 0.03f
    }

    val smoothLevel by animateFloatAsState(
        targetValue = effectiveLevel,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 350f),
        label = "voice_wave_rms"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "mini_wave_time")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val primaryColor = if (isListening) VocalisCyan else VocalisSky
    val secondaryColor = if (isListening) VocalisAmber else VocalisIndigo

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight)
            .clip(RoundedCornerShape(10.dp))
            .background(VocalisObsidian.copy(alpha = 0.85f))
            .border(
                width = 1.dp,
                color = if (isListening) VocalisGlassBorder else VocalisCardBorder.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .testTag("voice_wave_visualizer")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f

            // Baseline subtle guide line
            drawLine(
                color = primaryColor.copy(alpha = if (isActive) 0.25f else 0.10f),
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = 1.dp.toPx()
            )

            val maxAmp = (height * 0.42f) * (0.12f + smoothLevel * 0.88f)
            val stepCount = 50

            // Harmonic secondary shadow wave
            val shadowPath = Path()
            for (i in 0..stepCount) {
                val u = i.toFloat() / stepCount
                val x = u * width
                val window = sin(u * PI).toFloat().pow(0.75f)
                val s = sin(2 * PI * 2.2f * u - phase * 0.8f).toFloat()
                val y = centerY - s * maxAmp * 0.65f * window
                if (i == 0) shadowPath.moveTo(x, y) else shadowPath.lineTo(x, y)
            }
            drawPath(
                path = shadowPath,
                color = secondaryColor.copy(alpha = if (isActive) 0.4f else 0.15f),
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Primary active wave
            val primaryTopPath = Path()
            val primaryBottomPath = Path()
            val fillPath = Path()

            for (i in 0..stepCount) {
                val u = i.toFloat() / stepCount
                val x = u * width
                val window = sin(u * PI).toFloat().pow(0.85f)
                val s1 = sin(2 * PI * 3.0f * u + phase).toFloat()
                val s2 = sin(2 * PI * 5.5f * u - phase * 0.5f).toFloat() * 0.3f
                val s = (s1 + s2) / 1.3f
                val displacement = s * maxAmp * window
                val yTop = centerY - displacement
                val yBottom = centerY + displacement

                if (i == 0) {
                    primaryTopPath.moveTo(x, yTop)
                    primaryBottomPath.moveTo(x, yBottom)
                    fillPath.moveTo(x, yTop)
                } else {
                    primaryTopPath.lineTo(x, yTop)
                    primaryBottomPath.lineTo(x, yBottom)
                    fillPath.lineTo(x, yTop)
                }
            }

            for (i in stepCount downTo 0) {
                val u = i.toFloat() / stepCount
                val x = u * width
                val window = sin(u * PI).toFloat().pow(0.85f)
                val s1 = sin(2 * PI * 3.0f * u + phase).toFloat()
                val s2 = sin(2 * PI * 5.5f * u - phase * 0.5f).toFloat() * 0.3f
                val displacement = ((s1 + s2) / 1.3f) * maxAmp * window
                fillPath.lineTo(x, centerY + displacement)
            }
            fillPath.close()

            // Fill gradient between mirrored waves
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    listOf(
                        primaryColor.copy(alpha = if (isActive) 0.25f else 0.06f),
                        Color.Transparent
                    ),
                    startY = centerY - maxAmp,
                    endY = centerY + maxAmp
                )
            )

            // Primary strokes
            drawPath(
                path = primaryTopPath,
                color = if (isActive) primaryColor else primaryColor.copy(alpha = 0.4f),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = primaryBottomPath,
                color = if (isActive) primaryColor.copy(alpha = 0.5f) else primaryColor.copy(alpha = 0.2f),
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}
