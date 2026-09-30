package com.example.data.model

data class CognitiveRadarMetrics(
    val clarity: Int,          // 0-100: Articulation & precision
    val gravitas: Int,         // 0-100: Authority, weight, conviction
    val structure: Int,        // 0-100: Framework adherence (STAR/BLUF)
    val stressResilience: Int, // 0-100: Composure under interruption & pressure
    val vocalCadence: Int,     // 0-100: WPM pacing, rhythm & breath control
    val executiveBrevity: Int  // 0-100: Signal-to-noise ratio, zero fluff
) {
    val averageScore: Int
        get() = (clarity + gravitas + structure + stressResilience + vocalCadence + executiveBrevity) / 6
}

data class SpeechBiometrics(
    val wordsPerMinute: Int,
    val durationSeconds: Int,
    val totalWordsSpoken: Int,
    val fillerCount: Int,
    val fillerPercentage: Float,
    val detectedFillers: List<String>,
    val hedgeCount: Int,
    val detectedHedges: List<String>,
    val powerWordsCount: Int,
    val detectedPowerWords: List<String>,
    val latencyPauseMs: Long,
    val cadenceStatus: CadenceStatus
)

enum class CadenceStatus(val label: String, val description: String) {
    OPTIMAL("Optimal Cadence (130-155 WPM)", "Ideal executive pacing with deliberate authoritative pauses."),
    TOO_FAST("Rushed / High Anxiety (>165 WPM)", "Speech cadence indicates cognitive rush or nervous acceleration."),
    TOO_SLOW("Hesitant / Pauses (<115 WPM)", "Pacing suggests processing lag or low confidence projection.")
}

data class LinguisticSurgery(
    val originalExcerpt: String,
    val flawCategory: String,           // e.g. "Passive Hedging", "Vague Quantification", "Rambling Intro"
    val executiveLevel10Rewrite: String,
    val cognitivePrinciple: String      // e.g. "BLUF: Bottom Line Up Front", "Power Framing"
)

data class AstraDeepReport(
    val sessionId: Long = 0L,
    val overallIndex: Int,              // 0-100
    val readinessArchetype: String,     // e.g. "Visionary Strategist (Top 3%)", "Polished Operator"
    val archetypeDescription: String,
    val radar: CognitiveRadarMetrics,
    val biometrics: SpeechBiometrics,
    val surgeries: List<LinguisticSurgery>,
    val executiveSummary: String,
    val blindspots: List<String>,
    val highImpactPrescriptions: List<String>
)

data class StressCurveball(
    val triggerPrompt: String,
    val timeLimitSeconds: Int = 15,
    val objectionType: String           // e.g. "Budget Pushback", "Technical Disproof", "Professorial Probe"
)
