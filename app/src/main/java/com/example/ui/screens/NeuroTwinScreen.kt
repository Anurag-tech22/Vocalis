package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharismaTwinDuel
import com.example.data.model.LogicFractureAlert
import com.example.data.model.LogicPyramidNode
import com.example.data.model.NeuroTwinCatalog
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
import com.example.viewmodel.RehearsalViewModel
import kotlinx.coroutines.delay

/**
 * World-First Neural Charisma Twin & Logic Pyramid X-Ray.
 * 1. Pyramid Logic X-Ray (MECE Tree & Logical Fallacy Scanner)
 * 2. Charisma Twin Audio Duel (AI Audio Re-enactment & Conviction Multiplier)
 * 3. Vagal Composure & 4-4-4-4 Box Breathing Telemetry
 */
@Composable
fun NeuroTwinScreen(
    viewModel: RehearsalViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Charisma Twin, 1: Logic Pyramid, 2: Vagal Composure
    val isSpeaking by viewModel.speechManager.isSpeaking.collectAsState()

    Scaffold(
        containerColor = VocalisObsidian,
        topBar = {
            NeuroTwinTopBar(
                onBack = {
                    viewModel.speechManager.stopSpeaking()
                    viewModel.navigateBack()
                }
            )
        },
        modifier = modifier.testTag("neuro_twin_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Tri-Mode Segmented Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VocalisSurfaceElevated)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TabPill(
                        label = "Charisma Twin",
                        icon = Icons.Default.Hearing,
                        isSelected = selectedTab == 0,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = 0 }
                    )
                    TabPill(
                        label = "Logic X-Ray",
                        icon = Icons.Default.AccountTree,
                        isSelected = selectedTab == 1,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = 1 }
                    )
                    TabPill(
                        label = "Vagal Reset",
                        icon = Icons.Default.SelfImprovement,
                        isSelected = selectedTab == 2,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = 2 }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // TAB 0: Charisma Twin Audio Duels
                    item {
                        CharismaTwinDuelSection(
                            isSpeaking = isSpeaking,
                            onPlayTwinAudio = { script ->
                                if (isSpeaking) {
                                    viewModel.speechManager.stopSpeaking()
                                } else {
                                    viewModel.speechManager.speak(script)
                                }
                            }
                        )
                    }
                }
                1 -> {
                    // TAB 1: Pyramid Logic X-Ray
                    item {
                        PyramidLogicSection()
                    }
                }
                2 -> {
                    // TAB 2: Vagal Composure & Box Breathing
                    item {
                        VagalComposureSection()
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun TabPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) VocalisEmerald else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else VocalisTextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                color = if (isSelected) Color.Black else VocalisTextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun NeuroTwinTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(VocalisSurfaceElevated)
                    .border(1.dp, VocalisCardBorder, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = VocalisTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "NEURAL CHARISMA TWIN",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = VocalisTextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VocalisAmber.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "WORLD FIRST",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = VocalisAmber
                        )
                    }
                }
                Text(
                    text = "AI Alter-Ego Mirror & Pyramid Logic X-Ray",
                    fontSize = 10.sp,
                    color = VocalisTextMuted
                )
            }
        }
    }
}

@Composable
private fun CharismaTwinDuelSection(
    isSpeaking: Boolean,
    onPlayTwinAudio: (String) -> Unit
) {
    var selectedDuelIdx by remember { mutableIntStateOf(0) }
    val currentDuel = NeuroTwinCatalog.sampleDuels[selectedDuelIdx]

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Duel Scenario Switcher
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(NeuroTwinCatalog.sampleDuels) { idx, duel ->
                val isSelected = selectedDuelIdx == idx
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) VocalisEmerald.copy(alpha = 0.18f) else VocalisSurface)
                        .border(1.dp, if (isSelected) VocalisEmerald else VocalisCardBorder, RoundedCornerShape(10.dp))
                        .clickable { selectedDuelIdx = idx }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = duel.scenarioTitle,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) VocalisEmerald else VocalisTextSecondary
                    )
                }
            }
        }

        // Duel Comparison Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = VocalisSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(listOf(VocalisEmerald.copy(alpha = 0.4f), VocalisCardBorder))
            )
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Header with multiplier
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXECUTIVE CONVICTION DUEL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = VocalisEmerald
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VocalisAmber.copy(alpha = 0.2f))
                            .border(1.dp, VocalisAmber, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${currentDuel.convictionMultiplier}x CONVICTION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = VocalisAmber
                        )
                    }
                }

                // 1. User Original Excerpt (Red / Hesitant styling)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VocalisObsidian)
                        .border(1.dp, VocalisCrimson.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(VocalisCrimson))
                        Text(
                            text = "ORIGINAL DELIVERY (HIGH HEDGING & FILLERS)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = VocalisCrimson
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${currentDuel.userExcerpt}\"",
                        fontSize = 12.sp,
                        color = VocalisTextSecondary,
                        lineHeight = 18.sp
                    )
                }

                // 2. Charisma Twin Level 10 Audio Delivery (Emerald / C-Suite styling)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VocalisObsidian)
                        .border(1.dp, VocalisEmerald.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(VocalisEmerald))
                            Text(
                                text = "CHARISMA TWIN LEVEL 10 RE-SYNTHESIS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = VocalisEmerald
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(VocalisSurfaceElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "-${currentDuel.brevitySavedPercent}% FLUFF",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = VocalisSky
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${currentDuel.executiveTwinAudioScript}\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = VocalisTextPrimary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Audio Playback Button
                    Button(
                        onClick = { onPlayTwinAudio(currentDuel.executiveTwinAudioScript) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("play_twin_audio_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpeaking) VocalisCrimson else VocalisEmerald,
                            contentColor = if (isSpeaking) Color.White else Color.Black
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isSpeaking) "Stop Charisma Audio" else "Listen to Charisma Twin Delivery",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // BLUF Cognitive Principle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VocalisSurfaceElevated)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = VocalisAmber, modifier = Modifier.size(14.dp))
                            Text("COGNITIVE ANCHOR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = VocalisAmber)
                        }
                        Text(
                            text = currentDuel.blufPrinciple,
                            fontSize = 11.sp,
                            color = VocalisTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PyramidLogicSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = VocalisSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VocalisCardBorder, VocalisCardBorder)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AccountTree, contentDescription = null, tint = VocalisEmerald, modifier = Modifier.size(18.dp))
                    Text(
                        text = "THE MINTO PYRAMID LOGIC X-RAY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = VocalisEmerald
                    )
                }
                Text(
                    text = "Deconstructs speech into Mutually Exclusive, Collectively Exhaustive (MECE) argument branches.",
                    fontSize = 10.sp,
                    color = VocalisTextSecondary
                )
            }
        }

        // Logic Pyramid Nodes
        NeuroTwinCatalog.samplePyramid.forEach { node ->
            PyramidNodeCard(node = node)
        }

        // Logic Fracture Detector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = VocalisSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VocalisCrimson.copy(alpha = 0.5f), VocalisCardBorder)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = VocalisCrimson, modifier = Modifier.size(16.dp))
                    Text(
                        text = "LOGIC FRACTURE RADAR (FALLACY DETECTOR)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VocalisCrimson
                    )
                }

                NeuroTwinCatalog.sampleFractures.forEach { fracture ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(VocalisSurfaceElevated)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = fracture.fallacyType,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VocalisAmber
                                )
                                Text(
                                    text = fracture.severity,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = VocalisCrimson
                                )
                            }
                            Text(
                                text = "Excerpt: ${fracture.excerpt}",
                                fontSize = 10.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = VocalisTextMuted
                            )
                            Text(
                                text = "👉 Countermove: ${fracture.countermove}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = VocalisEmerald
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PyramidNodeCard(node: LogicPyramidNode) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (node.id == "apex") VocalisSurfaceElevated else VocalisSurface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    if (node.id == "apex") VocalisEmerald.copy(alpha = 0.6f) else VocalisCardBorder,
                    VocalisCardBorder
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (node.id == "apex") VocalisAmber else VocalisEmerald)
                    )
                    Text(
                        text = node.title,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (node.id == "apex") VocalisAmber else VocalisEmerald
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VocalisObsidian)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = node.evidenceType,
                            fontSize = 8.sp,
                            color = VocalisSky
                        )
                    }
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = VocalisTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = node.claim,
                fontSize = 12.sp,
                fontWeight = if (node.id == "apex") FontWeight.Bold else FontWeight.Normal,
                color = VocalisTextPrimary,
                lineHeight = 18.sp
            )

            AnimatedVisibility(visible = isExpanded && node.subClaims.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    node.subClaims.forEach { sub ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VocalisEmerald, modifier = Modifier.size(12.dp))
                            Text(sub, fontSize = 10.sp, color = VocalisTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VagalComposureSection() {
    var phase by remember { mutableIntStateOf(0) } // 0: Inhale, 1: Hold, 2: Exhale, 3: Hold
    var cyclesCompleted by remember { mutableIntStateOf(3) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            phase = (phase + 1) % 4
            if (phase == 0) cyclesCompleted++
        }
    }

    val (phaseText, phaseInstruction, targetScale) = when (phase) {
        0 -> Triple("INHALATION (4s)", "Diaphragmatic lower-belly expansion", 1.25f)
        1 -> Triple("HOLD WITH GRAVITAS (4s)", "Relax jaw and drop shoulder tension", 1.25f)
        2 -> Triple("MEASURED EXHALE (4s)", "Slow, steady whisper-release through lips", 0.85f)
        else -> Triple("HOLD AT ZERO (4s)", "Ground physical posture into floor", 0.85f)
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
        label = "vagal_sphere_scale"
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Hero Composure & Autonomic Vagal Shield
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = VocalisSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(listOf(VocalisEmerald.copy(alpha = 0.5f), VocalisCardBorder))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = VocalisEmerald, modifier = Modifier.size(18.dp))
                        Text("VAGAL COMPOSURE ANCHOR", fontSize = 11.sp, fontWeight = FontWeight.Black, color = VocalisEmerald)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(VocalisSurfaceElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("$cyclesCompleted Cycles Done", fontSize = 9.sp, color = VocalisTextPrimary)
                    }
                }

                // Animated Vagal Breathing Circle
                Box(
                    modifier = Modifier
                        .size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(160.dp)) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = (size.width / 2f) * animatedScale

                        // Outer Glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(VocalisEmerald.copy(alpha = 0.35f), Color.Transparent),
                                center = center,
                                radius = radius * 1.25f
                            ),
                            center = center,
                            radius = radius * 1.25f
                        )

                        // Central Resonant Core
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(VocalisEmerald, VocalisSky.copy(alpha = 0.6f)),
                                center = center,
                                radius = radius
                            ),
                            center = center,
                            radius = radius * 0.75f
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = phaseText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Text(
                    text = phaseInstruction,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = VocalisTextPrimary
                )

                // Sub-telemetry
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
                            Text("Cortisol Suppressor", fontSize = 8.sp, color = VocalisTextMuted)
                            Text("-42% Panic Spike", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VocalisEmerald)
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
                            Text("Heart Rate Coherence", fontSize = 8.sp, color = VocalisTextMuted)
                            Text("0.84 Optimal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VocalisAmber)
                        }
                    }
                }
            }
        }
    }
}
