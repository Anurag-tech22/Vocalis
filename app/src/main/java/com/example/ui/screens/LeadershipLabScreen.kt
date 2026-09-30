package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DifficultyTone
import com.example.data.model.LeadershipCatalog
import com.example.data.model.LeadershipScenario
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.PolarisDarkBorder
import com.example.ui.theme.PolarisIndigoSecondary
import com.example.ui.theme.ScoreGreen
import com.example.viewmodel.RehearsalViewModel

@Composable
fun LeadershipLabScreen(
    viewModel: RehearsalViewModel,
    modifier: Modifier = Modifier
) {
    val isPremium by viewModel.isPremium.collectAsState()
    val scenarios = LeadershipCatalog.scenarios
    val categories = listOf("All", "Tech Giants", "Silicon Valley", "Feedback", "Boundaries", "Saying No", "Conflict", "Performance", "Change")
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredScenarios = if (selectedCategory == "All") {
        scenarios
    } else {
        scenarios.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header: Leadership Heather Career Coaching Feature
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            PolarisIndigoSecondary.copy(alpha = 0.22f),
                            PolarisBluePrimary.copy(alpha = 0.15f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(PolarisIndigoSecondary, PolarisBluePrimary)),
                    RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(PolarisBluePrimary, PolarisIndigoSecondary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Executive & Tech Giant Lab",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "FAANG Screens, YC Pitches & Critical Friction Scenarios",
                            style = MaterialTheme.typography.labelSmall,
                            color = PolarisBluePrimary
                        )
                    }
                }

                Text(
                    text = "High-stakes communication simulations. Practice handling adversarial architectural pushback, VC interrogation, and boundary enforcement out loud.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) PolarisBluePrimary.copy(alpha = 0.25f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            1.dp,
                            if (isSelected) PolarisBluePrimary else PolarisDarkBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PolarisBluePrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Scenario List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredScenarios, key = { it.id }) { scenario ->
                val isLocked = scenario.difficulty == DifficultyTone.TOUGH && !isPremium

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            if (isLocked) {
                                viewModel.triggerPaywall("Tough Mode workplace scenarios are included in Polaris Pro. Upgrade or enter judge promo code.")
                            } else {
                                viewModel.launchLeadershipScenario(scenario)
                            }
                        }
                        .testTag("scenario_card_${scenario.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            if (isLocked) listOf(PolarisDarkBorder, PolarisDarkBorder)
                            else listOf(PolarisBluePrimary.copy(alpha = 0.3f), PolarisIndigoSecondary.copy(alpha = 0.15f))
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PolarisBluePrimary.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = scenario.category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PolarisBluePrimary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isLocked) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Pro required",
                                        tint = PolarisAmberGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "${scenario.difficulty.displayName} Pressure",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isLocked) PolarisAmberGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = scenario.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = scenario.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Counterpart: ${scenario.counterpartPersona}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PolarisAmberGold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Focus: ${scenario.coreSkillTested}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    fontSize = 10.sp
                                )
                            }

                            Button(
                                onClick = {
                                    if (isLocked) {
                                        viewModel.triggerPaywall("Tough Mode workplace scenarios are included in Polaris Pro. Upgrade or enter judge promo code.")
                                    } else {
                                        viewModel.launchLeadershipScenario(scenario)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLocked) PolarisAmberGold else PolarisBluePrimary,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isLocked) "Unlock" else "Practice",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
