package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.billing.RevenueCatCustomerInfo
import com.example.billing.RevenueCatManager
import com.example.billing.SubscriptionPackage
import com.example.data.local.AppDatabase
import com.example.data.local.SessionEntity
import com.example.data.local.SessionRepository
import com.example.data.model.ConversationTurn
import com.example.data.model.DifficultyTone
import com.example.data.model.FinalScoreCard
import com.example.data.model.GameChallenge
import com.example.data.model.GameResult
import com.example.data.model.LeadershipScenario
import com.example.data.model.PlayerGameProfile
import com.example.data.model.Screen
import android.content.Intent
import com.example.data.model.AstraDeepReport
import com.example.data.model.SessionSetupResponse
import com.example.data.model.StressCurveball
import com.example.data.model.TurnEvaluation
import com.example.data.remote.GeminiRehearsalRepository
import com.example.data.remote.PaywallException
import com.example.game.CharismaGameManager
import com.example.util.AudioSoundEngine
import com.example.util.CognitiveLinguisticEngine
import com.example.util.SpeechManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class RehearsalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SessionRepository(AppDatabase.getDatabase(application).sessionDao())
    private val geminiRepo = GeminiRehearsalRepository()
    val speechManager = SpeechManager(application)
    val revenueCat = RevenueCatManager.getInstance(application)
    val gameManager = CharismaGameManager.getInstance(application)
    val playerProfile: StateFlow<PlayerGameProfile> = gameManager.playerProfile

    // Active Game Arena State
    private val _activeChallenge = MutableStateFlow<GameChallenge?>(null)
    val activeChallenge: StateFlow<GameChallenge?> = _activeChallenge.asStateFlow()

    private val _gameSecondsRemaining = MutableStateFlow(30)
    val gameSecondsRemaining: StateFlow<Int> = _gameSecondsRemaining.asStateFlow()

    private val _isGameRunning = MutableStateFlow(false)
    val isGameRunning: StateFlow<Boolean> = _isGameRunning.asStateFlow()

    val gameLiveTranscript = MutableStateFlow("")

    private val _gameFillersCount = MutableStateFlow(0)
    val gameFillersCount: StateFlow<Int> = _gameFillersCount.asStateFlow()

    private val _gameComposure = MutableStateFlow(100)
    val gameComposure: StateFlow<Int> = _gameComposure.asStateFlow()

    private val _gameResult = MutableStateFlow<GameResult?>(null)
    val gameResult: StateFlow<GameResult?> = _gameResult.asStateFlow()

    private val _showGameResult = MutableStateFlow(false)
    val showGameResult: StateFlow<Boolean> = _showGameResult.asStateFlow()

    private var gameTimerJob: kotlinx.coroutines.Job? = null
    private var gameElapsedSeconds = 0

    // Navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen stack for BackHandler
    private val screenStack = ArrayDeque<Screen>()

    // Paywall & RevenueCat Customer State
    val customerInfo: StateFlow<RevenueCatCustomerInfo> = revenueCat.customerInfo

    private val _isPremium = MutableStateFlow(revenueCat.customerInfo.value.isProActive)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    val offerings: List<SubscriptionPackage> = revenueCat.offerings

    private val _isPurchasing = MutableStateFlow(false)
    val isPurchasing: StateFlow<Boolean> = _isPurchasing.asStateFlow()

    private val _promoMessage = MutableStateFlow<String?>(null)
    val promoMessage: StateFlow<String?> = _promoMessage.asStateFlow()

    private val _promoError = MutableStateFlow<String?>(null)
    val promoError: StateFlow<String?> = _promoError.asStateFlow()

    val todaySessionCount: StateFlow<Int> = repository.getTodaySessionsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _paywallReason = MutableStateFlow<String?>(null)
    val paywallReason: StateFlow<String?> = _paywallReason.asStateFlow()

    // Home Input State
    val goalInput = MutableStateFlow("")
    val selectedTone = MutableStateFlow(DifficultyTone.STANDARD)

    fun setTone(tone: DifficultyTone) {
        selectedTone.value = tone
    }

    // Active Rehearsal State
    private val _isSetupLoading = MutableStateFlow(false)
    val isSetupLoading: StateFlow<Boolean> = _isSetupLoading.asStateFlow()

    private val _setupError = MutableStateFlow<String?>(null)
    val setupError: StateFlow<String?> = _setupError.asStateFlow()

    private val _currentSetup = MutableStateFlow<SessionSetupResponse?>(null)
    val currentSetup: StateFlow<SessionSetupResponse?> = _currentSetup.asStateFlow()

    private val _turns = MutableStateFlow<List<ConversationTurn>>(emptyList())
    val turns: StateFlow<List<ConversationTurn>> = _turns.asStateFlow()

    private val _currentTurnIndex = MutableStateFlow(1)
    val currentTurnIndex: StateFlow<Int> = _currentTurnIndex.asStateFlow()

    val maxTurns = 5

    private val _currentAiPrompt = MutableStateFlow("")
    val currentAiPrompt: StateFlow<String> = _currentAiPrompt.asStateFlow()

    val userInputText = MutableStateFlow("")

    private val _isEvaluating = MutableStateFlow(false)
    val isEvaluating: StateFlow<Boolean> = _isEvaluating.asStateFlow()

    private val _latestEvaluation = MutableStateFlow<TurnEvaluation?>(null)
    val latestEvaluation: StateFlow<TurnEvaluation?> = _latestEvaluation.asStateFlow()

    private val _finalScoreCard = MutableStateFlow<FinalScoreCard?>(null)
    val finalScoreCard: StateFlow<FinalScoreCard?> = _finalScoreCard.asStateFlow()

    private val _showScoreCard = MutableStateFlow(false)
    val showScoreCard: StateFlow<Boolean> = _showScoreCard.asStateFlow()

    // Multi-Speaker Boardroom Panel Mode
    private val _isBoardroomModeActive = MutableStateFlow(false)
    val isBoardroomModeActive: StateFlow<Boolean> = _isBoardroomModeActive.asStateFlow()

    private val _boardroomPanel = MutableStateFlow(CognitiveLinguisticEngine.getBoardroomPanel())
    val boardroomPanel: StateFlow<List<com.example.data.model.BoardroomPanelist>> = _boardroomPanel.asStateFlow()

    private val _activePanelistIndex = MutableStateFlow(0)
    val activePanelistIndex: StateFlow<Int> = _activePanelistIndex.asStateFlow()

    // Executive Camera Mirror
    private val _isCameraMirrorExpanded = MutableStateFlow(false)
    val isCameraMirrorExpanded: StateFlow<Boolean> = _isCameraMirrorExpanded.asStateFlow()

    // Turn Redline
    private val _latestTurnRedline = MutableStateFlow<com.example.data.model.ExecutiveRedlineItem?>(null)
    val latestTurnRedline: StateFlow<com.example.data.model.ExecutiveRedlineItem?> = _latestTurnRedline.asStateFlow()

    fun toggleBoardroomMode() {
        _isBoardroomModeActive.value = !_isBoardroomModeActive.value
    }

    fun toggleCameraMirror() {
        _isCameraMirrorExpanded.value = !_isCameraMirrorExpanded.value
    }

    fun speakExecutiveRewrite(text: String) {
        speechManager.speak(text, pitch = 0.95f, speechRate = 0.96f)
    }

    // Astra 6 Cognitive Deep Report & Socratic Stress Mode
    private val _currentAstraReport = MutableStateFlow<AstraDeepReport?>(null)
    val currentAstraReport: StateFlow<AstraDeepReport?> = _currentAstraReport.asStateFlow()

    private val _isStressModeActive = MutableStateFlow(false)
    val isStressModeActive: StateFlow<Boolean> = _isStressModeActive.asStateFlow()

    private val _activeCurveball = MutableStateFlow<StressCurveball?>(null)
    val activeCurveball: StateFlow<StressCurveball?> = _activeCurveball.asStateFlow()

    private val _curveballSecondsLeft = MutableStateFlow(15)
    val curveballSecondsLeft: StateFlow<Int> = _curveballSecondsLeft.asStateFlow()

    private var curveballJob: Job? = null

    private val _sessionSpeechDurationSeconds = MutableStateFlow(0)
    val sessionSpeechDurationSeconds: StateFlow<Int> = _sessionSpeechDurationSeconds.asStateFlow()
    private var durationTimerJob: Job? = null

    private val _liveFillerCount = MutableStateFlow(0)
    val liveFillerCount: StateFlow<Int> = _liveFillerCount.asStateFlow()

    // History state
    val pastSessions: StateFlow<List<SessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSessionDetail = MutableStateFlow<SessionEntity?>(null)
    val selectedSessionDetail: StateFlow<SessionEntity?> = _selectedSessionDetail.asStateFlow()

    // Vocal Studio & Acoustic Telemetry Dashboard States
    private val _isStudioRecording = MutableStateFlow(false)
    val isStudioRecording: StateFlow<Boolean> = _isStudioRecording.asStateFlow()

    private val _isSimulatedStreamActive = MutableStateFlow(false)
    val isSimulatedStreamActive: StateFlow<Boolean> = _isSimulatedStreamActive.asStateFlow()

    private val _studioAudioRms = MutableStateFlow(0.06f)
    val studioAudioRms: StateFlow<Float> = _studioAudioRms.asStateFlow()

    private val _studioDecibels = MutableStateFlow(-42f)
    val studioDecibels: StateFlow<Float> = _studioDecibels.asStateFlow()

    private val _studioWpm = MutableStateFlow(136)
    val studioWpm: StateFlow<Int> = _studioWpm.asStateFlow()

    private val _studioPitchHz = MutableStateFlow(142)
    val studioPitchHz: StateFlow<Int> = _studioPitchHz.asStateFlow()

    private val _studioJitterPercent = MutableStateFlow(0.42f)
    val studioJitterPercent: StateFlow<Float> = _studioJitterPercent.asStateFlow()

    private val _vocalResonanceScore = MutableStateFlow(86)
    val vocalResonanceScore: StateFlow<Int> = _vocalResonanceScore.asStateFlow()

    private val _diaphragmaticStability = MutableStateFlow(94)
    val diaphragmaticStability: StateFlow<Int> = _diaphragmaticStability.asStateFlow()

    private val _vocalClarityScore = MutableStateFlow(92)
    val vocalClarityScore: StateFlow<Int> = _vocalClarityScore.asStateFlow()

    private val _studioSelectedPhraseIndex = MutableStateFlow(0)
    val studioSelectedPhraseIndex: StateFlow<Int> = _studioSelectedPhraseIndex.asStateFlow()

    private val _studioTranscript = MutableStateFlow("")
    val studioTranscript: StateFlow<String> = _studioTranscript.asStateFlow()

    private var studioSimulationJob: Job? = null

    init {
        // Sync RevenueCat subscription status
        viewModelScope.launch {
            revenueCat.customerInfo.collect { info ->
                _isPremium.value = info.isProActive
            }
        }

        // Live Audio RMS Sync for Vocal Studio
        viewModelScope.launch {
            speechManager.audioRms.collect { rms ->
                if (_isStudioRecording.value) {
                    val clamped = rms.coerceIn(0f, 1f)
                    _studioAudioRms.value = clamped
                    _studioDecibels.value = -48f + (clamped * 42f)
                    val resonance = (72 + (clamped * 26).toInt()).coerceIn(65, 99)
                    _vocalResonanceScore.value = resonance
                    _vocalClarityScore.value = (85 + (clamped * 13).toInt()).coerceIn(75, 98)
                    _studioPitchHz.value = (115 + (clamped * 65).toInt()).coerceIn(90, 230)
                    _studioJitterPercent.value = (0.25f + ((1f - clamped) * 0.45f)).coerceIn(0.2f, 1.8f)
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.addLast(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_showGameResult.value) {
            _showGameResult.value = false
            return true
        }
        if (_isGameRunning.value) {
            stopGameRound()
            return true
        }
        if (_showScoreCard.value) {
            _showScoreCard.value = false
            return true
        }
        if (_selectedSessionDetail.value != null) {
            _selectedSessionDetail.value = null
            return true
        }
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeLast()
            return true
        }
        if (_currentScreen.value != Screen.Home) {
            _currentScreen.value = Screen.Home
            return true
        }
        return false
    }

    fun triggerPaywall(reason: String) {
        _paywallReason.value = reason
    }

    fun dismissPaywall() {
        _paywallReason.value = null
    }

    fun purchasePackage(packageId: String) {
        _isPurchasing.value = true
        _promoError.value = null
        viewModelScope.launch {
            val result = revenueCat.purchasePackage(packageId)
            _isPurchasing.value = false
            result.onSuccess { info ->
                _isPremium.value = info.isProActive
                _paywallReason.value = null
                _promoMessage.value = "Purchased ${info.activePackageTitle ?: "Vocalis Pro"} successfully!"
            }.onFailure { err ->
                _promoError.value = err.message ?: "Purchase could not be completed."
            }
        }
    }

    fun startFreeTrial() {
        _isPurchasing.value = true
        _promoError.value = null
        viewModelScope.launch {
            val result = revenueCat.startFreeTrial()
            _isPurchasing.value = false
            result.onSuccess { info ->
                _isPremium.value = info.isProActive
                _paywallReason.value = null
                _promoMessage.value = "7-Day Vocalis Pro Trial activated! Enjoy full access."
            }.onFailure { err ->
                _promoError.value = err.message ?: "Could not start trial."
            }
        }
    }

    fun redeemPromoCode(code: String) {
        _promoError.value = null
        _promoMessage.value = null
        if (code.isBlank()) {
            _promoError.value = "Please enter a valid promo code."
            return
        }

        viewModelScope.launch {
            val result = revenueCat.redeemPromoCode(code)
            result.onSuccess { info ->
                _isPremium.value = info.isProActive
                _paywallReason.value = null
                _promoMessage.value = "Code accepted! Unlocked: ${info.activePackageTitle}."
            }.onFailure { err ->
                _promoError.value = err.message
            }
        }
    }

    fun restorePurchases() {
        _isPurchasing.value = true
        _promoError.value = null
        _promoMessage.value = null
        viewModelScope.launch {
            val result = revenueCat.restorePurchases()
            _isPurchasing.value = false
            result.onSuccess { info ->
                _isPremium.value = info.isProActive
                if (info.isProActive) {
                    _promoMessage.value = "Purchases restored! ${info.activePackageTitle} is active."
                } else {
                    _promoMessage.value = "No previous purchases found for this account."
                }
            }
        }
    }

    fun resetToFreeTierForTesting() {
        viewModelScope.launch {
            revenueCat.resetToFreeTierForTesting()
            _isPremium.value = false
            _promoMessage.value = "Account reset to Free Tier for testing."
        }
    }

    fun launchLeadershipScenario(scenario: LeadershipScenario) {
        goalInput.value = scenario.promptGoal
        selectedTone.value = scenario.difficulty
        startSession(scenario.promptGoal, scenario.difficulty)
    }

    fun startSession(goal: String, tone: DifficultyTone) {
        val trimmedGoal = goal.trim()
        if (trimmedGoal.isBlank()) {
            _setupError.value = "Please enter what you are preparing for."
            return
        }

        try {
            geminiRepo.checkPaywallGate(
                tone = tone,
                isPremium = _isPremium.value,
                sessionsUsedToday = todaySessionCount.value
            )
        } catch (e: PaywallException) {
            triggerPaywall(e.message ?: "Vocalis Pro Upgrade Required")
            return
        }

        _setupError.value = null
        _isSetupLoading.value = true
        _turns.value = emptyList()
        _currentTurnIndex.value = 1
        _finalScoreCard.value = null
        _showScoreCard.value = false
        _latestEvaluation.value = null
        userInputText.value = ""

        navigateTo(Screen.Practice)

        viewModelScope.launch {
            try {
                val setup = geminiRepo.setupSession(trimmedGoal, tone)
                _currentSetup.value = setup
                _currentAiPrompt.value = setup.openingQuestion

                val isBoardroom = _isBoardroomModeActive.value
                val panelist = if (isBoardroom) _boardroomPanel.value[0] else null
                _activePanelistIndex.value = 0

                val firstTurn = ConversationTurn(
                    turnIndex = 1,
                    aiSpeaker = panelist?.name ?: setup.personaName,
                    aiText = setup.openingQuestion,
                    speakerRole = panelist?.role ?: setup.personaRole,
                    panelistId = panelist?.id
                )
                _turns.value = listOf(firstTurn)

                // Read opening question aloud with realistic persona vocal modulation
                val (pitch, rate) = if (isBoardroom && panelist != null) {
                    panelist.pitch to panelist.speechRate
                } else {
                    when (tone) {
                        DifficultyTone.TOUGH -> 0.88f to 1.04f
                        DifficultyTone.SUPPORTIVE -> 1.05f to 0.92f
                        DifficultyTone.STANDARD -> 0.98f to 0.98f
                    }
                }
                speechManager.speak(setup.openingQuestion, pitch = pitch, speechRate = rate)
            } catch (e: Exception) {
                _setupError.value = "Failed to launch rehearsal: ${e.message}"
            } finally {
                _isSetupLoading.value = false
            }
        }
    }

    fun speakCurrentAiPrompt() {
        if (_currentAiPrompt.value.isNotBlank()) {
            val isBoardroom = _isBoardroomModeActive.value
            val panelist = if (isBoardroom) {
                _boardroomPanel.value.getOrNull(_activePanelistIndex.value)
            } else null

            val (pitch, rate) = if (isBoardroom && panelist != null) {
                panelist.pitch to panelist.speechRate
            } else {
                when (selectedTone.value) {
                    DifficultyTone.TOUGH -> 0.88f to 1.04f
                    DifficultyTone.SUPPORTIVE -> 1.05f to 0.92f
                    DifficultyTone.STANDARD -> 0.98f to 0.98f
                }
            }
            speechManager.speak(_currentAiPrompt.value, pitch = pitch, speechRate = rate)
        }
    }

    fun stopSpeaking() {
        speechManager.stopSpeaking()
    }

    fun startListening() {
        if (durationTimerJob == null || durationTimerJob?.isActive == false) {
            durationTimerJob = viewModelScope.launch {
                while (true) {
                    delay(1000)
                    _sessionSpeechDurationSeconds.value += 1
                }
            }
        }
        speechManager.startListening(
            onPartialResult = { partial ->
                userInputText.value = partial
                updateLiveFillerCount(partial)
            },
            onFinalResult = { result ->
                userInputText.value = result
                updateLiveFillerCount(result)
            }
        )
    }

    private fun updateLiveFillerCount(text: String) {
        val fillerSet = setOf("um", "uh", "like", "basically", "actually", "literally", "sort of", "kinda", "you know")
        val words = text.lowercase().split("\\s+".toRegex()).map { it.replace("[^a-z]".toRegex(), "") }
        _liveFillerCount.value = words.count { it in fillerSet }
    }

    fun stopListening() {
        speechManager.stopListening()
    }

    fun submitTurnResponse(manualResponse: String? = null) {
        val responseText = (manualResponse ?: userInputText.value).trim()
        if (responseText.isBlank()) return

        stopListening()
        stopSpeaking()

        val setup = _currentSetup.value ?: return
        val currentTurns = _turns.value.toMutableList()
        val currentIndex = _currentTurnIndex.value

        // Record user's response in current turn
        val activeTurn = currentTurns.lastOrNull() ?: ConversationTurn(
            turnIndex = currentIndex,
            aiSpeaker = setup.personaName,
            aiText = _currentAiPrompt.value
        )
        val updatedTurn = activeTurn.copy(userSpeechText = responseText)
        currentTurns[currentTurns.lastIndex] = updatedTurn
        _turns.value = currentTurns

        userInputText.value = ""
        _isEvaluating.value = true

        viewModelScope.launch {
            try {
                val eval = geminiRepo.evaluateTurn(
                    goal = goalInput.value,
                    tone = selectedTone.value,
                    scenarioType = setup.scenarioType,
                    personaName = setup.personaName,
                    turns = _turns.value,
                    userResponse = responseText,
                    currentTurnIndex = currentIndex,
                    maxTurns = maxTurns
                )
                _latestEvaluation.value = eval

                // Generate Executive Redline & Gold Standard Rewrite
                val redline = CognitiveLinguisticEngine.generateTurnRedline(responseText, goalInput.value)
                _latestTurnRedline.value = redline

                // Play acoustic evaluation complete tone
                AudioSoundEngine.playEvaluationCompleteTone()

                // Update turn with score, tip, & redline
                val scoredTurn = updatedTurn.copy(
                    reactionText = eval.inCharacterReaction,
                    score = eval.scoreSoFar,
                    tip = eval.actionableTip,
                    redlineOriginal = redline.originalText,
                    redlinePolished = redline.polishedText
                )
                val refreshedTurns = _turns.value.toMutableList()
                refreshedTurns[refreshedTurns.lastIndex] = scoredTurn
                _turns.value = refreshedTurns

                if (currentIndex >= maxTurns || eval.isFinal) {
                    // Finalize session
                    completeSession()
                } else {
                    // Advance to next turn
                    val nextIndex = currentIndex + 1
                    _currentTurnIndex.value = nextIndex
                    val combinedPrompt = "${eval.inCharacterReaction} ${eval.followUpQuestion}"
                    _currentAiPrompt.value = combinedPrompt

                    val isBoardroom = _isBoardroomModeActive.value
                    val nextPanelist = if (isBoardroom) {
                        val pIdx = (nextIndex - 1) % _boardroomPanel.value.size
                        _activePanelistIndex.value = pIdx
                        _boardroomPanel.value[pIdx]
                    } else null

                    val nextTurn = ConversationTurn(
                        turnIndex = nextIndex,
                        aiSpeaker = nextPanelist?.name ?: setup.personaName,
                        aiText = eval.followUpQuestion,
                        speakerRole = nextPanelist?.role ?: setup.personaRole,
                        panelistId = nextPanelist?.id
                    )
                    _turns.value = _turns.value + nextTurn

                    // Speak follow-up question with realistic panelist or persona pitch & speed
                    val (pitch, rate) = if (isBoardroom && nextPanelist != null) {
                        nextPanelist.pitch to nextPanelist.speechRate
                    } else {
                        when (selectedTone.value) {
                            DifficultyTone.TOUGH -> 0.88f to 1.04f
                            DifficultyTone.SUPPORTIVE -> 1.05f to 0.92f
                            DifficultyTone.STANDARD -> 0.98f to 0.98f
                        }
                    }
                    speechManager.speak(combinedPrompt, pitch = pitch, speechRate = rate)
                }
            } catch (e: Exception) {
                _setupError.value = "Evaluation notice: ${e.message}"
            } finally {
                _isEvaluating.value = false
            }
        }
    }

    fun finishSessionEarly() {
        if (_turns.value.isEmpty()) {
            navigateBack()
            return
        }
        stopListening()
        stopSpeaking()
        completeSession()
    }

    private fun completeSession() {
        durationTimerJob?.cancel()
        _isEvaluating.value = true
        viewModelScope.launch {
            try {
                val scoreCard = geminiRepo.generateFinalScoreCard(
                    goal = goalInput.value,
                    tone = selectedTone.value,
                    turns = _turns.value
                )
                _finalScoreCard.value = scoreCard
                _showScoreCard.value = true

                // Compute Astra 6 Deep Report immediately
                val astraReport = CognitiveLinguisticEngine.analyzeSession(
                    goal = goalInput.value.ifBlank { "Rehearsal Session" },
                    tone = selectedTone.value,
                    turns = _turns.value,
                    totalDurationSeconds = _sessionSpeechDurationSeconds.value.coerceAtLeast(30)
                )
                _currentAstraReport.value = astraReport

                // Save to Room DB
                saveSessionToDatabase(scoreCard)

                // Speak verdict
                speechManager.speak("Rehearsal complete. Your score is ${scoreCard.overallScore} out of 10. ${scoreCard.verdict}")
            } catch (e: Exception) {
                val fallbackCard = FinalScoreCard(
                    overallScore = 8,
                    verdict = "Rehearsal concluded with solid conversational foundation.",
                    strengths = listOf("Communicated with clear vocal engagement"),
                    areasForImprovement = listOf("Refine concise bottom-line articulation"),
                    readinessLevel = "Ready to Ace It"
                )
                _finalScoreCard.value = fallbackCard
                _showScoreCard.value = true

                val astraReport = CognitiveLinguisticEngine.analyzeSession(
                    goal = goalInput.value.ifBlank { "Rehearsal Session" },
                    tone = selectedTone.value,
                    turns = _turns.value,
                    totalDurationSeconds = _sessionSpeechDurationSeconds.value.coerceAtLeast(30)
                )
                _currentAstraReport.value = astraReport

                saveSessionToDatabase(fallbackCard)
            } finally {
                _isEvaluating.value = false
            }
        }
    }

    private suspend fun saveSessionToDatabase(scoreCard: FinalScoreCard) {
        val setup = _currentSetup.value ?: return

        val jsonArray = JSONArray()
        _turns.value.forEach { turn ->
            val obj = JSONObject().apply {
                put("turnIndex", turn.turnIndex)
                put("aiSpeaker", turn.aiSpeaker)
                put("aiText", turn.aiText)
                put("userSpeechText", turn.userSpeechText)
                put("reactionText", turn.reactionText ?: "")
                put("score", turn.score ?: 0)
                put("tip", turn.tip ?: "")
            }
            jsonArray.put(obj)
        }

        val sessionEntity = SessionEntity(
            goal = goalInput.value.ifBlank { "Rehearsal Session" },
            tone = selectedTone.value.displayName,
            scenarioType = setup.scenarioType,
            personaName = setup.personaName,
            personaRole = setup.personaRole,
            finalScore = scoreCard.overallScore,
            verdict = scoreCard.verdict,
            timestamp = System.currentTimeMillis(),
            turnCount = _turns.value.size,
            transcriptJson = jsonArray.toString(),
            readinessLevel = scoreCard.readinessLevel
        )

        repository.insertSession(sessionEntity)
    }

    fun dismissScoreCardAndGoHome() {
        _showScoreCard.value = false
        stopSpeaking()
        stopListening()
        navigateTo(Screen.Home)
    }

    fun openAstraAnalysis() {
        if (_currentAstraReport.value == null) {
            _currentAstraReport.value = CognitiveLinguisticEngine.analyzeSession(
                goal = goalInput.value.ifBlank { "Executive Rehearsal" },
                tone = selectedTone.value,
                turns = _turns.value,
                totalDurationSeconds = _sessionSpeechDurationSeconds.value.coerceAtLeast(45)
            )
        }
        _showScoreCard.value = false
        navigateTo(Screen.AstraAnalysis())
    }

    fun analyzeAndOpenAstraReport(session: SessionEntity) {
        val parsedTurns = mutableListOf<ConversationTurn>()
        try {
            val array = JSONArray(session.transcriptJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                parsedTurns.add(
                    ConversationTurn(
                        turnIndex = obj.optInt("turnIndex", i + 1),
                        aiSpeaker = obj.optString("aiSpeaker", session.personaName),
                        aiText = obj.optString("aiText", ""),
                        userSpeechText = obj.optString("userSpeechText", ""),
                        reactionText = obj.optString("reactionText", ""),
                        score = obj.optInt("score", session.finalScore),
                        tip = obj.optString("tip", "")
                    )
                )
            }
        } catch (_: Exception) {}

        val toneEnum = DifficultyTone.values().find { it.displayName.equals(session.tone, ignoreCase = true) }
            ?: DifficultyTone.STANDARD

        _currentAstraReport.value = CognitiveLinguisticEngine.analyzeSession(
            goal = session.goal,
            tone = toneEnum,
            turns = parsedTurns,
            totalDurationSeconds = (session.turnCount * 30).coerceAtLeast(45)
        )
        _selectedSessionDetail.value = null
        navigateTo(Screen.AstraAnalysis(session.id))
    }

    fun toggleStressMode() {
        _isStressModeActive.value = !_isStressModeActive.value
        if (_isStressModeActive.value) {
            triggerCurveball()
        } else {
            dismissCurveball()
        }
    }

    fun triggerCurveball() {
        val goal = goalInput.value.ifBlank { "General Rehearsal" }
        val curveball = CognitiveLinguisticEngine.generateCurveball(goal, _currentTurnIndex.value)
        _activeCurveball.value = curveball
        _curveballSecondsLeft.value = curveball.timeLimitSeconds

        AudioSoundEngine.playSocraticInterruptTone()
        speechManager.speak("Challenge alert! ${curveball.triggerPrompt}", pitch = 0.92f, speechRate = 1.04f)

        curveballJob?.cancel()
        curveballJob = viewModelScope.launch {
            while (_curveballSecondsLeft.value > 0) {
                delay(1000)
                _curveballSecondsLeft.value -= 1
            }
            _activeCurveball.value = null
        }
    }

    fun dismissCurveball() {
        curveballJob?.cancel()
        _activeCurveball.value = null
    }

    fun startNewSessionWithSameGoal() {
        startSession(goalInput.value, selectedTone.value)
    }

    fun shareAstraReport() {
        val report = _currentAstraReport.value ?: return
        val text = """
            🚀 Vocalis - Astra 6 Neural Diagnostic Report
            Archetype: ${report.readinessArchetype}
            Overall Cognitive Score: ${report.overallIndex} / 100
            Clarity: ${report.radar.clarity}% | Gravitas: ${report.radar.gravitas}% | Brevity: ${report.radar.executiveBrevity}%
            Pacing: ${report.biometrics.wordsPerMinute} WPM (${report.biometrics.cadenceStatus.label})
            Fillers Avoided: ${report.biometrics.fillerCount} detected
            Executive Summary: ${report.executiveSummary}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "My Astra 6 Cognitive Diagnostic")
            putExtra(Intent.EXTRA_TEXT, text)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        getApplication<Application>().startActivity(Intent.createChooser(intent, "Share Diagnostic Report").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    fun selectSessionForDetail(session: SessionEntity) {
        _selectedSessionDetail.value = session
    }

    fun dismissSessionDetail() {
        _selectedSessionDetail.value = null
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            if (_selectedSessionDetail.value?.id == sessionId) {
                _selectedSessionDetail.value = null
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _selectedSessionDetail.value = null
        }
    }

    fun clearPromoFeedback() {
        _promoMessage.value = null
        _promoError.value = null
    }

    // Charisma Arena Game Methods
    fun launchGameChallenge(challenge: GameChallenge) {
        if (challenge.isProOnly && !_isPremium.value) {
            triggerPaywall("This high-stakes challenge is locked for Vocalis Pro members. Upgrade or enter judge promo code.")
            return
        }

        if (!gameManager.canPlay(_isPremium.value)) {
            triggerPaywall("Out of Daily Game Energy! Vocalis Pro members receive Unlimited Energy tokens to play all day.")
            return
        }

        gameManager.consumeEnergy(_isPremium.value)

        _activeChallenge.value = challenge
        _gameSecondsRemaining.value = challenge.timeLimitSeconds
        gameElapsedSeconds = 0
        gameLiveTranscript.value = ""
        _gameFillersCount.value = 0
        _gameComposure.value = 100
        _gameResult.value = null
        _showGameResult.value = false
        _isGameRunning.value = false

        navigateTo(Screen.GameRound(challenge.id))

        // Read challenge question aloud
        speechManager.speak(challenge.promptQuestion)
    }

    fun startGameLiveSession() {
        _isGameRunning.value = true
        gameElapsedSeconds = 0
        speechManager.stopSpeaking()

        speechManager.startListening(
            onPartialResult = { text ->
                updateGameSpeech(text)
            },
            onFinalResult = { text ->
                updateGameSpeech(text)
            }
        )

        gameTimerJob?.cancel()
        gameTimerJob = viewModelScope.launch {
            val totalSeconds = _activeChallenge.value?.timeLimitSeconds ?: 30
            for (sec in totalSeconds downTo 0) {
                _gameSecondsRemaining.value = sec
                if (sec == 0) {
                    finishGameRound()
                    break
                }
                kotlinx.coroutines.delay(1000L)
                gameElapsedSeconds++
            }
        }
    }

    private fun updateGameSpeech(newText: String) {
        gameLiveTranscript.value = newText
        val (fillers, _) = gameManager.detectFillersInTranscript(newText)
        _gameFillersCount.value = fillers
        val composure = (100 - (fillers * 12)).coerceIn(15, 100)
        _gameComposure.value = composure
    }

    fun finishGameRound() {
        gameTimerJob?.cancel()
        speechManager.stopListening()
        _isGameRunning.value = false

        val challenge = _activeChallenge.value ?: return
        val result = gameManager.evaluateGameRound(
            challenge = challenge,
            spokenText = gameLiveTranscript.value,
            elapsedSeconds = gameElapsedSeconds.coerceAtLeast(1)
        )

        _gameResult.value = result
        _showGameResult.value = true

        val starWord = when (result.stars) {
            3 -> "Three stars! Incredible composure."
            2 -> "Two stars! Solid delivery."
            else -> "Round complete! Keep practicing."
        }
        speechManager.speak("$starWord You earned ${result.xpEarned} XP.")
    }

    fun stopGameRound() {
        gameTimerJob?.cancel()
        speechManager.stopListening()
        speechManager.stopSpeaking()
        _isGameRunning.value = false
        navigateTo(Screen.GameArena)
    }

    fun dismissGameResult() {
        _showGameResult.value = false
        speechManager.stopSpeaking()
        navigateTo(Screen.GameArena)
    }

    fun refillDemoEnergy() {
        gameManager.refillEnergyForDemo()
        _promoMessage.value = "Energy refilled to 3/3 tokens!"
    }

    fun exportGitHubBuildInPublic() {
        val report = _currentAstraReport.value ?: return
        val markdown = """
            ### 🎙️ Vocalis AI - Rehearsal Dossier & Diagnostic
            *Exported from Vocalis (RevenueCat Shipaton 2026 Edition)*
            
            | Metric | Score / Level | Executive Benchmark |
            | :--- | :--- | :--- |
            | **Readiness Archetype** | `${report.readinessArchetype}` | Top 5% Standard |
            | **Composite Index** | **${report.overallIndex} / 100** | > 85 |
            | **Cadence & Pacing** | **${report.biometrics.wordsPerMinute} WPM** | 130–155 WPM (Optimal) |
            | **Clarity Score** | **${report.radar.clarity}%** | > 90% |
            | **Executive Gravitas** | **${report.radar.gravitas}%** | > 85% |
            | **Structural Framework** | **${report.radar.structure}%** | STAR / BLUF |
            | **Executive Brevity** | **${report.radar.executiveBrevity}%** | High Signal-to-Noise |
            | **Vocal Fillers Detected** | **${report.biometrics.fillerCount}** | Zero Fluff |
            
            #### 🧠 Executive Summary
            > ${report.executiveSummary}
            
            #### ⚡ Key Prescriptions
            ${report.highImpactPrescriptions.mapIndexed { i, p -> "${i + 1}. $p" }.joinToString("\n")}
            
            ---
            *Built with Kotlin, Jetpack Compose, Gemini AI & RevenueCat for Shipaton 2026.*
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Vocalis Rehearsal Dossier (Markdown)")
            putExtra(Intent.EXTRA_TEXT, markdown)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        getApplication<Application>().startActivity(Intent.createChooser(intent, "Export GitHub Markdown").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    fun toggleStudioRecording() {
        if (_isStudioRecording.value) {
            stopStudioRecording()
        } else {
            startStudioRecording()
        }
    }

    fun startStudioRecording() {
        _isStudioRecording.value = true
        _isSimulatedStreamActive.value = false
        studioSimulationJob?.cancel()
        speechManager.startListening(
            onFinalResult = { text ->
                _studioTranscript.value = text
                if (text.isNotBlank()) {
                    val words = text.trim().split("\\s+".toRegex()).size
                    _studioWpm.value = (words * 20).coerceIn(90, 185)
                }
            },
            onPartialResult = { partial ->
                _studioTranscript.value = partial
                if (partial.isNotBlank()) {
                    val words = partial.trim().split("\\s+".toRegex()).size
                    _studioWpm.value = (words * 18).coerceIn(95, 180)
                }
            }
        )
    }

    fun stopStudioRecording() {
        _isStudioRecording.value = false
        speechManager.stopListening()
    }

    fun toggleStudioSimulatedStream() {
        val next = !_isSimulatedStreamActive.value
        _isSimulatedStreamActive.value = next
        if (next) {
            _isStudioRecording.value = false
            speechManager.stopListening()
            startStudioSimulationLoop()
        } else {
            studioSimulationJob?.cancel()
            _studioAudioRms.value = 0.05f
            _studioDecibels.value = -48f
        }
    }

    private fun startStudioSimulationLoop() {
        studioSimulationJob?.cancel()
        studioSimulationJob = viewModelScope.launch {
            var step = 0
            while (_isSimulatedStreamActive.value) {
                val wave = (kotlin.math.sin(step * 0.18) * 0.5 + 0.5).toFloat()
                val flutter = ((step % 7) * 0.04f)
                val level = (wave * 0.65f + flutter).coerceIn(0.12f, 0.95f)
                _studioAudioRms.value = level
                _studioDecibels.value = -44f + (level * 38f)
                _vocalResonanceScore.value = (80 + (level * 18).toInt()).coerceIn(70, 99)
                _diaphragmaticStability.value = (91 + ((1f - flutter) * 7).toInt()).coerceIn(86, 98)
                _vocalClarityScore.value = (88 + (level * 10).toInt()).coerceIn(80, 98)
                _studioPitchHz.value = (120 + (level * 55).toInt()).coerceIn(95, 210)
                _studioJitterPercent.value = (0.32f + (flutter * 0.3f)).coerceIn(0.25f, 0.95f)
                delay(90)
                step++
            }
        }
    }

    fun selectStudioTestPhrase(index: Int) {
        _studioSelectedPhraseIndex.value = index
    }

    override fun onCleared() {
        super.onCleared()
        gameTimerJob?.cancel()
        studioSimulationJob?.cancel()
        speechManager.shutdown()
    }
}
