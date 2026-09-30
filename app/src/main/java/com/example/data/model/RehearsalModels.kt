package com.example.data.model

enum class DifficultyTone(val displayName: String, val description: String, val requiresPro: Boolean) {
    SUPPORTIVE("Supportive", "Encouraging, gentle guidance, constructive confidence-builder", false),
    STANDARD("Standard", "Realistic, balanced, professional real-world pressure", false),
    TOUGH("Tough", "Intense, skeptical, high-stakes stress-test & challenging follow-ups", true)
}

data class BoardroomPanelist(
    val id: String,
    val name: String,
    val role: String,
    val focusArea: String,
    val avatarInitials: String,
    val pitch: Float,
    val speechRate: Float,
    val accentColorHex: Long
)

data class ExecutiveRedlineItem(
    val originalText: String,
    val polishedText: String,
    val wordsRemoved: List<String>,
    val wordsAdded: List<String>,
    val blufStrength: String,
    val convictionMultiplier: String
)

data class ConversationTurn(
    val turnIndex: Int,
    val aiSpeaker: String,
    val aiText: String,
    val userSpeechText: String = "",
    val reactionText: String? = null,
    val score: Int? = null,
    val tip: String? = null,
    val speakerRole: String? = null,
    val panelistId: String? = null,
    val redlineOriginal: String? = null,
    val redlinePolished: String? = null,
    val cadenceWpm: Int? = null,
    val fillerWordsCount: Int? = null,
    val eyeContactScore: Int? = null
)

data class SessionSetupResponse(
    val scenarioType: String,
    val personaName: String,
    val personaRole: String,
    val contextBrief: String,
    val openingQuestion: String,
    val suggestedTips: List<String> = emptyList()
)

data class TurnEvaluation(
    val inCharacterReaction: String,
    val followUpQuestion: String,
    val scoreSoFar: Int,
    val verdictSoFar: String,
    val actionableTip: String,
    val isFinal: Boolean = false
)

data class FinalScoreCard(
    val overallScore: Int,
    val verdict: String,
    val strengths: List<String>,
    val areasForImprovement: List<String>,
    val readinessLevel: String
)

data class LeadershipScenario(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val counterpartPersona: String,
    val promptGoal: String,
    val coreSkillTested: String,
    val difficulty: DifficultyTone
)

data class DebateOpponent(
    val id: String,
    val name: String,
    val title: String,
    val organization: String,
    val objectionStrategy: String,
    val initialSalvo: String,
    val followUpCurveballs: List<String>,
    val pitch: Float,
    val speed: Float,
    val avatarInitials: String,
    val badgeColorHex: Long
)

sealed interface Screen {
    data object Home : Screen
    data object LeadershipLab : Screen
    data object GameArena : Screen
    data object Practice : Screen
    data object History : Screen
    data object Paywall : Screen
    data object VocalStudio : Screen
    data object NeuroTwin : Screen
    data object DebateGauntlet : Screen
    data class GameRound(val challengeId: String) : Screen
    data class SessionDetail(val sessionId: Long) : Screen
    data class AstraAnalysis(val sessionId: Long? = null) : Screen
}
