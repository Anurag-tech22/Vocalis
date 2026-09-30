package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DifficultyTone
import com.example.data.model.Screen
import com.example.ui.components.RealtimeWaveformVisualizer
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.PolarisDarkBorder
import com.example.ui.theme.PolarisIndigoSecondary
import com.example.ui.theme.ScoreGreen
import com.example.ui.theme.VocalisAmber
import com.example.ui.theme.VocalisCardBorder
import com.example.ui.theme.VocalisCyan
import com.example.ui.theme.VocalisEmerald
import com.example.ui.theme.VocalisGlassBorder
import com.example.ui.theme.VocalisIndigo
import com.example.ui.theme.VocalisObsidian
import com.example.ui.theme.VocalisSky
import com.example.ui.theme.VocalisSurface
import com.example.ui.theme.VocalisSurfaceElevated
import com.example.ui.theme.VocalisTextMuted
import com.example.ui.theme.VocalisTextPrimary
import com.example.ui.theme.VocalisTextSecondary
import com.example.viewmodel.RehearsalViewModel

data class UniversalPreset(
    val title: String,
    val category: String,
    val audienceBadge: String,
    val prompt: String,
    val recommendedTone: DifficultyTone,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: RehearsalViewModel,
    modifier: Modifier = Modifier
) {
    val goal by viewModel.goalInput.collectAsState()
    val selectedTone by viewModel.selectedTone.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val todaySessions by viewModel.todaySessionCount.collectAsState()
    val isLoading by viewModel.isSetupLoading.collectAsState()
    val error by viewModel.setupError.collectAsState()
    val audioRms by viewModel.speechManager.audioRms.collectAsState()
    val isSpeaking by viewModel.speechManager.isSpeaking.collectAsState()
    val studioAudioRms by viewModel.studioAudioRms.collectAsState()
    val isStudioRecording by viewModel.isStudioRecording.collectAsState()
    val isSimulated by viewModel.isSimulatedStreamActive.collectAsState()
    val studioDecibels by viewModel.studioDecibels.collectAsState()
    val vocalResonance by viewModel.vocalResonanceScore.collectAsState()
    val isStudioActive = isStudioRecording || isSimulated

    var isMicTesting by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All Fields") }
    var isLargeTextMode by remember { mutableStateOf(false) }

    val universalCategories = listOf(
        "All Fields",
        "Students & Kids",
        "Job Seekers",
        "Healthcare",
        "Executive & Tech",
        "Seniors & Family"
    )

    val universalPresets = listOf(
        // Students & Kids
        UniversalPreset(
            title = "School Science Presentation",
            category = "Students & Kids",
            audienceBadge = "Grade School / College",
            prompt = "Explaining solar panel energy conversion to school science judges without rushing or fidgeting",
            recommendedTone = DifficultyTone.SUPPORTIVE,
            icon = Icons.Default.School
        ),
        UniversalPreset(
            title = "College Viva & Thesis Defense",
            category = "Students & Kids",
            audienceBadge = "University / MUN",
            prompt = "University DBMS & Operating Systems Viva oral defense on B+ trees, ACID transactions, and deadlocks",
            recommendedTone = DifficultyTone.STANDARD,
            icon = Icons.Default.School
        ),
        UniversalPreset(
            title = "Overcoming Speaking Fear",
            category = "Students & Kids",
            audienceBadge = "Youth Confidence",
            prompt = "Practicing 60 seconds of calm storytelling aloud to conquer stage fright and build daily speaking poise",
            recommendedTone = DifficultyTone.SUPPORTIVE,
            icon = Icons.Default.School
        ),

        // Job Seekers & Daily Workers (For Everyone, Rich or Poor)
        UniversalPreset(
            title = "Entry-Level Job Interview",
            category = "Job Seekers",
            audienceBadge = "First Job / Career Start",
            prompt = "Answering 'Tell me about yourself' concisely with genuine enthusiasm and relevant work ethic",
            recommendedTone = DifficultyTone.SUPPORTIVE,
            icon = Icons.Default.Work
        ),
        UniversalPreset(
            title = "Retail Customer De-escalation",
            category = "Job Seekers",
            audienceBadge = "Service Workers",
            prompt = "De-escalating an angry retail customer whose delivery was delayed with calm, structured solutions",
            recommendedTone = DifficultyTone.STANDARD,
            icon = Icons.Default.Work
        ),
        UniversalPreset(
            title = "Shift & Wage Adjustment",
            category = "Job Seekers",
            audienceBadge = "Hourly Workforce",
            prompt = "Requesting a fair schedule adjustment or wage review with honest clarity and reliability records",
            recommendedTone = DifficultyTone.STANDARD,
            icon = Icons.Default.Work
        ),

        // Healthcare & Essential Service
        UniversalPreset(
            title = "Doctor-Patient Empathy",
            category = "Healthcare",
            audienceBadge = "Clinical Consult",
            prompt = "Explaining diagnostic findings and next care steps compassionately to a worried patient and family",
            recommendedTone = DifficultyTone.SUPPORTIVE,
            icon = Icons.Default.Favorite
        ),
        UniversalPreset(
            title = "Clinical Shift Handoff (SBAR)",
            category = "Healthcare",
            audienceBadge = "Nursing & Medical",
            prompt = "Delivering a crisp, error-free clinical patient handoff using the SBAR protocol under hospital time pressure",
            recommendedTone = DifficultyTone.TOUGH,
            icon = Icons.Default.CrisisAlert
        ),

        // Executive & Tech
        UniversalPreset(
            title = "FAANG System Design",
            category = "Executive & Tech",
            audienceBadge = "Google / Meta",
            prompt = "Defending distributed cache invalidation and database partition tolerances during an L6/L7 Staff Tech Screen",
            recommendedTone = DifficultyTone.TOUGH,
            icon = Icons.Default.Business
        ),
        UniversalPreset(
            title = "YC 60s Demo Day Pitch",
            category = "Executive & Tech",
            audienceBadge = "Silicon Valley",
            prompt = "Delivering a 60-second venture pitch for an AI B2B developer tool with \$45k MRR and 25% MoM organic growth",
            recommendedTone = DifficultyTone.TOUGH,
            icon = Icons.Default.RocketLaunch
        ),
        UniversalPreset(
            title = "25% Comp Increase",
            category = "Executive & Tech",
            audienceBadge = "Executive",
            prompt = "Negotiating executive base salary, 25% compensation bump, and RSU equity refreshers with verified market benchmarking",
            recommendedTone = DifficultyTone.STANDARD,
            icon = Icons.Default.TrendingUp
        ),

        // Seniors & Family Life
        UniversalPreset(
            title = "Family 50th Anniversary Toast",
            category = "Seniors & Family",
            audienceBadge = "Life & Family",
            prompt = "Giving a warm, heartfelt toast at a family wedding anniversary celebrating 50 years of love and resilience",
            recommendedTone = DifficultyTone.SUPPORTIVE,
            icon = Icons.Default.Favorite
        ),
        UniversalPreset(
            title = "Voice Vitality & Diaphragmatic Breath",
            category = "Seniors & Family",
            audienceBadge = "Speech Longevity",
            prompt = "Practicing measured, deep-belly vocal resonance to maintain vocal strength, respiratory health, and clear diction",
            recommendedTone = DifficultyTone.SUPPORTIVE,
            icon = Icons.Default.Favorite
        )
    )

    val filteredPresets = if (selectedCategory == "All Fields") {
        universalPresets
    } else {
        universalPresets.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Branding Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Vocalis Geometric "V" Monogram
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(listOf(VocalisCyan, PolarisIndigoSecondary))
                        )
                        .padding(1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(11.dp))
                            .background(VocalisObsidian),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "V",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = VocalisCyan
                        )
                    }
                }

                Column {
                    Text(
                        text = "Vocalis",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Executive Speech & Interview Intelligence",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Real-time Acoustic Wave Visualizer Chamber (Direct Hero Access)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo(Screen.VocalStudio) }
                .testTag("home_vocal_studio_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = VocalisSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                    listOf(
                        if (isStudioActive) VocalisCyan.copy(alpha = 0.6f) else VocalisGlassBorder,
                        VocalisCardBorder
                    )
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(VocalisCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = VocalisCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "ACOUSTIC STUDIO & WAVE VISUALIZER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = VocalisCyan
                            )
                            Text(
                                text = if (isStudioActive) "Live Audio Stream • ${studioDecibels.toInt()} dBFS" else "Tap to launch DSP Acoustic Lab",
                                fontSize = 10.sp,
                                color = if (isStudioActive) VocalisEmerald else VocalisTextMuted
                            )
                        }
                    }

                    // Quick live toggle button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isStudioActive) VocalisCyan.copy(alpha = 0.2f) else VocalisSurfaceElevated)
                            .border(1.dp, if (isStudioActive) VocalisCyan else VocalisCardBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.toggleStudioSimulatedStream() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isStudioActive) "WAVE ACTIVE" else "TEST WAVE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isStudioActive) VocalisCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // The Real-Time Wave Visualizer in compact chamber mode
                RealtimeWaveformVisualizer(
                    audioLevel = if (isStudioActive) studioAudioRms else audioRms,
                    isAudioActive = isStudioActive || isSpeaking,
                    height = 80.dp,
                    showGrid = false,
                    accentColor = VocalisCyan,
                    secondaryColor = VocalisIndigo
                )

                // Telemetry Pills Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(VocalisSurfaceElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "RES: $vocalResonance%",
                                fontSize = 9.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = VocalisSky
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(VocalisSurfaceElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${studioDecibels.toInt()} dBFS",
                                fontSize = 9.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = VocalisEmerald
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Open Vocal Studio",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VocalisCyan
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = VocalisCyan,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }

        // Executive Command Center Quick Access Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tile 1: Keynote Teleprompter
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo(Screen.VocalStudio) }
                    .testTag("home_launch_teleprompter"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = VocalisSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VocalisCardBorder, VocalisCardBorder)))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(VocalisAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FormatQuote, contentDescription = null, tint = VocalisAmber, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Teleprompter", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VocalisTextPrimary)
                    Text("Keynote Orator", fontSize = 8.sp, color = VocalisTextMuted)
                }
            }

            // Tile 2: Acoustic Pitch & Wave
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo(Screen.VocalStudio) }
                    .testTag("home_launch_dsp_lab"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = VocalisSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VocalisCardBorder, VocalisCardBorder)))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(VocalisEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = VocalisEmerald, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Acoustic DSP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VocalisTextPrimary)
                    Text("F0 Pitch & EQ", fontSize = 8.sp, color = VocalisTextMuted)
                }
            }

            // Tile 3: Speech Blitz Game
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo(Screen.GameArena) }
                    .testTag("home_launch_game"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = VocalisSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VocalisCardBorder, VocalisCardBorder)))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(VocalisIndigo.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = VocalisIndigo, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Speech Blitz", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VocalisTextPrimary)
                    Text("Verbal Agility", fontSize = 8.sp, color = VocalisTextMuted)
                }
            }
        }

        // World-First Innovation Banner: Neural Charisma Twin & Logic Pyramid X-Ray
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo(Screen.NeuroTwin) }
                .testTag("home_launch_neuro_twin"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = VocalisSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                    listOf(VocalisEmerald.copy(alpha = 0.7f), VocalisAmber.copy(alpha = 0.5f))
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(VocalisEmerald, VocalisSky))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "CHARISMA TWIN & LOGIC X-RAY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = VocalisEmerald
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(VocalisAmber.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("NEW", fontSize = 8.sp, fontWeight = FontWeight.Black, color = VocalisAmber)
                            }
                        }
                        Text(
                            text = "Audio Clone Re-enactments • Minto Pyramid • Vagal Reset",
                            fontSize = 9.sp,
                            color = VocalisTextMuted,
                            maxLines = 1
                        )
                    }
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = VocalisEmerald,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Advanced Executive Suite Banner: Boardroom Panel, Camera Mirror, Redlines
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.toggleBoardroomMode()
                    viewModel.navigateTo(Screen.Practice)
                }
                .testTag("home_launch_boardroom_panel"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = VocalisSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                    listOf(VocalisCyan.copy(alpha = 0.7f), PolarisIndigoSecondary.copy(alpha = 0.5f))
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(VocalisCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = VocalisCyan, modifier = Modifier.size(22.dp))
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("BOARDROOM PANEL & GAZE MIRROR", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(VocalisCyan)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("PRO", fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            }
                        }
                        Text(
                            text = "3-Executive Cross-Examination • Front Camera Mirror • AI Redlines",
                            fontSize = 9.sp,
                            color = VocalisTextMuted,
                            maxLines = 1
                        )
                    }
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = VocalisCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // World-Class Verbal Sparring Banner: AI Debate Gauntlet
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo(Screen.DebateGauntlet) }
                .testTag("home_launch_debate_gauntlet"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = VocalisSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFFF5252).copy(alpha = 0.8f), VocalisAmber.copy(alpha = 0.6f))
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF5252).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(22.dp))
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("AI DEBATE GAUNTLET", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFF5252))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("ARENA", fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            }
                        }
                        Text(
                            text = "60s Rapid-Fire Cross-Examination • VC & Hostile M&A Adversaries",
                            fontSize = 9.sp,
                            color = VocalisTextMuted,
                            maxLines = 1
                        )
                    }
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Universal Life & Field Scenarios (Everyone: Kids, Students, Job Seekers, Healthcare, Executive, Seniors)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "UNIVERSAL LIFE & FIELD SCENARIOS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = PolarisBluePrimary
            )

            // Category Filter Pills
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(universalCategories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) PolarisBluePrimary else VocalisSurface)
                            .border(
                                1.dp,
                                if (isSelected) PolarisBluePrimary else VocalisCardBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else VocalisTextSecondary
                        )
                    }
                }
            }

            // Filtered Presets Carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredPresets) { preset ->
                    Card(
                        modifier = Modifier
                            .width(230.dp)
                            .clickable {
                                viewModel.goalInput.value = preset.prompt
                                viewModel.setTone(preset.recommendedTone)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (goal == preset.prompt) PolarisBluePrimary.copy(alpha = 0.15f) else VocalisSurface
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = if (goal == preset.prompt) Brush.linearGradient(listOf(PolarisBluePrimary, PolarisIndigoSecondary))
                            else Brush.linearGradient(listOf(PolarisDarkBorder, PolarisDarkBorder))
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(
                                        imageVector = preset.icon,
                                        contentDescription = null,
                                        tint = PolarisBluePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = preset.audienceBadge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PolarisBluePrimary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PolarisIndigoSecondary.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = preset.recommendedTone.displayName,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PolarisIndigoSecondary
                                    )
                                }
                            }

                            Text(
                                text = preset.title,
                                fontSize = if (isLargeTextMode) 14.sp else 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = preset.prompt,
                                fontSize = if (isLargeTextMode) 12.sp else 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                lineHeight = if (isLargeTextMode) 16.sp else 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Universal Accessibility & Offline Equity Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(VocalisSurface)
                .border(1.dp, VocalisCardBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(VocalisEmerald)
                )
                Text(
                    text = "Zero Data Needed • 100% Free Offline Mode",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = VocalisEmerald
                )
            }

            // Senior / Child Large Text Mode Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isLargeTextMode) VocalisAmber.copy(alpha = 0.2f) else VocalisSurfaceElevated)
                    .clickable { isLargeTextMode = !isLargeTextMode }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isLargeTextMode) "Large Text ON" else "Large Text",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLargeTextMode) VocalisAmber else VocalisTextSecondary
                )
            }
        }

        // Hero Rehearsal Input Studio
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    listOf(PolarisBluePrimary.copy(alpha = 0.4f), PolarisIndigoSecondary.copy(alpha = 0.15f))
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Target Conversation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Free-Text AI Persona",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PolarisBluePrimary
                    )
                }

                OutlinedTextField(
                    value = goal,
                    onValueChange = { viewModel.goalInput.value = it },
                    placeholder = {
                        Text(
                            text = "e.g. School presentation, first job interview, hospital handoff, salary review, family toast...",
                            style = if (isLargeTextMode) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    trailingIcon = {
                        if (goal.isNotBlank()) {
                            IconButton(onClick = { viewModel.goalInput.value = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear input",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("goal_input_field"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PolarisBluePrimary,
                        unfocusedBorderColor = PolarisDarkBorder,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (goal.isNotBlank()) viewModel.startSession(goal, selectedTone)
                    })
                )

                // Difficulty / Tone Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Choose Rehearsal Adversary Tone:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyTone.values().forEach { tone ->
                            val isSelected = selectedTone == tone
                            val isLocked = tone.requiresPro && !isPremium

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setTone(tone) }
                                    .testTag("tone_button_${tone.name.lowercase()}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) PolarisBluePrimary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surface
                                ),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = if (isSelected) Brush.linearGradient(listOf(PolarisBluePrimary, PolarisIndigoSecondary))
                                    else Brush.linearGradient(listOf(PolarisDarkBorder, PolarisDarkBorder))
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = tone.displayName,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = if (isSelected) PolarisBluePrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isLocked) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Pro required",
                                                tint = PolarisAmberGold,
                                                modifier = Modifier.size(11.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = when (tone) {
                                            DifficultyTone.SUPPORTIVE -> "Encouraging"
                                            DifficultyTone.STANDARD -> "Balanced"
                                            DifficultyTone.TOUGH -> "Adversarial"
                                        },
                                        fontSize = 10.sp,
                                        color = if (isSelected) PolarisBluePrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Mic & Acoustic Calibration Pill
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isMicTesting = !isMicTesting
                    if (isMicTesting) {
                        viewModel.startListening()
                    } else {
                        viewModel.stopListening()
                    }
                },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isMicTesting) PolarisBluePrimary.copy(alpha = 0.12f) else VocalisSurface
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(PolarisDarkBorder, PolarisDarkBorder))
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Mic Test",
                        tint = if (isMicTesting) ScoreGreen else PolarisBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = if (isMicTesting) "Testing Microphone Audio [LIVE]" else "Quick Acoustic & Mic Check",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isMicTesting) "Signal: ${audioRms.toInt()} dB (Speak to calibrate)" else "Tap to verify input audio before rehearsal",
                            fontSize = 10.sp,
                            color = if (isMicTesting) ScoreGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isMicTesting) ScoreGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isMicTesting) "TESTING..." else "CALIBRATE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMicTesting) ScoreGreen else Color.White
                    )
                }
            }
        }

        // Error message if any
        AnimatedVisibility(visible = error != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Start Rehearsal CTA Button
        Button(
            onClick = { viewModel.startSession(goal, selectedTone) },
            enabled = goal.isNotBlank() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("start_rehearsal_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PolarisBluePrimary,
                contentColor = Color(0xFF0F172A)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.Black,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Synthesizing Executive Persona...", fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Initialize Rehearsal Studio",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Frontier Astra 6 Neural Diagnostic Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.openAstraAnalysis() }
                .testTag("card_astra_frontier"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    listOf(PolarisBluePrimary.copy(alpha = 0.6f), PolarisIndigoSecondary.copy(alpha = 0.4f), PolarisAmberGold.copy(alpha = 0.5f))
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(PolarisBluePrimary, PolarisIndigoSecondary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "VOCALIS COGNITIVE LAB",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = PolarisBluePrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PolarisAmberGold)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("6-AXIS RADAR", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            }
                        }
                        Text(
                            text = "Interactive Neural Matrix & Sentence Surgeon",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open Astra Matrix",
                    tint = PolarisAmberGold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Coaching Philosophy Highlights
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                Triple(Icons.Default.Mic, "Zero Latency Voice", "Native Android TTS & Speech"),
                Triple(Icons.Default.AutoAwesome, "Adaptive Persona", "Dynamic Gemini Socratic Model"),
                Triple(Icons.Default.WorkspacePremium, "RevenueCat Powered", "2026 Global Shipaton Entry")
            ).forEach { (icon, title, subtitle) ->
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = PolarisBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 10.sp
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 8.sp,
                            lineHeight = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
