package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SessionEntity
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.PolarisDarkBorder
import com.example.ui.theme.PolarisIndigoSecondary

@Composable
fun ScoreTrendChart(
    sessions: List<SessionEntity>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Performance Trajectory",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (sessions.isNotEmpty()) "Score evolution across ${sessions.size} rehearsals (0–10 scale)" else "Complete your first rehearsal to view trajectory",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No rehearsal sessions recorded yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val lineColor = PolarisBluePrimary
                    val gradientStart = PolarisBluePrimary.copy(alpha = 0.35f)
                    val gradientEnd = PolarisIndigoSecondary.copy(alpha = 0.02f)
                    val gridColor = PolarisDarkBorder.copy(alpha = 0.4f)
                    val avgLineColor = PolarisAmberGold.copy(alpha = 0.6f)

                    // Sessions in chronological order for charting
                    val chronological = sessions.sortedBy { it.timestamp }
                    val scores = chronological.map { it.finalScore.coerceIn(0, 10).toFloat() }
                    val avgScore = scores.average().toFloat()

                    Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 8.dp)) {
                        val width = size.width
                        val height = size.height

                        // Grid lines for 2, 4, 6, 8, 10
                        val gridSteps = 5
                        for (i in 0..gridSteps) {
                            val y = height * (1f - (i.toFloat() / gridSteps))
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1f
                            )
                        }

                        // Average reference line
                        val avgY = height * (1f - (avgScore / 10f))
                        drawLine(
                            color = avgLineColor,
                            start = Offset(0f, avgY),
                            end = Offset(width, avgY),
                            strokeWidth = 1.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        if (scores.size == 1) {
                            // Single point
                            val cx = width / 2f
                            val cy = height * (1f - (scores[0] / 10f))
                            drawCircle(
                                color = lineColor,
                                radius = 7f,
                                center = Offset(cx, cy)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.5f,
                                center = Offset(cx, cy)
                            )
                        } else {
                            val stepX = width / (scores.size - 1)
                            val path = Path()
                            val fillPath = Path()

                            fillPath.moveTo(0f, height)

                            scores.forEachIndexed { index, score ->
                                val x = index * stepX
                                val y = height * (1f - (score / 10f))
                                if (index == 0) {
                                    path.moveTo(x, y)
                                    fillPath.lineTo(x, y)
                                } else {
                                    // Smooth cubic curve
                                    val prevX = (index - 1) * stepX
                                    val prevY = height * (1f - (scores[index - 1] / 10f))
                                    val controlX1 = prevX + (x - prevX) / 2f
                                    val controlY1 = prevY
                                    val controlX2 = prevX + (x - prevX) / 2f
                                    val controlY2 = y
                                    path.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                                    fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                                }
                            }

                            fillPath.lineTo(width, height)
                            fillPath.close()

                            // Draw gradient area
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(gradientStart, gradientEnd),
                                    startY = 0f,
                                    endY = height
                                )
                            )

                            // Draw trend line
                            drawPath(
                                path = path,
                                color = lineColor,
                                style = Stroke(width = 3.5f)
                            )

                            // Draw points
                            scores.forEachIndexed { index, score ->
                                val x = index * stepX
                                val y = height * (1f - (score / 10f))
                                drawCircle(
                                    color = lineColor,
                                    radius = 5.5f,
                                    center = Offset(x, y)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.5f,
                                    center = Offset(x, y)
                                )
                            }
                        }
                    }
                }

                // Chart footnote
                val avgFormatted = String.format("%.1f", sessions.map { it.finalScore }.average())
                Text(
                    text = "• Avg Score: $avgFormatted / 10  (Dashed gold reference line)",
                    style = MaterialTheme.typography.labelSmall,
                    color = PolarisAmberGold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
