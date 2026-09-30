package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Resilient Fallback Ladder ordered by latency & availability
    private val modelLadder = listOf(
        "gemini-3.6-flash",
        "gemini-3.1-flash-lite",
        "gemini-flash-latest",
        "gemini-3.7-flash"
    )

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateContentWithFallback(
        prompt: String,
        systemInstruction: String? = null,
        temperature: Double = 0.7
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("GEMINI_API_KEY is not configured in Secrets or .env. Please configure your key in the AI Studio Secrets panel.")
            )
        }

        var lastException: Exception? = null

        for (model in modelLadder) {
            try {
                Log.d("GeminiService", "Attempting generation with model: $model")
                val responseText = executeGenerateContent(
                    model = model,
                    apiKey = apiKey,
                    prompt = prompt,
                    systemInstruction = systemInstruction,
                    temperature = temperature
                )
                if (responseText.isNotBlank()) {
                    return@withContext Result.success(responseText)
                }
            } catch (e: Exception) {
                Log.w("GeminiService", "Model $model encountered error: ${e.message}. Trying next fallback...")
                lastException = e
            }
        }

        Result.failure(lastException ?: IOException("Failed to generate content across all fallback models."))
    }

    private fun executeGenerateContent(
        model: String,
        apiKey: String,
        prompt: String,
        systemInstruction: String?,
        temperature: Double
    ): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val rootJson = JSONObject().apply {
            // Contents
            val contentsArray = JSONArray().apply {
                val turnObj = JSONObject().apply {
                    put("role", "user")
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    }
                    put("parts", partsArray)
                }
                put(turnObj)
            }
            put("contents", contentsArray)

            // System Instruction if provided
            if (!systemInstruction.isNullOrBlank()) {
                val sysObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    }
                    put("parts", partsArray)
                }
                put("systemInstruction", sysObj)
            }

            // Generation Config
            val genConfig = JSONObject().apply {
                put("temperature", temperature)
                put("maxOutputTokens", 1500)
            }
            put("generationConfig", genConfig)
        }

        val requestBody = rootJson.toString().toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            val code = response.code
            val responseBodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                // Recoverable status codes in ladder: 429 (quota), 503 (unavailable), 500 (internal), 404 (model deprecated)
                throw IOException("Gemini API error (HTTP $code): $responseBodyString")
            }

            val responseJson = JSONObject(responseBodyString)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                throw IOException("No candidates returned from Gemini API: $responseBodyString")
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text", "") ?: ""

            if (text.isBlank()) {
                throw IOException("Empty text in Gemini response candidate")
            }
            return text
        }
    }
}
