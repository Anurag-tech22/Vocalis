package com.example.game

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.GameChallenge
import com.example.data.model.GameResult
import com.example.data.model.PlayerGameProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class CharismaGameManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("polaris_game_prefs", Context.MODE_PRIVATE)

    private val _playerProfile = MutableStateFlow(loadProfile())
    val playerProfile: StateFlow<PlayerGameProfile> = _playerProfile.asStateFlow()

    private val fillerKeywords = listOf(
        "um", "uh", "like", "basically", "actually", "literally", "you know", "kinda", "sort of"
    )

    private fun loadProfile(): PlayerGameProfile {
        val totalXp = prefs.getInt("player_xp", 0)
        val gamesPlayed = prefs.getInt("games_played", 0)
        val gamesWon = prefs.getInt("games_won", 0)
        val bestStreak = prefs.getInt("best_streak", 0)
        val totalFillersAvoided = prefs.getInt("fillers_avoided", 0)

        // Calculate Level and Title
        val (level, rankTitle, nextLevelXp) = calculateLevel(totalXp)

        // Check daily energy tokens (3 per day for free players)
        val lastDayTimestamp = prefs.getLong("last_energy_date", 0L)
        val currentDay = getStartOfDay()

        val energy = if (lastDayTimestamp < currentDay) {
            prefs.edit().putLong("last_energy_date", currentDay).putInt("energy_remaining", 3).apply()
            3
        } else {
            prefs.getInt("energy_remaining", 3)
        }

        return PlayerGameProfile(
            level = level,
            currentXp = totalXp,
            xpForNextLevel = nextLevelXp,
            rankTitle = rankTitle,
            gamesPlayed = gamesPlayed,
            gamesWon = gamesWon,
            bestStreak = bestStreak,
            totalFillersAvoided = totalFillersAvoided,
            energyRemaining = energy,
            maxEnergy = 3
        )
    }

    private fun calculateLevel(xp: Int): Triple<Int, String, Int> {
        return when {
            xp >= 2000 -> Triple(5, "Charisma Legend 👑", 3500)
            xp >= 1200 -> Triple(4, "Boardroom Master 🏆", 2000)
            xp >= 700 -> Triple(3, "Silver Tongue 🎙️", 1200)
            xp >= 300 -> Triple(2, "Confident Speaker ⚡", 700)
            else -> Triple(1, "Timid Whisperer 🌱", 300)
        }
    }

    private fun getStartOfDay(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun canPlay(isPro: Boolean): Boolean {
        if (isPro) return true
        return _playerProfile.value.energyRemaining > 0
    }

    fun consumeEnergy(isPro: Boolean) {
        if (isPro) return
        val current = _playerProfile.value.energyRemaining
        if (current > 0) {
            val updated = current - 1
            prefs.edit().putInt("energy_remaining", updated).apply()
            _playerProfile.value = _playerProfile.value.copy(energyRemaining = updated)
        }
    }

    fun detectFillersInTranscript(text: String): Pair<Int, List<String>> {
        val words = text.lowercase().split("\\s+".toRegex()).map { it.replace("[^a-z]".toRegex(), "") }
        val found = mutableListOf<String>()

        words.forEach { word ->
            if (word in fillerKeywords) {
                found.add(word)
            }
        }
        return Pair(found.size, found)
    }

    fun evaluateGameRound(
        challenge: GameChallenge,
        spokenText: String,
        elapsedSeconds: Int
    ): GameResult {
        val words = spokenText.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        val wordCount = words.size

        val effectiveSeconds = elapsedSeconds.coerceAtLeast(5)
        val wpm = ((wordCount.toFloat() / effectiveSeconds.toFloat()) * 60f).toInt()

        val (fillerCount, detectedFillers) = detectFillersInTranscript(spokenText)

        // Composure score: starts at 100, drops with filler words or low word count
        var composure = (100 - (fillerCount * 12)).coerceIn(15, 100)
        if (wordCount < 10) composure = (composure - 30).coerceAtLeast(10)

        // Star calculation
        val stars = when {
            wordCount >= 20 && fillerCount == 0 && composure >= 80 -> 3
            wordCount >= 12 && fillerCount <= 2 && composure >= 60 -> 2
            wordCount >= 5 -> 1
            else -> 1
        }

        // Base XP + Star bonus + Flawless bonus
        var xp = stars * 60
        if (fillerCount == 0 && wordCount >= 15) xp += 80 // Clean speech bonus
        if (wpm in 100..160) xp += 40 // Ideal conversational tempo

        // Badges
        val badge = when {
            fillerCount == 0 && wordCount >= 20 -> "🎖️ Flawless Eloquence (0 Fillers)"
            wpm in 120..150 -> "⚡ Ideal Flow Master (Pacing Gold)"
            stars == 3 -> "🌟 3-Star Master Communicator"
            else -> null
        }

        // Generate funny yet constructive feedback
        val feedback = when (stars) {
            3 -> "Sensational delivery! You commanded the room, avoided vocal traps, and articulated your thoughts with pure magnetic poise."
            2 -> "Solid verbal agility! A couple of filler words slipped in under pressure, but your core narrative stayed punchy and engaging."
            else -> "Good courage stepping into the arena! Focus on taking a deep 1-second breath instead of filling the silence with 'um' or 'like'."
        }

        // Update profile in memory and prefs
        val newTotalXp = _playerProfile.value.currentXp + xp
        val newGamesPlayed = _playerProfile.value.gamesPlayed + 1
        val newGamesWon = _playerProfile.value.gamesWon + (if (stars >= 2) 1 else 0)
        val newStreak = if (stars >= 2) _playerProfile.value.bestStreak + 1 else 0
        val newFillersAvoided = _playerProfile.value.totalFillersAvoided + (15 - fillerCount).coerceAtLeast(0)

        prefs.edit()
            .putInt("player_xp", newTotalXp)
            .putInt("games_played", newGamesPlayed)
            .putInt("games_won", newGamesWon)
            .putInt("best_streak", maxOf(newStreak, prefs.getInt("best_streak", 0)))
            .putInt("fillers_avoided", newFillersAvoided)
            .apply()

        val (level, rankTitle, nextLevelXp) = calculateLevel(newTotalXp)
        _playerProfile.value = _playerProfile.value.copy(
            level = level,
            currentXp = newTotalXp,
            xpForNextLevel = nextLevelXp,
            rankTitle = rankTitle,
            gamesPlayed = newGamesPlayed,
            gamesWon = newGamesWon,
            bestStreak = maxOf(newStreak, _playerProfile.value.bestStreak),
            totalFillersAvoided = newFillersAvoided
        )

        return GameResult(
            challengeId = challenge.id,
            stars = stars,
            xpEarned = xp,
            totalWords = wordCount,
            wordsPerMinute = wpm,
            fillerCount = fillerCount,
            fillersDetected = detectedFillers,
            composureScore = composure,
            feedback = feedback,
            badgeUnlocked = badge
        )
    }

    fun refillEnergyForDemo() {
        prefs.edit().putInt("energy_remaining", 3).apply()
        _playerProfile.value = _playerProfile.value.copy(energyRemaining = 3)
    }

    companion object {
        @Volatile
        private var INSTANCE: CharismaGameManager? = null

        fun getInstance(context: Context): CharismaGameManager {
            return INSTANCE ?: synchronized(this) {
                val instance = CharismaGameManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
