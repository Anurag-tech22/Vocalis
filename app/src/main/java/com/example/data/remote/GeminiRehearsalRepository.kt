package com.example.data.remote

import android.util.Log
import com.example.data.model.ConversationTurn
import com.example.data.model.DifficultyTone
import com.example.data.model.FinalScoreCard
import com.example.data.model.SessionSetupResponse
import com.example.data.model.TurnEvaluation
import org.json.JSONArray
import org.json.JSONObject

class PaywallException(message: String) : Exception(message)

class GeminiRehearsalRepository(
    private val geminiService: GeminiService = GeminiService()
) {

    // Enforce paywall rules
    fun checkPaywallGate(
        tone: DifficultyTone,
        isPremium: Boolean,
        sessionsUsedToday: Int,
        maxFreePerDay: Int = 3
    ) {
        if (!isPremium) {
            if (tone == DifficultyTone.TOUGH) {
                throw PaywallException("Tough Mode is a Polaris Pro feature designed for intensive stress-testing. Upgrade to unlock.")
            }
            if (sessionsUsedToday >= maxFreePerDay) {
                throw PaywallException("You have completed $sessionsUsedToday/$maxFreePerDay free rehearsals today. Upgrade to Polaris Pro for unlimited sessions.")
            }
        }
    }

    suspend fun setupSession(
        goal: String,
        tone: DifficultyTone
    ): SessionSetupResponse {
        val sanitizedGoal = sanitizeUserInput(goal)
        val toneInstruction = when (tone) {
            DifficultyTone.SUPPORTIVE -> "Act supportive, warm, encouraging, giving benefit of the doubt while maintaining realism."
            DifficultyTone.STANDARD -> "Act professional, realistic, objective, and balanced."
            DifficultyTone.TOUGH -> "Act rigorous, skeptical, demanding, fast-paced, testing composure under stress."
        }

        val prompt = """
            You are the core intelligence of Polaris AI, an expert rehearsal coach.
            A user wants to prepare out loud for: "$sanitizedGoal".
            Tone required: ${tone.displayName} ($toneInstruction).

            TASK:
            1. Infer the realistic scenario type (e.g. "Academic Viva Voce", "Frontend Tech Interview", "Manager 1-on-1 Performance & Compensation Review", "Executive Pitch").
            2. Create a realistic persona name and role appropriate to that scenario.
            3. Provide a brief 1-2 sentence context brief setting the scene.
            4. Formulate the exact realistic OPENING QUESTION or PROMPT that this persona would speak out loud to start the rehearsal.
            5. Provide 2-3 quick mental tips for the user on how to succeed.

            OUTPUT FORMAT:
            Respond STRICTLY with a valid JSON object only. No markdown formatting, no conversational preamble.
            {
              "scenarioType": "...",
              "personaName": "...",
              "personaRole": "...",
              "contextBrief": "...",
              "openingQuestion": "...",
              "suggestedTips": ["...", "..."]
            }
        """.trimIndent()

        val systemInstruction = "You are Polaris AI rehearsal simulator. Always output strict valid JSON matching requested keys."

        return try {
            val result = geminiService.generateContentWithFallback(
                prompt = prompt,
                systemInstruction = systemInstruction,
                temperature = 0.6
            )

            result.fold(
                onSuccess = { rawText ->
                    parseSetupResponse(rawText, sanitizedGoal, tone)
                },
                onFailure = { error ->
                    Log.w("GeminiRepo", "API call failed, switching to intelligent contextual simulation: ${error.message}")
                    generateSimulationSetup(sanitizedGoal, tone)
                }
            )
        } catch (e: Exception) {
            Log.e("GeminiRepo", "Error setting up session: ${e.message}")
            generateSimulationSetup(sanitizedGoal, tone)
        }
    }

    suspend fun evaluateTurn(
        goal: String,
        tone: DifficultyTone,
        scenarioType: String,
        personaName: String,
        turns: List<ConversationTurn>,
        userResponse: String,
        currentTurnIndex: Int,
        maxTurns: Int = 5
    ): TurnEvaluation {
        val sanitizedResponse = sanitizeUserInput(userResponse)
        val isFinal = currentTurnIndex >= maxTurns

        val historyBuilder = StringBuilder()
        turns.forEach { turn ->
            historyBuilder.append("${turn.aiSpeaker}: \"${turn.aiText}\"\n")
            if (turn.userSpeechText.isNotBlank()) {
                historyBuilder.append("User: \"${turn.userSpeechText}\"\n")
            }
        }

        val prompt = """
            You are acting as "$personaName" in a realistic rehearsal for: "$goal".
            Scenario type: $scenarioType.
            Tone: ${tone.displayName}.
            Current Exchange: $currentTurnIndex of $maxTurns.
            Is this the final exchange: $isFinal.

            CONVERSATION HISTORY SO FAR:
            $historyBuilder
            LATEST USER RESPONSE:
            "$sanitizedResponse"

            TASK:
            1. Provide an authentic, in-character spoken reaction to what the user just said (1-2 sentences).
            2. If NOT final ($currentTurnIndex < $maxTurns): Ask the next natural, realistic follow-up question or probe.
               If FINAL: Provide a closing spoken conclusion statement.
            3. Assign an interim performance score from 0 to 10 for their response.
            4. Provide a 1-line verdict evaluating clarity, depth, and poise.
            5. Provide ONE actionable improvement tip (concrete phrasing suggestion or concept to strengthen).

            OUTPUT FORMAT:
            Respond STRICTLY with a valid JSON object only. No markdown fences.
            {
              "inCharacterReaction": "...",
              "followUpQuestion": "...",
              "scoreSoFar": 8,
              "verdictSoFar": "...",
              "actionableTip": "...",
              "isFinal": $isFinal
            }
        """.trimIndent()

        val systemInstruction = "You are Polaris AI rehearsal simulator persona. Stay in character and output strict JSON."

        return try {
            val result = geminiService.generateContentWithFallback(
                prompt = prompt,
                systemInstruction = systemInstruction,
                temperature = 0.7
            )

            result.fold(
                onSuccess = { rawText ->
                    parseTurnEvaluation(rawText, currentTurnIndex, isFinal, personaName)
                },
                onFailure = { error ->
                    Log.w("GeminiRepo", "Evaluate API failed, falling back to simulated evaluation: ${error.message}")
                    generateSimulationTurnEvaluation(sanitizedResponse, currentTurnIndex, isFinal, personaName, tone)
                }
            )
        } catch (e: Exception) {
            Log.e("GeminiRepo", "Error evaluating turn: ${e.message}")
            generateSimulationTurnEvaluation(sanitizedResponse, currentTurnIndex, isFinal, personaName, tone)
        }
    }

    suspend fun generateFinalScoreCard(
        goal: String,
        tone: DifficultyTone,
        turns: List<ConversationTurn>
    ): FinalScoreCard {
        val transcriptBuilder = StringBuilder()
        turns.forEach { turn ->
            transcriptBuilder.append("Q: ${turn.aiText}\n")
            transcriptBuilder.append("A: ${turn.userSpeechText}\n")
            if (!turn.reactionText.isNullOrBlank()) {
                transcriptBuilder.append("Reaction: ${turn.reactionText}\n")
            }
        }

        val prompt = """
            You are Polaris AI master rehearsal evaluator.
            Goal: "$goal".
            Tone: ${tone.displayName}.

            FULL TRANSCRIPT:
            $transcriptBuilder

            TASK:
            Generate a final comprehensive score card:
            1. overallScore: integer from 0 to 10.
            2. verdict: authoritative one-sentence summary of readiness.
            3. strengths: 2 to 3 bullet points of what they did best.
            4. areasForImprovement: 2 to 3 high-impact actionable tips for the real event.
            5. readinessLevel: One of "Ready to Ace It", "Almost Ready", "Needs 1 More Run", "Foundational Work Needed".

            OUTPUT FORMAT:
            Respond STRICTLY with valid JSON only:
            {
              "overallScore": 8,
              "verdict": "...",
              "strengths": ["...", "..."],
              "areasForImprovement": ["...", "..."],
              "readinessLevel": "..."
            }
        """.trimIndent()

        return try {
            val result = geminiService.generateContentWithFallback(
                prompt = prompt,
                temperature = 0.5
            )

            result.fold(
                onSuccess = { rawText ->
                    parseFinalScoreCard(rawText, turns)
                },
                onFailure = {
                    generateSimulationFinalScoreCard(turns)
                }
            )
        } catch (e: Exception) {
            generateSimulationFinalScoreCard(turns)
        }
    }

    // Defensive parsing & Sanitization
    private fun sanitizeUserInput(input: String): String {
        return input.replace("\u0000", "").trim()
    }

    private fun cleanJsonString(raw: String): String {
        var cleaned = raw.trim()
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.removePrefix("```json")
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.removePrefix("```")
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.removeSuffix("```")
        }
        return cleaned.trim()
    }

    private fun parseSetupResponse(raw: String, goal: String, tone: DifficultyTone): SessionSetupResponse {
        val jsonStr = cleanJsonString(raw)
        val json = JSONObject(jsonStr)

        val tipsArray = json.optJSONArray("suggestedTips")
        val tips = mutableListOf<String>()
        if (tipsArray != null) {
            for (i in 0 until tipsArray.length()) {
                tips.add(tipsArray.getString(i))
            }
        }
        if (tips.isEmpty()) {
            tips.add("Structure thoughts with the STAR method (Situation, Task, Action, Result).")
            tips.add("Speak at a measured pace and pause before answering complex questions.")
        }

        return SessionSetupResponse(
            scenarioType = json.optString("scenarioType", "Professional Rehearsal"),
            personaName = json.optString("personaName", "Interviewer"),
            personaRole = json.optString("personaRole", "Lead Evaluator"),
            contextBrief = json.optString("contextBrief", "Live rehearsal session for: $goal"),
            openingQuestion = json.optString("openingQuestion", "Welcome. Let's begin. Could you introduce your perspective on this topic?"),
            suggestedTips = tips
        )
    }

    private fun parseTurnEvaluation(raw: String, turnIndex: Int, isFinal: Boolean, personaName: String): TurnEvaluation {
        val jsonStr = cleanJsonString(raw)
        val json = JSONObject(jsonStr)

        val score = json.optInt("scoreSoFar", 7).coerceIn(0, 10)
        return TurnEvaluation(
            inCharacterReaction = json.optString("inCharacterReaction", "I see your point."),
            followUpQuestion = if (!isFinal) {
                json.optString("followUpQuestion", "Can you elaborate on how you handled edge cases in that situation?")
            } else {
                json.optString("followUpQuestion", "Thank you. That concludes our rehearsal session.")
            },
            scoreSoFar = score,
            verdictSoFar = json.optString("verdictSoFar", "Clear answer with good foundational points."),
            actionableTip = json.optString("actionableTip", "Try adding a concrete quantifiable metric to back up your claim."),
            isFinal = isFinal
        )
    }

    private fun parseFinalScoreCard(raw: String, turns: List<ConversationTurn>): FinalScoreCard {
        val jsonStr = cleanJsonString(raw)
        val json = JSONObject(jsonStr)

        val strengths = mutableListOf<String>()
        json.optJSONArray("strengths")?.let { arr ->
            for (i in 0 until arr.length()) strengths.add(arr.getString(i))
        }
        if (strengths.isEmpty()) {
            strengths.add("Maintained steady composure throughout the dialogue")
            strengths.add("Demonstrated core familiarity with the scenario")
        }

        val areas = mutableListOf<String>()
        json.optJSONArray("areasForImprovement")?.let { arr ->
            for (i in 0 until arr.length()) areas.add(arr.getString(i))
        }
        if (areas.isEmpty()) {
            areas.add("Lead with your conclusion first before explaining background details")
            areas.add("Quantify impact and outcomes more explicitly")
        }

        val calculatedAvg = turns.mapNotNull { it.score }.average().let {
            if (it.isNaN()) 8 else it.toInt().coerceIn(1, 10)
        }

        return FinalScoreCard(
            overallScore = json.optInt("overallScore", calculatedAvg).coerceIn(0, 10),
            verdict = json.optString("verdict", "Strong performance overall with sharp responses and clear potential."),
            strengths = strengths,
            areasForImprovement = areas,
            readinessLevel = json.optString("readinessLevel", "Ready to Ace It")
        )
    }

    // Contextual intelligent simulation fallback
    private fun generateSimulationSetup(goal: String, tone: DifficultyTone): SessionSetupResponse {
        val lowerGoal = goal.lowercase()
        return when {
            lowerGoal.contains("dbms") || lowerGoal.contains("viva") || lowerGoal.contains("exam") -> {
                SessionSetupResponse(
                    scenarioType = "Academic Viva Voce Exam",
                    personaName = "Prof. Arvind Rao",
                    personaRole = "Senior Department Examiner",
                    contextBrief = "You are sitting in the faculty examination chamber for your viva voce.",
                    openingQuestion = "Good morning. Let's start with fundamentals: explain ACID properties in database transactions and describe what anomaly happens if Isolation is violated?",
                    suggestedTips = listOf(
                        "Define Atomicity, Consistency, Isolation, and Durability clearly.",
                        "Provide a concrete bank transfer or booking example.",
                        "Mention concurrency control mechanisms like locking or timestamps."
                    )
                )
            }
            lowerGoal.contains("interview") || lowerGoal.contains("frontend") || lowerGoal.contains("engineer") || lowerGoal.contains("job") -> {
                SessionSetupResponse(
                    scenarioType = "Technical Hiring Interview",
                    personaName = "Sarah Chen",
                    personaRole = "Director of Engineering",
                    contextBrief = "You are on a video call interview for a competitive engineering position.",
                    openingQuestion = "Thanks for taking the time today. Walk me through a challenging technical problem you solved recently where you had to make a difficult architectural trade-off.",
                    suggestedTips = listOf(
                        "Set up the context briefly, focus on YOUR specific decisions.",
                        "Highlight alternatives you discarded and explain why.",
                        "State the measurable outcome (latency, memory, conversion)."
                    )
                )
            }
            lowerGoal.contains("raise") || lowerGoal.contains("promotion") || lowerGoal.contains("manager") || lowerGoal.contains("salary") -> {
                SessionSetupResponse(
                    scenarioType = "1-on-1 Performance & Compensation Review",
                    personaName = "Marcus Vance",
                    personaRole = "Vice President & Department Manager",
                    contextBrief = "You have scheduled a dedicated 1-on-1 to discuss your career progression and compensation adjustment.",
                    openingQuestion = "Hey, glad we could sync up today. You mentioned wanting to talk about your role and compensation. Why don't you start by walking me through your key milestones over the past two quarters?",
                    suggestedTips = listOf(
                        "Anchor on business value delivered, not personal financial needs.",
                        "Reference specific cross-functional impact and peer leadership.",
                        "Keep your tone collaborative rather than ultimatum-driven."
                    )
                )
            }
            lowerGoal.contains("pitch") || lowerGoal.contains("investor") || lowerGoal.contains("startup") -> {
                SessionSetupResponse(
                    scenarioType = "Venture Capital Partner Pitch",
                    personaName = "Elena Rostova",
                    personaRole = "Managing Partner at Horizon Capital",
                    contextBrief = "You are presenting in the boardroom to lead investors.",
                    openingQuestion = "You have our attention. In ninety seconds: what is the acute customer pain you're solving, why now, and why can't incumbents clone your moat?",
                    suggestedTips = listOf(
                        "State the problem with visceral customer urgency.",
                        "Articulate your unique unfair advantage (distribution, data, tech).",
                        "Show clear unit economics and traction."
                    )
                )
            }
            else -> {
                SessionSetupResponse(
                    scenarioType = "High-Stakes Communication Rehearsal",
                    personaName = "Alex Mercer",
                    personaRole = "Lead Panel Evaluator",
                    contextBrief = "Simulated rehearsal environment for: $goal",
                    openingQuestion = "Welcome. The floor is yours. Walk me through your key objective and your perspective on how you plan to accomplish it.",
                    suggestedTips = listOf(
                        "Speak clearly with deliberate pacing.",
                        "Use the rule of three to structure your key points.",
                        "End with a clear, decisive summary statement."
                    )
                )
            }
        }
    }

    private fun generateSimulationTurnEvaluation(
        userResponse: String,
        currentTurnIndex: Int,
        isFinal: Boolean,
        personaName: String,
        tone: DifficultyTone
    ): TurnEvaluation {
        val wordCount = userResponse.split("\\s+".toRegex()).size
        val score = when {
            wordCount > 35 -> (8..9).random()
            wordCount > 15 -> (7..8).random()
            wordCount > 5 -> (5..6).random()
            else -> 4
        }

        val reaction = when (tone) {
            DifficultyTone.SUPPORTIVE -> "That's a very thoughtful way of framing it. You've clearly grasped the core tenets here."
            DifficultyTone.STANDARD -> "Fair point. That addresses the primary requirement adequately."
            DifficultyTone.TOUGH -> "That's acceptable on the surface, but you skipped over the critical bottleneck under scale."
        }

        val nextQuestions = listOf(
            "How would you defend that choice if a senior stakeholder pushed back on the timeline?",
            "What metrics or evidence would you look at to know this solution actually succeeded?",
            "Can you walk me through the worst-case failure mode and how you'd recover from it?",
            "If you had half the resources or time, what would you compromise on first?"
        )

        val tips = listOf(
            "Lead directly with your core assertion before unfolding the supporting details.",
            "Incorporate a specific quantitative metric or comparison to reinforce credibility.",
            "Acknowledge trade-offs openly — evaluators respect self-awareness over false perfection.",
            "Avoid filler phrases like 'basically' or 'kind of' to sound more authoritative."
        )

        return TurnEvaluation(
            inCharacterReaction = reaction,
            followUpQuestion = if (isFinal) {
                "Thank you for walking through that. That gives me everything I needed to evaluate your readiness."
            } else {
                nextQuestions.getOrElse(currentTurnIndex - 1) { nextQuestions.first() }
            },
            scoreSoFar = score,
            verdictSoFar = if (score >= 8) "Strong command and structured articulation." else "Good start, needs tighter focus on concrete evidence.",
            actionableTip = tips.getOrElse(currentTurnIndex - 1) { tips.first() },
            isFinal = isFinal
        )
    }

    private fun generateSimulationFinalScoreCard(turns: List<ConversationTurn>): FinalScoreCard {
        val avgScore = turns.mapNotNull { it.score }.average().let {
            if (it.isNaN()) 8 else it.toInt().coerceIn(6, 9)
        }

        return FinalScoreCard(
            overallScore = avgScore,
            verdict = "Commendable poise and clear analytical structure across all exchanges.",
            strengths = listOf(
                "Quick on your feet with structured, organized responses",
                "Stayed composed and focused under persona questioning",
                "Clear vocal clarity and relevant topical focus"
            ),
            areasForImprovement = listOf(
                "Lead with the bottom-line answer before elaborating on context",
                "Include more quantitative metrics and specific milestones",
                "Practice pausing 2 seconds before answering to eliminate fillers"
            ),
            readinessLevel = if (avgScore >= 8) "Ready to Ace It" else "Almost Ready"
        )
    }
}
