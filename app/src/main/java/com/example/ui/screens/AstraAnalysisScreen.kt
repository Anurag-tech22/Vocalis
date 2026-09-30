package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.model.AstraDeepReport
import com.example.data.model.Screen
import com.example.ui.components.ExecutiveDossierCertificate
import com.example.ui.components.NeuralRadarChart
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.PolarisDarkBorder
import com.example.ui.theme.PolarisDarkSurface
import com.example.ui.theme.PolarisIndigoSecondary
import com.example.ui.theme.ScoreAmber
import com.example.ui.theme.ScoreGreen
import com.example.ui.theme.ScoreRed
import com.example.viewmodel.RehearsalViewModel

@Composable
fun AstraAnalysisScreen(
    viewModel: RehearsalViewModel,
    sessionId: Long? = null,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.Home)
    }

    val report by viewModel.currentAstraReport.collectAsState()

    if (report == null) {
        Box(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "No analysis data available yet.", color = Color.White)
                Button(onClick = { viewModel.navigateTo(Screen.Home) }) {
                    Text("Return Home")
                }
            }
        }
        return
    }

    val safeReport: AstraDeepReport = report!!

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PolarisDarkSurface)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        modifier = Modifier.testTag("btn_back_astra")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Column {
                        Text(
                            text = "VOCALIS COGNITIVE LAB",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = PolarisBluePrimary
                        )
                        Text(
                            text = "Frontier 6-Axis Neural Diagnostic",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { viewModel.exportGitHubBuildInPublic() },
                        modifier = Modifier.testTag("btn_export_github_markdown")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Export GitHub Markdown",
                            tint = PolarisBluePrimary
                        )
                    }
                    IconButton(onClick = { viewModel.shareAstraReport() }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Report",
                            tint = PolarisAmberGold
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Verified Executive Orator Credential & Cryptographic Certificate
            ExecutiveDossierCertificate(
                report = safeReport,
                sessionGoal = safeReport.executiveSummary.take(60)
            )

            // 1. Master Composite Cognitive Index Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = PolarisDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(PolarisBluePrimary, PolarisIndigoSecondary, PolarisAmberGold)
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PolarisAmberGold, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "READINESS ARCHETYPE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PolarisAmberGold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = safeReport.readinessArchetype,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        // Circular Composite Score Badge
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(PolarisBluePrimary, PolarisIndigoSecondary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${safeReport.overallIndex}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "/ 100",
                                    fontSize = 8.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Text(
                        text = safeReport.archetypeDescription,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    // Key Summary Callout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = safeReport.executiveSummary,
                            fontSize = 12.sp,
                            color = Color.White,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // 2. Interactive 6-Axis Neural Radar Chart
            NeuralRadarChart(
                metrics = safeReport.radar,
                benchmarkEnabled = true
            )

            // 3. Speech & Vocal Biometrics Dashboard
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PolarisDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(PolarisIndigoSecondary.copy(alpha = 0.5f), PolarisDarkBorder)
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = PolarisBluePrimary)
                        Text(
                            text = "VOCAL & SPEECH TELEMETRY",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    }

                    // Metric Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // WPM
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("CADENCE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${safeReport.biometrics.wordsPerMinute} WPM", fontSize = 16.sp, fontWeight = FontWeight.Black, color = PolarisBluePrimary)
                                Text(safeReport.biometrics.cadenceStatus.label, fontSize = 8.sp, color = ScoreGreen)
                            }
                        }

                        // Fillers
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("FILLERS", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${safeReport.biometrics.fillerCount}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = if (safeReport.biometrics.fillerCount > 0) ScoreAmber else ScoreGreen)
                                Text("${String.format("%.1f", safeReport.biometrics.fillerPercentage)}% of words", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Hedges
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("HEDGES", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${safeReport.biometrics.hedgeCount}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = if (safeReport.biometrics.hedgeCount > 0) ScoreRed else ScoreGreen)
                                Text(if (safeReport.biometrics.hedgeCount == 0) "Zero Passivity" else "Weak Qualifiers", fontSize = 8.sp, color = ScoreAmber)
                            }
                        }
                    }

                    // Filler & Power word Chips if detected
                    if (safeReport.biometrics.detectedFillers.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Detected Fillers:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            safeReport.biometrics.detectedFillers.forEach { f ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ScoreAmber.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("\"$f\"", fontSize = 10.sp, color = ScoreAmber, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    if (safeReport.biometrics.detectedPowerWords.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Power Verbs:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            safeReport.biometrics.detectedPowerWords.forEach { p ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ScoreGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(p, fontSize = 10.sp, color = ScoreGreen, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Sentence Surgeon Autopsy (Before vs Level 10 Executive Rewrite)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PolarisDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(PolarisAmberGold.copy(alpha = 0.4f), PolarisIndigoSecondary.copy(alpha = 0.3f))
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Transform, contentDescription = null, tint = PolarisAmberGold)
                        Column {
                            Text(
                                text = "SENTENCE SURGEON AUTOPSY",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                            Text(
                                text = "How You Spoke vs How a Top 1% Executive Would Say It",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    safeReport.surgeries.forEachIndexed { idx, surgery ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Category Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ScoreRed.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = surgery.flawCategory,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ScoreRed
                                    )
                                }

                                Text(
                                    text = surgery.cognitivePrinciple,
                                    fontSize = 10.sp,
                                    color = PolarisBluePrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Original Excerpt
                            Column {
                                Text("YOUR WORDS:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = surgery.originalExcerpt,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    lineHeight = 16.sp
                                )
                            }

                            // Executive Rewrite
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PolarisBluePrimary.copy(alpha = 0.12f))
                                    .border(1.dp, PolarisBluePrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ScoreGreen, modifier = Modifier.size(12.dp))
                                    Text("LEVEL 10 EXECUTIVE REWRITE:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PolarisBluePrimary)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = surgery.executiveLevel10Rewrite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }

            // 5. Blindspots & Action Prescription
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PolarisDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(PolarisIndigoSecondary, PolarisBluePrimary)
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = PolarisAmberGold)
                        Text(
                            text = "UNCONSCIOUS BLINDSPOTS & AI ACTION PLAN",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    }

                    // Blindspots
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        safeReport.blindspots.forEach { spot ->
                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = ScoreAmber, modifier = Modifier.size(16.dp))
                                Text(text = spot, fontSize = 12.sp, color = Color.White, lineHeight = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("3 HIGH-IMPACT MICRO-DRILLS:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PolarisBluePrimary)

                    // Prescriptions
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        safeReport.highImpactPrescriptions.forEachIndexed { i, rx ->
                            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(PolarisBluePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${i + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Text(text = rx, fontSize = 12.sp, color = Color.White, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }

            // Bottom Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(Screen.Home) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Return Home")
                }

                Button(
                    onClick = { viewModel.startNewSessionWithSameGoal() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PolarisBluePrimary)
                ) {
                    Text("Rehearse Again", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
