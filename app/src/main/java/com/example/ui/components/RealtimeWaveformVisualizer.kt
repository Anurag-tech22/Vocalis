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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.VocalisAmber
import com.example.ui.theme.VocalisCardBorder
import com.example.ui.theme.VocalisCyan
import com.example.ui.theme.VocalisEmerald
import com.example.ui.theme.VocalisGlassBorder
import com.example.ui.theme.VocalisIndigo
import com.example.ui.theme.VocalisObsidian
import com.example.ui.theme.VocalisSky
import com.example.ui.theme.VocalisSurface
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

/**
 * High-precision, real-time multi-harmonic acoustic wave visualizer.
 * Synthesizes multi-frequency Bézier waves with dynamic amplitude driven by live audio RMS levels.
 */
@Composable
fun RealtimeWaveformVisualizer(
    audioLevel: Float, // 0.0f .. 1.0f
    isAudioActive: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp,
    showGrid: Boolean = true,
    showCrestNodes: Boolean = true,
    accentColor: Color = VocalisCyan,
    secondaryColor: Color = VocalisIndigo
) {
    // Smooth audio level response with spring physics to prevent jarring jumps
    val smoothAudioLevel by animateFloatAsState(
        targetValue = if (isAudioActive) audioLevel.coerceIn(0f, 1f) else 0.04f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 300f),
        label = "smooth_audio_rms"
    )

    // Continuous time oscillator for fluid organic wave motion
    val infiniteTransition = rememberInfiniteTransition(label = "acoustic_wave_time")
    
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_primary"
    )

    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -(2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_harmonic"
    )

    val breathingPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        VocalisSurface,
                        VocalisObsidian
                    )
                )
            )
            .border(1.dp, if (isAudioActive) VocalisGlassBorder else VocalisCardBorder, RoundedCornerShape(16.dp))
            .testTag("realtime_waveform_visualizer")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val heightPx = size.height
            val centerY = heightPx / 2f

            if (showGrid) {
                drawAcousticGrid(width, heightPx, centerY, isAudioActive)
            }

            // Calculate base vs dynamic amplitude
            val baselineAmp = heightPx * 0.08f * breathingPulse
            val dynamicAmp = heightPx * 0.38f * smoothAudioLevel
            val totalMaxAmp = (baselineAmp + dynamicAmp).coerceAtMost(heightPx * 0.46f)

            // Layer 1: Ambient Resonance Fill (Harmonic Sub-wave with gradient wash)
            drawHarmonicWave(
                width = width,
                centerY = centerY,
                amplitude = totalMaxAmp * 0.65f,
                frequency = 1.8f,
                phase = phase2,
                fillBrush = Brush.verticalGradient(
                    listOf(
                        secondaryColor.copy(alpha = if (isAudioActive) 0.30f else 0.10f),
                        Color.Transparent
                    ),
                    startY = centerY - totalMaxAmp * 0.8f,
                    endY = centerY + totalMaxAmp * 0.8f
                ),
                strokeColor = secondaryColor.copy(alpha = if (isAudioActive) 0.55f else 0.25f),
                strokeWidth = 2.dp.toPx()
            )

            // Layer 2: Vocal Presence Harmonic Wave
            drawHarmonicWave(
                width = width,
                centerY = centerY,
                amplitude = totalMaxAmp * 0.85f,
                frequency = 2.4f,
                phase = phase1 * 0.7f,
                fillBrush = null,
                strokeColor = VocalisEmerald.copy(alpha = if (isAudioActive) 0.6f else 0.2f),
                strokeWidth = 1.5.dp.toPx()
            )

            // Layer 3: Primary Voice Fundamental Wave (Crisp Electric Cyan with dual mirror)
            val crestPoints = mutableListOf<Offset>()
            drawHarmonicWave(
                width = width,
                centerY = centerY,
                amplitude = totalMaxAmp,
                frequency = 3.1f,
                phase = phase1,
                fillBrush = Brush.verticalGradient(
                    listOf(
                        accentColor.copy(alpha = if (isAudioActive) 0.22f else 0.08f),
                        Color.Transparent
                    ),
                    startY = centerY - totalMaxAmp,
                    endY = centerY + totalMaxAmp
                ),
                strokeColor = if (isAudioActive) accentColor else accentColor.copy(alpha = 0.5f),
                strokeWidth = 3.dp.toPx(),
                collectCrests = if (showCrestNodes && isAudioActive) crestPoints else null
            )

            // Layer 4: Glowing Crest Nodes riding the acoustic peaks
            if (showCrestNodes && isAudioActive && smoothAudioLevel > 0.08f) {
                crestPoints.forEach { point ->
                    // Outer glow
                    drawCircle(
                        color = accentColor.copy(alpha = 0.35f),
                        radius = 6.dp.toPx(),
                        center = point
                    )
                    // Inner node
                    drawCircle(
                        color = Color.White,
                        radius = 2.5.dp.toPx(),
                        center = point
                    )
                }
            }

            // Center zero-crossing baseline glow
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        accentColor.copy(alpha = if (isAudioActive) 0.5f else 0.2f),
                        Color.Transparent
                    )
                ),
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

/**
 * Draws precision acoustic measurement grid lines (dBFS reference and frequency guides).
 */
private fun DrawScope.drawAcousticGrid(
    width: Float,
    height: Float,
    centerY: Float,
    isAudioActive: Boolean
) {
    val gridColor = if (isAudioActive) VocalisCyan.copy(alpha = 0.07f) else Color.White.copy(alpha = 0.04f)
    
    // Vertical frequency markers (5 sections)
    val columns = 6
    for (i in 1 until columns) {
        val x = width * (i.toFloat() / columns)
        drawLine(
            color = gridColor,
            start = Offset(x, 12f),
            end = Offset(x, height - 12f),
            strokeWidth = 1f
        )
    }

    // Horizontal dB lines (-6dB, -18dB)
    val upperOffset1 = height * 0.22f
    val upperOffset2 = height * 0.36f
    
    drawLine(
        color = gridColor,
        start = Offset(0f, centerY - upperOffset1),
        end = Offset(width, centerY - upperOffset1),
        strokeWidth = 1f
    )
    drawLine(
        color = gridColor,
        start = Offset(0f, centerY + upperOffset1),
        end = Offset(width, centerY + upperOffset1),
        strokeWidth = 1f
    )
    drawLine(
        color = gridColor,
        start = Offset(0f, centerY - upperOffset2),
        end = Offset(width, centerY - upperOffset2),
        strokeWidth = 1f
    )
    drawLine(
        color = gridColor,
        start = Offset(0f, centerY + upperOffset2),
        end = Offset(width, centerY + upperOffset2),
        strokeWidth = 1f
    )
}

/**
 * Draws a harmonic synthesized sinusoidal wave with window tapering at bounds.
 */
private fun DrawScope.drawHarmonicWave(
    width: Float,
    centerY: Float,
    amplitude: Float,
    frequency: Float,
    phase: Float,
    fillBrush: Brush?,
    strokeColor: Color,
    strokeWidth: Float,
    collectCrests: MutableList<Offset>? = null
) {
    val stepCount = 80
    val topPath = Path()
    val bottomPath = Path()
    val closedFillPath = Path()

    var lastTopY = centerY
    var isRising = false
    var prevDy = 0f

    for (i in 0..stepCount) {
        val u = i.toFloat() / stepCount
        val x = u * width

        // Hann window function to gracefully ground wave at both horizontal edges
        val window = sin(u * PI).toFloat().pow(0.85f)

        // Superposition of fundamental + 2nd overtone harmonic
        val sin1 = sin(2 * PI * frequency * u + phase).toFloat()
        val sin2 = sin(4 * PI * frequency * u * 0.6f - phase * 0.5f).toFloat() * 0.35f
        val composite = (sin1 + sin2) / 1.35f

        val displacement = composite * amplitude * window
        val yTop = centerY - displacement
        val yBottom = centerY + displacement

        if (i == 0) {
            topPath.moveTo(x, yTop)
            bottomPath.moveTo(x, yBottom)
            closedFillPath.moveTo(x, yTop)
        } else {
            topPath.lineTo(x, yTop)
            bottomPath.lineTo(x, yBottom)
            closedFillPath.lineTo(x, yTop)
        }

        // Detect local peaks for glowing crest nodes
        val dy = yTop - lastTopY
        if (prevDy < 0 && dy >= 0 && window > 0.3f && abs(displacement) > amplitude * 0.45f) {
            collectCrests?.add(Offset(x, lastTopY))
        }
        prevDy = dy
        lastTopY = yTop
    }

    // Complete the fill path along bottom mirror
    for (i in stepCount downTo 0) {
        val u = i.toFloat() / stepCount
        val x = u * width
        val window = sin(u * PI).toFloat().pow(0.85f)
        val sin1 = sin(2 * PI * frequency * u + phase).toFloat()
        val sin2 = sin(4 * PI * frequency * u * 0.6f - phase * 0.5f).toFloat() * 0.35f
        val displacement = (sin1 + sin2) / 1.35f * amplitude * window
        closedFillPath.lineTo(x, centerY + displacement)
    }
    closedFillPath.close()

    // Draw fill if requested
    if (fillBrush != null) {
        drawPath(path = closedFillPath, brush = fillBrush)
    }

    // Draw top mirrored stroke
    drawPath(
        path = topPath,
        color = strokeColor,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Draw bottom mirrored stroke with slightly higher subtlety
    drawPath(
        path = bottomPath,
        color = strokeColor.copy(alpha = strokeColor.alpha * 0.65f),
        style = Stroke(
            width = strokeWidth * 0.8f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}
