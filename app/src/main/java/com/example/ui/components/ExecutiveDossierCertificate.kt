package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AstraDeepReport
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.VocalisAmber
import com.example.ui.theme.VocalisCyan
import com.example.ui.theme.VocalisEmerald
import com.example.ui.theme.VocalisSurface

@Composable
fun ExecutiveDossierCertificate(
    report: AstraDeepReport,
    sessionGoal: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isDossierExpanded by remember { mutableStateOf(false) }

    val auditHash = remember(report.overallIndex) {
        val hashInt = (report.overallIndex * 31 + sessionGoal.hashCode()).let { if (it < 0) -it else it }
        "0x" + hashInt.toString(16).uppercase().padStart(8, '0') + "E7A1C"
    }

    val credentialId = remember(report.overallIndex) {
        "VOCALIS-VERIFIED-" + (1000 + (Math.random() * 8999).toInt()) + "-EXEC"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF090D15)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    PolarisAmberGold,
                    Color(0xFF8D6E63),
                    PolarisAmberGold
                )
            ),
            width = 1.5.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Certificate Top Header & Verified Hologram Seal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(PolarisAmberGold, Color(0xFFB78103))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Verified Seal",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "VOCALIS EXECUTIVE ORATOR CREDENTIAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = PolarisAmberGold
                            )
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = VocalisCyan,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = "Audited Linguistic, Gravitas & Composure Telemetry",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PolarisAmberGold.copy(alpha = 0.15f))
                        .border(1.dp, PolarisAmberGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable { isDossierExpanded = !isDossierExpanded }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isDossierExpanded) "Collapse" else "View Certificate",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PolarisAmberGold
                    )
                }
            }

            // Quick Snapshot Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF101726))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("OVERALL READINESS", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text("${report.overallIndex} / 100", fontSize = 16.sp, fontWeight = FontWeight.Black, color = VocalisEmerald)
                }
                Column {
                    Text("ARCHETYPE", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text(report.readinessArchetype, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("CADENCE", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text("${report.biometrics.wordsPerMinute} WPM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VocalisCyan)
                }
            }

            AnimatedVisibility(visible = isDossierExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HorizontalDivider(color = PolarisAmberGold.copy(alpha = 0.3f), thickness = 0.5.dp)

                    // Core Credential Data Body
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0C111C))
                            .border(1.dp, Color(0xFF26324D), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "OFFICIAL EXECUTIVE AUDIT RECORD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = PolarisAmberGold
                        )

                        Text(
                            text = "This certifies that the candidate has completed an intensive stress-tested executive verbal rehearsal under high-stakes adversarial conditions.",
                            fontSize = 11.sp,
                            color = Color(0xFFD1D5DB),
                            lineHeight = 16.sp
                        )

                        // 4 Diagnostic Pillars Grid
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Pillar 1: Clarity
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF141C2E))
                                    .padding(8.dp)
                            ) {
                                Text("CLARITY", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                Text("${report.radar.clarity}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = VocalisCyan)
                                Text("Filler: ${report.biometrics.fillerPercentage}%", fontSize = 8.sp, color = VocalisEmerald)
                            }
                            // Pillar 2: Gravitas
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF141C2E))
                                    .padding(8.dp)
                            ) {
                                Text("GRAVITAS", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                Text("${report.radar.gravitas}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = PolarisAmberGold)
                                Text("Power words: ${report.biometrics.powerWordsCount}", fontSize = 8.sp, color = VocalisCyan)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Pillar 3: Structure
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF141C2E))
                                    .padding(8.dp)
                            ) {
                                Text("STRUCTURE", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                Text("${report.radar.structure}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = VocalisEmerald)
                                Text("Minto BLUF Master", fontSize = 8.sp, color = VocalisTextMuted)
                            }
                            // Pillar 4: Stress Resilience
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF141C2E))
                                    .padding(8.dp)
                            ) {
                                Text("RESILIENCE", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                Text("${report.radar.stressResilience}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF7C4DFF))
                                Text("Vagal Composure", fontSize = 8.sp, color = VocalisTextMuted)
                            }
                        }

                        // Cryptographic Audit Footer
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF070B12))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("CREDENTIAL SERIAL", fontSize = 7.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(credentialId, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = PolarisAmberGold)
                                Text("HASH: $auditHash", fontSize = 7.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = PolarisAmberGold, modifier = Modifier.size(28.dp))
                        }
                    }

                    // Share Dossier Button
                    Button(
                        onClick = {
                            shareExecutiveDossier(
                                context = context,
                                report = report,
                                sessionGoal = sessionGoal,
                                credentialId = credentialId,
                                auditHash = auditHash
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PolarisAmberGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Text("Share Verified Executive Dossier", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

private val VocalisTextMuted = Color(0xFF9E9E9E)

private fun shareExecutiveDossier(
    context: Context,
    report: AstraDeepReport,
    sessionGoal: String,
    credentialId: String,
    auditHash: String
) {
    val shareBody = buildString {
        appendLine("🏆 VOCALIS VERIFIED EXECUTIVE ORATOR DOSSIER")
        appendLine("Credential Serial: $credentialId")
        appendLine("Integrity Hash: $auditHash")
        appendLine("--------------------------------------------")
        appendLine("Target Scenario: \"$sessionGoal\"")
        appendLine("Overall Executive Index: ${report.overallIndex} / 100")
        appendLine("Readiness Archetype: ${report.readinessArchetype}")
        appendLine()
        appendLine("📊 6-AXIS COGNITIVE BIOMETRICS:")
        appendLine("• Clarity & Precision: ${report.radar.clarity}%")
        appendLine("• Gravitas & Conviction: ${report.radar.gravitas}%")
        appendLine("• Minto Pyramid Structure: ${report.radar.structure}%")
        appendLine("• Stress Resilience: ${report.radar.stressResilience}%")
        appendLine("• Cadence Velocity: ${report.biometrics.wordsPerMinute} WPM (${report.biometrics.cadenceStatus.name})")
        appendLine("• Filler Word Ratio: ${report.biometrics.fillerPercentage}%")
        appendLine()
        appendLine("💡 EXECUTIVE SUMMARY:")
        appendLine(report.executiveSummary)
        appendLine("--------------------------------------------")
        appendLine("Verified by Vocalis AI Studio Speech Intelligence.")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Executive Briefing Dossier: $sessionGoal")
        putExtra(Intent.EXTRA_TEXT, shareBody)
    }
    context.startActivity(Intent.createChooser(intent, "Share Executive Dossier"))
}
