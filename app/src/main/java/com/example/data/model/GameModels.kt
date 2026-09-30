package com.example.data.model

data class GameChallenge(
    val id: String,
    val title: String,
    val category: GameCategory,
    val promptQuestion: String,
    val timeLimitSeconds: Int = 45,
    val forbiddenWords: List<String> = listOf("um", "uh", "like", "basically", "actually", "you know"),
    val targetKeypoints: List<String>,
    val scenarioHumor: String,
    val opponentName: String,
    val opponentTitle: String,
    val difficultyStars: Int = 2,
    val isProOnly: Boolean = false
)

enum class GameCategory(val displayName: String, val emoji: String, val description: String) {
    NO_FILLER("No-Filler Gauntlet", "⚡", "Speak fluently without saying 'um', 'uh', or 'like'"),
    SHARK_TANK("Shark Tank Blitz", "🚀", "Pitch wild inventions under demanding investor timer"),
    DE_ESCALATE("Crisis De-escalation", "🛡️", "Defuse hostile stakeholders before the panic meter fills"),
    STORY_DUEL("Impromptu Storyteller", "🧠", "Weave 3 random words into a compelling persuasive pitch"),
    SILENCE_BREAKER("Awkward Silence Saver", "💬", "Drop charming conversational bridges when things get tense")
}

data class GameResult(
    val challengeId: String,
    val stars: Int,
    val xpEarned: Int,
    val totalWords: Int,
    val wordsPerMinute: Int,
    val fillerCount: Int,
    val fillersDetected: List<String>,
    val composureScore: Int,
    val feedback: String,
    val badgeUnlocked: String? = null
)

data class PlayerGameProfile(
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpForNextLevel: Int = 300,
    val rankTitle: String = "Timid Whisperer",
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val bestStreak: Int = 0,
    val totalFillersAvoided: Int = 0,
    val energyRemaining: Int = 3,
    val maxEnergy: Int = 3
)
