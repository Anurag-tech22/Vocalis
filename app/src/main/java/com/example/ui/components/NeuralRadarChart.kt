package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CognitiveRadarMetrics
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.PolarisIndigoSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NeuralRadarChart(
    metrics: CognitiveRadarMetrics,
    modifier: Modifier = Modifier,
    benchmarkEnabled: Boolean = true
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(metrics) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    var selectedAxisIndex by remember { mutableStateOf<Int?>(null) }

    val axes = listOf(
        "Clarity" to metrics.clarity,
        "Gravitas" to metrics.gravitas,
        "Structure" to metrics.structure,
        "Resilience" to metrics.stressResilience,
        "Cadence" to metrics.vocalCadence,
        "Brevity" to metrics.executiveBrevity
    )

    // Benchmark comparison (Top 5% Executive standard)
    val benchmarkScores = listOf(92, 88, 90, 85, 95, 88)

    val axisDescriptions = listOf(
        "Clarity & Articulation: Absence of filler words, crisp vocal resonance, and unambiguous pronunciation.",
        "Gravitas & Conviction: Unwavering authority and psychological assertiveness; zero defensive hedging.",
        "Structural Coherence: Rigorous logical frameworks (STAR/BLUF) with distinct problem-action-impact flow.",
        "Stress Resilience: Composure retention and cognitive recovery when interrupted or challenged.",
        "Vocal Cadence: Rhythm, breath control, and deliberate 130-155 WPM conversational speed.",
        "Executive Brevity: High signal-to-noise ratio; maximum insight with minimum extraneous syllables."
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
            .border(1.dp, PolarisBluePrimary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Chart Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ASTRA COGNITIVE RADAR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = PolarisBluePrimary
                )
                Text(
                    text = "6-Dimensional Vocal & Mental Matrix",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Legend
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(PolarisBluePrimary, CircleShape)
                    )
                    Text(
                        text = " You",
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
                if (benchmarkEnabled) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(PolarisAmberGold, CircleShape)
                        )
                        Text(
                            text = " Top 5%",
                            fontSize = 10.sp,
                            color = PolarisAmberGold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Radar Canvas
        Box(
            modifier = Modifier
                .size(280.dp)
                .testTag("neural_radar_canvas"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(260.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = (size.width / 2f) * 0.78f
                val numAxes = axes.size
                val angleStep = (2 * Math.PI / numAxes).toFloat()

                // Draw concentric pentagonal/hexagonal grid webs (20%, 40%, 60%, 80%, 100%)
                val gridLevels = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)
                gridLevels.forEach { level ->
                    val webPath = Path()
                    for (i in 0 until numAxes) {
                        val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                        val r = radius * level
                        val x = center.x + (r * cos(angle))
                        val y = center.y + (r * sin(angle))
                        if (i == 0) webPath.moveTo(x, y) else webPath.lineTo(x, y)
                    }
                    webPath.close()

                    drawPath(
                        path = webPath,
                        color = Color.White.copy(alpha = if (level == 1f) 0.25f else 0.10f),
                        style = Stroke(
                            width = if (level == 1f) 1.5f else 1f,
                            pathEffect = if (level < 1f) PathEffect.dashPathEffect(floatArrayOf(6f, 6f)) else null
                        )
                    )
                }

                // Draw spoke axis lines
                for (i in 0 until numAxes) {
                    val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                    val x = center.x + (radius * cos(angle))
                    val y = center.y + (radius * sin(angle))
                    drawLine(
                        color = Color.White.copy(alpha = 0.15f),
                        start = center,
                        end = Offset(x, y),
                        strokeWidth = 1f
                    )
                }

                // Draw Benchmark Polygon (Top 5% Gold)
                if (benchmarkEnabled) {
                    val benchPath = Path()
                    for (i in 0 until numAxes) {
                        val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                        val scoreNorm = (benchmarkScores[i] / 100f) * animationProgress.value
                        val r = radius * scoreNorm
                        val x = center.x + (r * cos(angle))
                        val y = center.y + (r * sin(angle))
                        if (i == 0) benchPath.moveTo(x, y) else benchPath.lineTo(x, y)
                    }
                    benchPath.close()

                    drawPath(
                        path = benchPath,
                        color = PolarisAmberGold.copy(alpha = 0.12f),
                        style = Fill
                    )
                    drawPath(
                        path = benchPath,
                        color = PolarisAmberGold.copy(alpha = 0.5f),
                        style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f)))
                    )
                }

                // Draw User Performance Polygon (Cyan/Indigo Glow)
                val userPath = Path()
                val userPoints = mutableListOf<Offset>()

                for (i in 0 until numAxes) {
                    val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                    val scoreNorm = (axes[i].second / 100f) * animationProgress.value
                    val r = radius * scoreNorm
                    val x = center.x + (r * cos(angle))
                    val y = center.y + (r * sin(angle))
                    val point = Offset(x, y)
                    userPoints.add(point)
                    if (i == 0) userPath.moveTo(x, y) else userPath.lineTo(x, y)
                }
                userPath.close()

                // Translucent Gradient Fill
                drawPath(
                    path = userPath,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PolarisBluePrimary.copy(alpha = 0.55f),
                            PolarisIndigoSecondary.copy(alpha = 0.25f)
                        ),
                        center = center,
                        radius = radius
                    ),
                    style = Fill
                )

                // Glowing Stroke
                drawPath(
                    path = userPath,
                    color = PolarisBluePrimary,
                    style = Stroke(width = 2.5f)
                )

                // Draw anchor node circles at vertices
                userPoints.forEachIndexed { i, pt ->
                    val isSelected = selectedAxisIndex == i
                    drawCircle(
                        color = if (isSelected) Color.White else PolarisBluePrimary,
                        radius = if (isSelected) 7f else 4.5f,
                        center = pt
                    )
                    drawCircle(
                        color = Color(0xFF0F172A),
                        radius = 2.5f,
                        center = pt
                    )
                }

                // Draw Axis text labels on the outside
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 30f
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }

                for (i in 0 until numAxes) {
                    val angle = (i * angleStep) - (Math.PI / 2).toFloat()
                    val labelR = radius + 34f
                    val lx = center.x + (labelR * cos(angle))
                    val ly = center.y + (labelR * sin(angle)) + 10f

                    val label = "${axes[i].first} ${axes[i].second}"
                    drawContext.canvas.nativeCanvas.drawText(label, lx, ly, textPaint)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Axis Quick Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            axes.forEachIndexed { index, (name, score) ->
                val isSelected = selectedAxisIndex == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) PolarisBluePrimary else Color.White.copy(alpha = 0.08f))
                        .clickable { selectedAxisIndex = if (isSelected) null else index }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "$name: $score",
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.White
                    )
                }
            }
        }

        // Selected Diagnostic Card Breakdown
        selectedAxisIndex?.let { idx ->
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PolarisIndigoSecondary.copy(alpha = 0.2f))
                    .border(1.dp, PolarisIndigoSecondary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Axis Info",
                        tint = PolarisBluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = axisDescriptions[idx],
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
