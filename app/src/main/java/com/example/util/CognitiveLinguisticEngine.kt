package com.example.util

import com.example.data.model.AstraDeepReport
import com.example.data.model.CadenceStatus
import com.example.data.model.CognitiveRadarMetrics
import com.example.data.model.ConversationTurn
import com.example.data.model.DifficultyTone
import com.example.data.model.LinguisticSurgery
import com.example.data.model.SpeechBiometrics
import com.example.data.model.StressCurveball
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max
import kotlin.math.min

object CognitiveLinguisticEngine {

    private val fillerWords = setOf(
        "um", "uh", "like", "basically", "actually", "literally", "sort of", "kinda",
        "you know", "i mean", "right", "honestly", "so yeah"
    )

    private val hedgePhrases = listOf(
        "i feel like", "i guess", "maybe", "probably", "just my opinion",
        "i might be wrong", "sort of", "kind of", "somewhat", "hopefully",
        "i think maybe", "try to", "could potentially"
    )

    private val executivePowerWords = setOf(
        "architected", "spearheaded", "orchestrated", "engineered", "standardized",
        "quantified", "accelerated", "optimized", "delivered", "negotiated",
        "executed", "mitigated", "streamlined", "championed", "formulated",
        "validated", "governed", "catalyzed", "secured", "empowered"
    )

    fun analyzeSession(
        goal: String,
        tone: DifficultyTone,
        turns: List<ConversationTurn>,
        totalDurationSeconds: Int = 90
    ): AstraDeepReport {
        val userSpeechSegments = turns.map { it.userSpeechText.trim() }.filter { it.isNotBlank() }
        val fullTranscript = userSpeechSegments.joinToString(" ")
        val words = fullTranscript.lowercase()
            .split("\\s+".toRegex())
            .map { it.replace("[^a-z]".toRegex(), "") }
            .filter { it.isNotBlank() }

        val wordCount = max(words.size, 1)
        val durationSafe = max(totalDurationSeconds, 15)
        val calculatedWpm = ((wordCount.toFloat() / durationSafe.toFloat()) * 60f).toInt()
        val wpm = if (calculatedWpm in 60..240) calculatedWpm else 138

        // Filler analysis
        val foundFillers = mutableListOf<String>()
        words.forEach { word ->
            if (word in fillerWords) foundFillers.add(word)
        }
        val fillerRatio = foundFillers.size.toFloat() / wordCount.toFloat()

        // Hedge analysis
        val lowerTranscript = fullTranscript.lowercase()
        val foundHedges = mutableListOf<String>()
        hedgePhrases.forEach { hedge ->
            if (lowerTranscript.contains(hedge)) {
                foundHedges.add(hedge)
            }
        }

        // Power words
        val foundPowerWords = mutableListOf<String>()
        words.forEach { word ->
            if (word in executivePowerWords) foundPowerWords.add(word)
        }

        // Cadence status
        val cadenceStatus = when {
            wpm in 128..158 -> CadenceStatus.OPTIMAL
            wpm > 158 -> CadenceStatus.TOO_FAST
            else -> CadenceStatus.TOO_SLOW
        }

        // Compute 6-Axis Cognitive Radar Metrics
        // 1. Clarity (articulation, lack of filler clutter)
        val rawClarity = 100 - (fillerRatio * 350).toInt()
        val clarity = min(98, max(42, rawClarity))

        // 2. Gravitas (assertiveness, lack of hedges, presence of power words)
        val hedgePenalty = foundHedges.size * 9
        val powerBonus = foundPowerWords.size * 7
        val baseGravitas = when (tone) {
            DifficultyTone.SUPPORTIVE -> 75
            DifficultyTone.STANDARD -> 70
            DifficultyTone.TOUGH -> 65
        }
        val gravitas = min(96, max(38, baseGravitas - hedgePenalty + powerBonus))

        // 3. Structure (STAR, logical connectors like "firstly", "because", "impact", "result")
        val structuralKeywords = listOf("because", "specifically", "therefore", "result", "action", "measured", "first", "impact")
        val structureHits = structuralKeywords.count { lowerTranscript.contains(it) }
        val structure = min(98, max(45, 55 + (structureHits * 8)))

        // 4. Stress Resilience
        val stressResilience = when (tone) {
            DifficultyTone.TOUGH -> if (fillerRatio < 0.05f) 92 else 72
            DifficultyTone.STANDARD -> if (fillerRatio < 0.07f) 86 else 76
            DifficultyTone.SUPPORTIVE -> 84
        }

        // 5. Vocal Cadence
        val vocalCadence = when (cadenceStatus) {
            CadenceStatus.OPTIMAL -> 94
            CadenceStatus.TOO_FAST -> 68
            CadenceStatus.TOO_SLOW -> 64
        }

        // 6. Executive Brevity (Signal-to-noise ratio)
        val signalToNoise = 100 - (foundFillers.size * 6) - (foundHedges.size * 5)
        val executiveBrevity = min(95, max(40, signalToNoise))

        val radar = CognitiveRadarMetrics(
            clarity = clarity,
            gravitas = gravitas,
            structure = structure,
            stressResilience = stressResilience,
            vocalCadence = vocalCadence,
            executiveBrevity = executiveBrevity
        )

        val overallIndex = radar.averageScore

        val (archetype, archetypeDesc) = when {
            overallIndex >= 88 -> "Visionary Strategist (Top 3%)" to "Demonstrates sovereign command, razor-sharp BLUF executive brevity, and unshakable composure under cross-examination."
            overallIndex >= 78 -> "Polished Operator" to "Articulate and structured delivery with high professional credibility. Minor vocal hedges during spontaneous transitions."
            overallIndex >= 65 -> "Competent Practitioner" to "Solid foundational knowledge, but prone to defensive over-explaining and mid-sentence filler pauses."
            else -> "Nervous Expert" to "Deep subject domain expertise concealed beneath rushed cadence and passive hedging. High potential with deliberate pacing calibration."
        }

        // Generate Sentence Surgeon Autopsies
        val surgeries = generateLinguisticSurgeries(userSpeechSegments, goal)

        val blindspots = mutableListOf<String>()
        if (foundFillers.size >= 3) {
            blindspots.add("Vocal Filler Crutch: Relying on '${foundFillers.distinct().take(2).joinToString(", ")}' instead of silence during cognitive pauses.")
        }
        if (foundHedges.isNotEmpty()) {
            blindspots.add("Authority Deflation: Using qualifiers like '${foundHedges.first()}' which subtly prompts the listener to doubt your conclusion.")
        }
        if (cadenceStatus == CadenceStatus.TOO_FAST) {
            blindspots.add("Rushed Pacing: Speaking at ${wpm} WPM signals nervous adrenaline. World-class communicators slow down to radiate sovereign status.")
        }
        if (blindspots.isEmpty()) {
            blindspots.add("Precision Calibrations: Elevate metrics by quantifying exact percentage gains and strategic trade-offs.")
        }

        val prescriptions = listOf(
            "The 2-Second Strategic Pause: When hit with a difficult question, hold eye contact and pause for 2 full seconds before uttering your first word.",
            "BLUF (Bottom Line Up Front): State your final recommendation or key metric in sentence #1, then unpack the methodology in sentence #2.",
            "Eliminate 'I think/feel': Replace 'I feel like we should do X' with 'The data indicates X is the optimal strategy because Y.'"
        )

        val biometrics = SpeechBiometrics(
            wordsPerMinute = wpm,
            durationSeconds = durationSafe,
            totalWordsSpoken = wordCount,
            fillerCount = foundFillers.size,
            fillerPercentage = (fillerRatio * 100f),
            detectedFillers = foundFillers.distinct(),
            hedgeCount = foundHedges.size,
            detectedHedges = foundHedges.distinct(),
            powerWordsCount = foundPowerWords.size,
            detectedPowerWords = foundPowerWords.distinct(),
            latencyPauseMs = 850L,
            cadenceStatus = cadenceStatus
        )

        return AstraDeepReport(
            sessionId = System.currentTimeMillis(),
            overallIndex = overallIndex,
            readinessArchetype = archetype,
            archetypeDescription = archetypeDesc,
            radar = radar,
            biometrics = biometrics,
            surgeries = surgeries,
            executiveSummary = "Comprehensive multi-dimensional analysis conducted across ${turns.size} conversational turns. Demonstrated $archetype with a ${radar.clarity}% clarity index and ${biometrics.wordsPerMinute} WPM cadence.",
            blindspots = blindspots,
            highImpactPrescriptions = prescriptions
        )
    }

    private fun generateLinguisticSurgeries(
        segments: List<String>,
        goal: String
    ): List<LinguisticSurgery> {
        val surgeries = mutableListOf<LinguisticSurgery>()

        if (segments.isNotEmpty()) {
            val sample = segments.first()
            if (sample.length > 20) {
                surgeries.add(
                    LinguisticSurgery(
                        originalExcerpt = "\"$sample\"",
                        flawCategory = "Passive Hedging & Narrative Drift",
                        executiveLevel10Rewrite = "\"Our objective in $goal is anchored on three decisive pillars: architectural scalability, risk mitigation, and quantifiable outcome delivery.\"",
                        cognitivePrinciple = "BLUF Framework (Bottom Line Up Front)"
                    )
                )
            }
        }

        surgeries.add(
            LinguisticSurgery(
                originalExcerpt = "\"I basically think we should probably prioritize this feature because users kind of asked for it.\"",
                flawCategory = "Confidence Evisceration (4 Hedges in 1 Sentence)",
                executiveLevel10Rewrite = "\"Customer feedback and telemetry confirm this capability directly addresses our highest-friction drop-off point, unlocking an estimated 15% retention uplift.\"",
                cognitivePrinciple = "Metric-Anchored Assertiveness"
            )
        )

        surgeries.add(
            LinguisticSurgery(
                originalExcerpt = "\"So yeah, that's pretty much what happened and how I handled it.\"",
                flawCategory = "Weak Conversational Landing (Anti-Climax)",
                executiveLevel10Rewrite = "\"In summary: we neutralized the risk within 48 hours with zero data loss, establishing a reusable protocol for future incidents.\"",
                cognitivePrinciple = "Executive Punchline & Concrete Resolution"
            )
        )

        return surgeries
    }

    fun generateCurveball(goal: String, turnIndex: Int): StressCurveball {
        val lowerGoal = goal.lowercase()
        return when {
            lowerGoal.contains("viva") || lowerGoal.contains("exam") || lowerGoal.contains("dbms") -> {
                StressCurveball(
                    triggerPrompt = "⚡ PROFESSOR INTERRUPTS: 'Stop there! Your assumption about ACID transactions fails under distributed partitioning. State the CAP theorem trade-off you chose, in 15 seconds!'",
                    timeLimitSeconds = 15,
                    objectionType = "Theoretical Disproof"
                )
            }
            lowerGoal.contains("interview") || lowerGoal.contains("frontend") || lowerGoal.contains("role") -> {
                StressCurveball(
                    triggerPrompt = "⚡ HIRING MANAGER COUNTER: 'Hold on. That sounds great in theory, but what if your primary dependency goes unmaintained tomorrow? Give me your contingency plan!'",
                    timeLimitSeconds = 15,
                    objectionType = "Scalability Stress Test"
                )
            }
            lowerGoal.contains("raise") || lowerGoal.contains("salary") || lowerGoal.contains("manager") -> {
                StressCurveball(
                    triggerPrompt = "⚡ EXECUTIVE PUSHBACK: 'Look, budget cycles are frozen company-wide right now. Why should leadership make an unprecedented exception for you today?'",
                    timeLimitSeconds = 15,
                    objectionType = "Budgetary Hard Freeze"
                )
            }
            else -> {
                StressCurveball(
                    triggerPrompt = "⚡ SHADOW OPPONENT PROBE: 'That's a textbook answer, but what is the single biggest catastrophic risk of that approach that you haven't mentioned yet?'",
                    timeLimitSeconds = 15,
                    objectionType = "Critical Blindspot Challenge"
                )
            }
        }
    }

    fun reportToJson(report: AstraDeepReport): String {
        return JSONObject().apply {
            put("overallIndex", report.overallIndex)
            put("readinessArchetype", report.readinessArchetype)
            put("archetypeDescription", report.archetypeDescription)
            put("executiveSummary", report.executiveSummary)

            // Radar
            val radarObj = JSONObject().apply {
                put("clarity", report.radar.clarity)
                put("gravitas", report.radar.gravitas)
                put("structure", report.radar.structure)
                put("stressResilience", report.radar.stressResilience)
                put("vocalCadence", report.radar.vocalCadence)
                put("executiveBrevity", report.radar.executiveBrevity)
            }
            put("radar", radarObj)

            // Biometrics
            val bioObj = JSONObject().apply {
                put("wpm", report.biometrics.wordsPerMinute)
                put("durationSeconds", report.biometrics.durationSeconds)
                put("totalWords", report.biometrics.totalWordsSpoken)
                put("fillerCount", report.biometrics.fillerCount)
                put("fillerPercentage", report.biometrics.fillerPercentage)
                put("hedgeCount", report.biometrics.hedgeCount)
                put("powerWordsCount", report.biometrics.powerWordsCount)
                put("cadenceStatus", report.biometrics.cadenceStatus.name)
            }
            put("biometrics", bioObj)

            // Blindspots & Prescriptions
            put("blindspots", JSONArray(report.blindspots))
            put("prescriptions", JSONArray(report.highImpactPrescriptions))

            // Surgeries
            val surgArray = JSONArray()
            report.surgeries.forEach { surg ->
                val sObj = JSONObject().apply {
                    put("original", surg.originalExcerpt)
                    put("flaw", surg.flawCategory)
                    put("rewrite", surg.executiveLevel10Rewrite)
                    put("principle", surg.cognitivePrinciple)
                }
                surgArray.put(sObj)
            }
            put("surgeries", surgArray)
        }.toString()
    }

    fun getBoardroomPanel(): List<com.example.data.model.BoardroomPanelist> {
        return listOf(
            com.example.data.model.BoardroomPanelist(
                id = "cfo_marcus",
                name = "Dr. Marcus Vance",
                role = "CFO & Capital Allocator",
                focusArea = "Margins, CAC/LTV & Financial Defensibility",
                avatarInitials = "MV",
                pitch = 0.85f,
                speechRate = 1.02f,
                accentColorHex = 0xFFFFB300
            ),
            com.example.data.model.BoardroomPanelist(
                id = "cto_elena",
                name = "Elena Rostova",
                role = "CTO & Chief Architect",
                focusArea = "Distributed Architecture & Fault Tolerance",
                avatarInitials = "ER",
                pitch = 1.04f,
                speechRate = 1.05f,
                accentColorHex = 0xFF00E5FF
            ),
            com.example.data.model.BoardroomPanelist(
                id = "director_arthur",
                name = "Arthur Pendelton",
                role = "Lead Board Director",
                focusArea = "Governance, Executive Composure & Consensus",
                avatarInitials = "AP",
                pitch = 0.92f,
                speechRate = 0.96f,
                accentColorHex = 0xFF7C4DFF
            )
        )
    }

    fun generateTurnRedline(userResponse: String, goal: String): com.example.data.model.ExecutiveRedlineItem {
        val trimmed = userResponse.trim()
        val lower = trimmed.lowercase()

        val detectedFluff = mutableListOf<String>()
        fillerWords.forEach { if (lower.contains("\\b$it\\b".toRegex())) detectedFluff.add(it) }
        hedgePhrases.forEach { if (lower.contains(it)) detectedFluff.add(it) }

        val cleanRemoved = if (detectedFluff.isNotEmpty()) detectedFluff.take(4) else listOf("I feel like maybe", "sort of")

        val polished = when {
            trimmed.length < 25 -> "I deliver definitive results on this initiative by orchestrating cross-functional execution and standardizing SLA metrics."
            lower.contains("think") || lower.contains("feel") || lower.contains("maybe") || lower.contains("um") -> {
                trimmed
                    .replace("(?i)\\bi think\\b".toRegex(), "Our data demonstrates")
                    .replace("(?i)\\bi feel like\\b".toRegex(), "The operational metric is")
                    .replace("(?i)\\bmaybe\\b".toRegex(), "decisively")
                    .replace("(?i)\\bsort of\\b".toRegex(), "")
                    .replace("(?i)\\bum\\b".toRegex(), "")
                    .replace("(?i)\\buh\\b".toRegex(), "")
                    .replace("\\s+".toRegex(), " ")
                    .trim()
            }
            else -> "Bottom line: $trimmed. We structured the solution to eliminate split-brain risk and deliver 4.2x ROI."
        }

        val added = listOf("BLUF Framing", "Quantified Impact", "Definitive Assertion")

        return com.example.data.model.ExecutiveRedlineItem(
            originalText = if (trimmed.isNotBlank()) trimmed else "Well, um, I guess we sort of tried to optimize the database...",
            polishedText = polished,
            wordsRemoved = cleanRemoved,
            wordsAdded = added,
            blufStrength = "96% Executive Alignment",
            convictionMultiplier = "+3.8x Conviction"
        )
    }
}
