package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.billing.SubscriptionPackage
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.PolarisDarkBorder
import com.example.ui.theme.PolarisIndigoSecondary
import com.example.ui.theme.ScoreGreen
import com.example.viewmodel.RehearsalViewModel

@Composable
fun PaywallScreen(
    viewModel: RehearsalViewModel,
    modifier: Modifier = Modifier
) {
    val customerInfo by viewModel.customerInfo.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val isPurchasing by viewModel.isPurchasing.collectAsState()
    val promoMessage by viewModel.promoMessage.collectAsState()
    val promoError by viewModel.promoError.collectAsState()
    val todaySessions by viewModel.todaySessionCount.collectAsState()

    val offerings = viewModel.offerings
    var selectedPackageId by remember { mutableStateOf("rc_annual_pro") }
    var promoCodeInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // RevenueCat Verified Badge & Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            PolarisAmberGold.copy(alpha = 0.2f),
                            PolarisIndigoSecondary.copy(alpha = 0.2f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(PolarisAmberGold, PolarisBluePrimary)),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "RevenueCat",
                        tint = PolarisAmberGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Powered by RevenueCat",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = PolarisAmberGold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(PolarisAmberGold, PolarisBluePrimary))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "Vocalis Pro",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Unlock unlimited rehearsals, tough mode stress-testing, FAANG system design screens, and YC pitch labs before high-stakes moments.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                // Current Tier Indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isPremium) PolarisAmberGold else PolarisBluePrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isPremium) {
                            if (customerInfo.isTrialActive) "ACTIVE: 7-DAY FREE TRIAL (${customerInfo.trialDaysRemaining} DAYS LEFT)"
                            else "ACTIVE: ${customerInfo.activePackageTitle ?: "VOCALIS PRO"}"
                        } else {
                            "CURRENT: FREE TIER (3 REHEARSALS/DAY)"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPremium) Color.Black else PolarisBluePrimary
                    )
                }
            }
        }

        // Feedback Banner
        AnimatedVisibility(visible = promoMessage != null || promoError != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (promoMessage != null) ScoreGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = promoMessage ?: promoError ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (promoMessage != null) ScoreGreen else MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.clearPromoFeedback() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("✕", fontSize = 12.sp)
                    }
                }
            }
        }

        // RevenueCat Offerings Package Cards (HAMM Strategy: Tiered Pricing)
        Text(
            text = "Select Your Plan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            offerings.forEach { pkg ->
                val isSelected = selectedPackageId == pkg.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedPackageId = pkg.id }
                        .testTag("package_${pkg.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PolarisBluePrimary.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            if (isSelected) listOf(PolarisBluePrimary, PolarisAmberGold)
                            else listOf(PolarisDarkBorder, PolarisDarkBorder)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = pkg.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                pkg.badge?.let { badgeText ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PolarisAmberGold)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = badgeText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                            Text(
                                text = pkg.periodDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            if (pkg.hasFreeTrial) {
                                Text(
                                    text = "★ Includes ${pkg.trialDays}-Day Free Trial",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ScoreGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = pkg.price,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) PolarisBluePrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, if (isSelected) PolarisBluePrimary else PolarisDarkBorder, CircleShape)
                                    .background(if (isSelected) PolarisBluePrimary else Color.Transparent)
                            )
                        }
                    }
                }
            }
        }

        // Primary Subscription CTA Button
        Button(
            onClick = {
                val selectedPkg = offerings.find { it.id == selectedPackageId }
                if (selectedPkg?.hasFreeTrial == true && !isPremium) {
                    viewModel.startFreeTrial()
                } else {
                    viewModel.purchasePackage(selectedPackageId)
                }
            },
            enabled = !isPurchasing,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("subscribe_primary_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = PolarisAmberGold,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isPurchasing) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Processing RevenueCat Purchase...", fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val selectedPkg = offerings.find { it.id == selectedPackageId }
                Text(
                    text = if (isPremium) "Change / Renew Subscription"
                    else if (selectedPkg?.hasFreeTrial == true) "Start 7-Day Free Trial"
                    else "Subscribe with RevenueCat",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // Judge & Sponsor Promo Code System (REQUIRED by Shipaton Rules)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = PolarisAmberGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Shipaton Judge Promo Code",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Shipaton judges can enter SHIPATON2026 or JUDGE2026 below to unlock 100% of Pro features with zero payment.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = promoCodeInput,
                        onValueChange = { promoCodeInput = it.uppercase() },
                        placeholder = { Text("e.g. SHIPATON2026") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("promo_code_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PolarisAmberGold,
                            unfocusedBorderColor = PolarisDarkBorder
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            viewModel.redeemPromoCode(promoCodeInput)
                        })
                    )

                    Button(
                        onClick = { viewModel.redeemPromoCode(promoCodeInput) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PolarisBluePrimary, contentColor = Color.Black),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("redeem_promo_button")
                    ) {
                        Text("Redeem", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Tier Comparison Table
        Text(
            text = "Feature Matrix",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Capability", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1.8f))
                    Text("Free", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                    Text("Pro", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = PolarisAmberGold, modifier = Modifier.weight(1f))
                }

                HorizontalDivider(color = PolarisDarkBorder)

                ComparisonRow(
                    feature = "Daily Rehearsals",
                    freeValue = "3 / day ($todaySessions used)",
                    proValue = "Unlimited",
                    isProHighlighted = true
                )

                ComparisonRow(
                    feature = "Manager Lab (Leadership)",
                    freeValue = "Standard",
                    proValue = "All Scenarios",
                    isProHighlighted = true
                )

                ComparisonRow(
                    feature = "Tough Mode (Stress Test)",
                    freeValue = "Locked",
                    proValue = "Unlocked",
                    isProHighlighted = true
                )

                ComparisonRow(
                    feature = "Downloadable Prep Report",
                    freeValue = "None",
                    proValue = "Included",
                    isProHighlighted = true
                )

                ComparisonRow(
                    feature = "Voice-to-Voice Loop",
                    freeValue = "Included",
                    proValue = "High Priority",
                    isProHighlighted = false
                )
            }
        }

        // Secondary Actions (Restore Purchases & Test Tier Reset)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.restorePurchases() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Restore Purchases", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { viewModel.resetToFreeTierForTesting() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset for Testing", fontSize = 12.sp)
            }
        }

        // Customer Info Footer for Judging Audit
        Text(
            text = "RevenueCat App User ID: ${customerInfo.appUserId}\nEntitlement: ${customerInfo.activeEntitlement ?: "None"} • Status: ${if (isPremium) "Entitled (Active)" else "Free Tier"}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontSize = 10.sp,
            lineHeight = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ComparisonRow(
    feature: String,
    freeValue: String,
    proValue: String,
    isProHighlighted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.8f)
        )
        Text(
            text = freeValue,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp
        )
        Text(
            text = proValue,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (isProHighlighted) PolarisAmberGold else ScoreGreen,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp
        )
    }
}

