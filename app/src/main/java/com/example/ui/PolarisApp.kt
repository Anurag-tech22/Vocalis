package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Screen
import com.example.ui.components.PaywallGateDialog
import com.example.ui.screens.AstraAnalysisScreen
import com.example.ui.screens.DebateGauntletScreen
import com.example.ui.screens.GameArenaScreen
import com.example.ui.screens.GameRoundScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeadershipLabScreen
import com.example.ui.screens.NeuroTwinScreen
import com.example.ui.screens.PaywallScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.VocalStudioScreen
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.PolarisDarkSurface
import com.example.ui.theme.PolarisIndigoSecondary
import com.example.viewmodel.RehearsalViewModel

@Composable
fun PolarisApp(
    viewModel: RehearsalViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val paywallReason by viewModel.paywallReason.collectAsState()

    val isFullScreenSession = currentScreen is Screen.Practice || currentScreen is Screen.GameRound || currentScreen is Screen.AstraAnalysis || currentScreen is Screen.DebateGauntlet

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Hide bottom bar during active sessions to allow 100% immersive focus
            if (!isFullScreenSession) {
                NavigationBar(
                    containerColor = PolarisDarkSurface,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.Home,
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Rehearse"
                            )
                        },
                        label = {
                            Text(
                                text = "Rehearse",
                                fontWeight = if (currentScreen == Screen.Home) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = PolarisBluePrimary,
                            indicatorColor = PolarisBluePrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_rehearse")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.VocalStudio,
                        onClick = { viewModel.navigateTo(Screen.VocalStudio) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Studio & Prompter"
                            )
                        },
                        label = {
                            Text(
                                text = "Studio & Prompter",
                                fontWeight = if (currentScreen == Screen.VocalStudio) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 9.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = PolarisBluePrimary,
                            indicatorColor = PolarisBluePrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_studio")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.LeadershipLab,
                        onClick = { viewModel.navigateTo(Screen.LeadershipLab) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Executive Lab"
                            )
                        },
                        label = {
                            Text(
                                text = "Executive Lab",
                                fontWeight = if (currentScreen == Screen.LeadershipLab) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = PolarisIndigoSecondary,
                            indicatorColor = PolarisIndigoSecondary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_leadership")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.GameArena,
                        onClick = { viewModel.navigateTo(Screen.GameArena) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = "Charisma Game"
                            )
                        },
                        label = {
                            Text(
                                text = "Speech Blitz",
                                fontWeight = if (currentScreen == Screen.GameArena) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = PolarisAmberGold,
                            indicatorColor = PolarisAmberGold,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_game")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.History,
                        onClick = { viewModel.navigateTo(Screen.History) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History"
                            )
                        },
                        label = {
                            Text(
                                text = "History",
                                fontWeight = if (currentScreen == Screen.History) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = PolarisBluePrimary,
                            indicatorColor = PolarisBluePrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_history")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val screen = currentScreen
            when (screen) {
                Screen.Home -> HomeScreen(viewModel = viewModel)
                Screen.LeadershipLab -> LeadershipLabScreen(viewModel = viewModel)
                Screen.GameArena -> GameArenaScreen(viewModel = viewModel)
                is Screen.GameRound -> GameRoundScreen(viewModel = viewModel)
                Screen.Practice -> PracticeScreen(viewModel = viewModel)
                Screen.History -> HistoryScreen(viewModel = viewModel)
                Screen.Paywall -> PaywallScreen(viewModel = viewModel)
                Screen.VocalStudio -> VocalStudioScreen(viewModel = viewModel)
                Screen.NeuroTwin -> NeuroTwinScreen(viewModel = viewModel)
                Screen.DebateGauntlet -> DebateGauntletScreen(viewModel = viewModel)
                is Screen.SessionDetail -> HistoryScreen(viewModel = viewModel)
                is Screen.AstraAnalysis -> AstraAnalysisScreen(viewModel = viewModel, sessionId = screen.sessionId)
            }
        }
    }

    // Modal Paywall Gate Dialog
    paywallReason?.let { reason ->
        PaywallGateDialog(
            reason = reason,
            onDismiss = { viewModel.dismissPaywall() },
            onUpgrade = {
                viewModel.navigateTo(Screen.Paywall)
                viewModel.dismissPaywall()
            }
        )
    }
}
