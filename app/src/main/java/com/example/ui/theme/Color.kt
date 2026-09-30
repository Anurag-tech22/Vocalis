package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Vocalis Executive Carbon & Emerald Dark Palette (Zero Blue, Luxury Tech / Bloomberg / Teenage Engineering Inspired)
val VocalisObsidian = Color(0xFF0C0E0D)         // Deep warm carbon obsidian background (0% blue)
val VocalisSurface = Color(0xFF141815)          // Dark carbon graphite surface
val VocalisSurfaceElevated = Color(0xFF1C221E)  // Elevated carbon titanium container
val VocalisCardBorder = Color(0xFF27312A)       // Hairline titanium border
val VocalisGlassBorder = Color(0x3300E599)      // Translucent cyber emerald glass border

// Core Accent Brand Tokens (Vivid Cyber Emerald & Solar Gold)
val VocalisEmerald = Color(0xFF00E599)          // Vivid Cyber Emerald / Neon Mint (Primary)
val VocalisCyan = VocalisEmerald                // Aliased to Cyber Emerald (no blue)
val VocalisSky = Color(0xFF10B981)              // Rich Mint Emerald Accent
val VocalisAmber = Color(0xFFFFB800)            // Imperial Solar Gold / Warm Amber
val VocalisIndigo = Color(0xFFA855F7)           // Royal Amethyst Purple (Replaces Indigo/Blue)
val VocalisPurple = Color(0xFFA855F7)           // Royal Amethyst Purple
val VocalisCoral = Color(0xFFFF7A45)            // Warm Sunset Coral / Copper
val VocalisCrimson = Color(0xFFEF4444)          // Socratic Pressure Red / Warning

// Typography & Contrast Tokens
val VocalisTextPrimary = Color(0xFFF5F7F6)      // Pure crisp titanium white
val VocalisTextSecondary = Color(0xFFA1ABA4)    // Warm neutral slate
val VocalisTextMuted = Color(0xFF6B776E)        // Subtle helper carbon

// Legacy Aliases for Seamless Backwards Compatibility
val PolarisBluePrimary = VocalisEmerald
val PolarisBlueOnPrimary = Color(0xFF003822)
val PolarisPrimaryContainer = Color(0xFF005234)
val PolarisOnPrimaryContainer = Color(0xFF6FFFC4)

val PolarisIndigoSecondary = VocalisPurple
val PolarisSecondaryContainer = Color(0xFF3B1861)
val PolarisOnSecondaryContainer = Color(0xFFF3E8FF)

val PolarisAmberGold = VocalisAmber
val PolarisAmberContainer = Color(0xFF78350F)
val PolarisOnAmberContainer = Color(0xFFFEF3C7)

val PolarisDarkBackground = VocalisObsidian
val PolarisDarkSurface = VocalisSurface
val PolarisDarkSurfaceVariant = VocalisSurfaceElevated
val PolarisDarkBorder = VocalisCardBorder

val PolarisTextPrimary = VocalisTextPrimary
val PolarisTextSecondary = VocalisTextSecondary
val PolarisTextMuted = VocalisTextMuted

val ScoreGreen = VocalisEmerald
val ScoreAmber = VocalisAmber
val ScoreRed = VocalisCrimson
val VoiceActiveGlow = VocalisEmerald
