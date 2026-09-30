package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

/**
 * High-Precision Zero-Latency Acoustic Sound & Tactile Feedback Engine.
 * Utilizes native Android ToneGenerator for authentic hardware acoustic feedback.
 */
object AudioSoundEngine {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
        } catch (e: Exception) {
            Log.w("AudioSoundEngine", "Failed to initialize ToneGenerator: ${e.message}")
        }
    }

    /**
     * Subtle acoustic click when microphone starts listening.
     */
    fun playMicOpenTone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 60)
        } catch (e: Exception) {
            // Non-blocking fail-safe
        }
    }

    /**
     * Acoustic double-tap click when recording stops.
     */
    fun playMicCloseTone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
        } catch (e: Exception) {
            // Non-blocking fail-safe
        }
    }

    /**
     * High-tension alert tone when a Socratic stress challenge interrupts.
     */
    fun playSocraticInterruptTone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 220)
        } catch (e: Exception) {
            // Non-blocking fail-safe
        }
    }

    /**
     * High-stakes adversarial stress tone for debate gauntlets.
     */
    fun playStressSiren() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 260)
        } catch (e: Exception) {
            // Non-blocking fail-safe
        }
    }

    /**
     * Authoritative chime when AI turn evaluation arrives.
     */
    fun playEvaluationCompleteTone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
        } catch (e: Exception) {
            // Non-blocking fail-safe
        }
    }

    /**
     * Release resources.
     */
    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
