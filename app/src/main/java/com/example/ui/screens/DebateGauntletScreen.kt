package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.DebateOpponent
import com.example.data.model.Screen
import com.example.ui.components.RealtimeWaveformVisualizer
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.ScoreGreen
import com.example.ui.theme.ScoreRed
import com.example.ui.theme.VocalisAmber
import com.example.ui.theme.VocalisCyan
import com.example.ui.theme.VocalisEmerald
import com.example.ui.theme.VocalisSurface
import com.example.ui.theme.VocalisSurfaceElevated
import com.example.util.AudioSoundEngine
import com.example.viewmodel.RehearsalViewModel
import kotlinx.coroutines.delay

@Composable
fun DebateGauntletScreen(
    viewModel: RehearsalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler {
        viewModel.stopSpeaking()
        viewModel.stopListening()
        viewModel.navigateTo(Screen.Home)
    }

    val opponents = remember {
        listOf(
            DebateOpponent(
                id = "vc_victoria",
                name = "Victoria Sterling",
                title = "General Partner",
                organization = "Apex Tier Ventures",
                objectionStrategy = "Relentless CAC/LTV & Churn Cross-Examination",
                initialSalvo = "Your unit economics look completely theoretical. If your customer acquisition cost triples next quarter when Google changes ad algorithms, how do you avoid insolvency within 90 days?",
                followUpCurveballs = listOf(
                    "That answer avoids the question. Name your exact gross margins and explain why an incumbent won't copy this feature by Friday.",
                    "You're quoting GMV instead of net revenue. Give me the real take rate right now."
                ),
                pitch = 1.06f,
                speed = 1.08f,
                avatarInitials = "VS",
                badgeColorHex = 0xFFFF5252
            ),
            DebateOpponent(
                id = "journo_david",
                name = "David Cross",
                title = "Senior Investigative Reporter",
                organization = "Silicon Wire",
                objectionStrategy = "Ethical Scrutiny & Crisis Communications",
                initialSalvo = "Whistleblower documents suggest your engineering team knowingly bypassed automated safety audits to hit deadline targets. Did you personally authorize this deployment?",
                followUpCurveballs = listOf(
                    "You say safety is paramount, but your incident response took 48 hours. Why wasn't the board notified immediately?",
                    "If the liability falls on your sub-processors, why should enterprise clients trust your SLA?"
                ),
                pitch = 0.94f,
                speed = 1.04f,
                avatarInitials = "DC",
                badgeColorHex = 0xFFFFB300
            ),
            DebateOpponent(
                id = "ma_viktor",
                name = "Viktor Kozlov",
                title = "Hostile M&A Director",
                organization = "Global Synergies Corp",
                objectionStrategy = "Aggressive Valuation Squeeze & Defensibility",
                initialSalvo = "We're discounting your valuation by 40% immediately. Your market share has plateaued, and without our enterprise distribution rail, your churn will double. Why shouldn't we walk away?",
                followUpCurveballs = listOf(
                    "Your moat consists entirely of open-source wrappers. We can replicate your pipeline in six weeks with ten engineers.",
                    "If you reject this term sheet, where is your alternative liquidity coming from?"
                ),
                pitch = 0.86f,
                speed = 0.98f,
                avatarInitials = "VK",
                badgeColorHex = 0xFF7C4DFF
            )
        )
    }

    var selectedOpponent by remember { mutableStateOf(opponents[0]) }
    var isBattleActive by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableIntStateOf(60) }
    var candidateComposure by remember { mutableIntStateOf(100) }
    var opponentStage by remember { mutableIntStateOf(0) }
    var isBattleConcluded by remember { mutableStateOf(false) }

    val isListening by viewModel.speechManager.isListening.collectAsState()
    val isSpeaking by viewModel.speechManager.isSpeaking.collectAsState()
    val audioRms by viewModel.speechManager.audioRms.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        }
    }

    // Tension countdown loop
    LaunchedEffect(isBattleActive, secondsRemaining) {
        if (isBattleActive && secondsRemaining > 0) {
            delay(1000)
            secondsRemaining -= 1
            if (secondsRemaining % 20 == 0 && opponentStage < selectedOpponent.followUpCurveballs.size) {
                // Opponent fires next curveball
                val nextCurveball = selectedOpponent.followUpCurveballs[opponentStage]
                opponentStage += 1
                AudioSoundEngine.playStressSiren()
                viewModel.speechManager.speak(nextCurveball, pitch = selectedOpponent.pitch, speechRate = selectedOpponent.speed)
                candidateComposure = (candidateComposure - 12).coerceAtLeast(20)
            }
        } else if (isBattleActive && secondsRemaining == 0) {
            isBattleActive = false
            isBattleConcluded = true
            viewModel.stopListening()
            AudioSoundEngine.playEvaluationCompleteTone()
        }
    }

    val pulseTransition = rememberInfiniteTransition(label = "gauntletPulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulseScale"
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    viewModel.stopSpeaking()
                    viewModel.stopListening()
                    viewModel.navigateTo(Screen.Home)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isBattleActive) ScoreRed else VocalisEmerald)
                    )
                    Text(
                        text = if (isBattleActive) "LIVE VERBAL SPARRING" else "AI DEBATE GAUNTLET",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF261216))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${secondsRemaining}s",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = if (secondsRemaining <= 15) ScoreRed else PolarisAmberGold
                    )
                }
            }
        },
        containerColor = Color(0xFF06090F)
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Opponent Selector Pills (When not in active battle)
            if (!isBattleActive && !isBattleConcluded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SELECT YOUR ADVERSARY:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = PolarisBluePrimary
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(opponents) { opp ->
                            val isSelected = opp.id == selectedOpponent.id
                            val oppCol = Color(opp.badgeColorHex)
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable { selectedOpponent = opp },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) oppCol.copy(alpha = 0.15f) else VocalisSurface
                                ),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = if (isSelected) Brush.horizontalGradient(listOf(oppCol, PolarisBluePrimary))
                                    else Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFF1E293B)))
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(opp.name, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(oppCol)
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(opp.avatarInitials, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }
                                    Text(opp.title + " • " + opp.organization, fontSize = 9.sp, color = oppCol)
                                    Text(opp.objectionStrategy, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                }
                            }
                        }
                    }
                }
            }

            // Adversary Arena Stage Card
            val oppColor = Color(selectedOpponent.badgeColorHex)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C101A)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(oppColor.copy(alpha = 0.8f), Color(0xFF1E293B))
                    ),
                    width = 1.5.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .scale(if (isSpeaking) pulseScale else 1f)
                                    .clip(CircleShape)
                                    .background(oppColor.copy(alpha = 0.2f))
                                    .border(2.dp, oppColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedOpponent.avatarInitials,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = oppColor
                                )
                            }

                            Column {
                                Text(selectedOpponent.name, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text(selectedOpponent.title + " • " + selectedOpponent.organization, fontSize = 10.sp, color = oppColor)
                            }
                        }

                        IconButton(onClick = {
                            val textToSpeak = if (opponentStage == 0) selectedOpponent.initialSalvo
                            else selectedOpponent.followUpCurveballs[(opponentStage - 1).coerceAtLeast(0)]
                            viewModel.speechManager.speak(textToSpeak, pitch = selectedOpponent.pitch, speechRate = selectedOpponent.speed)
                        }) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Hear Opponent", tint = oppColor)
                        }
                    }

                    // Opponent Salvo Text
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF131926))
                            .border(1.dp, oppColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        val activeText = if (opponentStage == 0) selectedOpponent.initialSalvo
                        else selectedOpponent.followUpCurveballs[(opponentStage - 1).coerceAtLeast(0)]

                        Text(
                            text = "\"$activeText\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFF1F5F9),
                            lineHeight = 18.sp
                        )
                    }

                    // Composure Resilience Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("CANDIDATE COMPOSURE SHIELD", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$candidateComposure%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (candidateComposure >= 70) VocalisEmerald else ScoreRed)
                        }
                        LinearProgressIndicator(
                            progress = { candidateComposure / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (candidateComposure >= 70) VocalisEmerald else ScoreRed,
                            trackColor = Color(0xFF1E293B)
                        )
                    }
                }
            }

            // Real-time Audio Visualizer
            RealtimeWaveformVisualizer(
                audioLevel = audioRms,
                isAudioActive = isListening || isSpeaking,
                height = 70.dp,
                showGrid = false,
                accentColor = if (isBattleActive) oppColor else VocalisCyan,
                secondaryColor = PolarisBluePrimary
            )

            // Battle Results or Action Controls
            if (isBattleConcluded) {
                // Battle Post-Mortem Verdict Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = VocalisSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(VocalisEmerald, PolarisAmberGold))
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = VocalisEmerald)
                            Text(
                                text = "VERBAL GAUNTLET POST-MORTEM",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = VocalisEmerald
                            )
                        }

                        Text(
                            text = if (candidateComposure >= 75)
                                "🏆 VERDICT: BOARDROOM DOMINANCE! You absorbed intense adversarial pushback without breaking composure or adopting defensive postures."
                            else
                                "⚠️ VERDICT: COMPOSURE FRACTURED. Adversary detected hesitation. Practice BLUF assertions to lead with bottom-line impact immediately.",
                            fontSize = 11.sp,
                            color = Color.White,
                            lineHeight = 16.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    isBattleConcluded = false
                                    isBattleActive = false
                                    secondsRemaining = 60
                                    candidateComposure = 100
                                    opponentStage = 0
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = VocalisEmerald),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Rematch Opponent", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(Screen.Home) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Return Home", fontSize = 11.sp)
                            }
                        }
                    }
                }
            } else if (!isBattleActive) {
                // Launch Sparring Button
                Button(
                    onClick = {
                        isBattleActive = true
                        secondsRemaining = 60
                        candidateComposure = 100
                        opponentStage = 0
                        AudioSoundEngine.playStressSiren()
                        viewModel.speechManager.speak(selectedOpponent.initialSalvo, pitch = selectedOpponent.pitch, speechRate = selectedOpponent.speed)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = oppColor)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black)
                        Text(
                            text = "START 60s VERBAL SPARRING GAUNTLET",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color.Black
                        )
                    }
                }
            } else {
                // Live Spoken Response Mic Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (isListening) {
                                viewModel.stopListening()
                            } else {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                    viewModel.startListening()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isListening) ScoreRed else VocalisEmerald
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(if (isListening) Icons.Default.MicOff else Icons.Default.Mic, contentDescription = null, tint = Color.Black)
                            Text(if (isListening) "Submit Counter-Argument" else "Speak Counter-Argument", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            isBattleActive = false
                            isBattleConcluded = true
                            viewModel.stopListening()
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Yield", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
