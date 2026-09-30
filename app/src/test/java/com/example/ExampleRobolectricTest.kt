package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.billing.RevenueCatManager
import com.example.data.model.DifficultyTone
import com.example.data.model.LeadershipCatalog
import com.example.data.remote.GeminiRehearsalRepository
import com.example.data.remote.PaywallException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Polaris AI", appName)
  }

  @Test
  fun `test paywall gating prevents tough mode on free tier`() {
    val repo = GeminiRehearsalRepository()
    assertThrows(PaywallException::class.java) {
      repo.checkPaywallGate(
        tone = DifficultyTone.TOUGH,
        isPremium = false,
        sessionsUsedToday = 0
      )
    }
  }

  @Test
  fun `test paywall gating allows tough mode on pro tier`() {
    val repo = GeminiRehearsalRepository()
    repo.checkPaywallGate(
      tone = DifficultyTone.TOUGH,
      isPremium = true,
      sessionsUsedToday = 10
    )
  }

  @Test
  fun `test paywall gating limits daily sessions on free tier`() {
    val repo = GeminiRehearsalRepository()
    assertThrows(PaywallException::class.java) {
      repo.checkPaywallGate(
        tone = DifficultyTone.STANDARD,
        isPremium = false,
        sessionsUsedToday = 3
      )
    }
  }

  @Test
  fun `test leadership catalog has valid coaching scenarios`() {
    val scenarios = LeadershipCatalog.scenarios
    assertTrue("Should contain multiple manager coaching scenarios", scenarios.size >= 5)
    val feedbackScenario = scenarios.find { it.category == "Feedback" }
    assertNotNull(feedbackScenario)
    val boundariesScenario = scenarios.find { it.category == "Boundaries" }
    assertNotNull(boundariesScenario)
  }

  @Test
  fun `test revenuecat judge promo code unlocks pro access`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val revenueCat = RevenueCatManager.getInstance(context)

    val result = revenueCat.redeemPromoCode("SHIPATON2026")
    assertTrue("Promo code SHIPATON2026 should succeed", result.isSuccess)

    val info = result.getOrNull()
    assertNotNull(info)
    assertTrue("Pro should be active after redeeming SHIPATON2026", info!!.isProActive)
    assertEquals("polaris_pro", info.activeEntitlement)
  }

  @Test
  fun `test revenuecat invalid promo code is rejected`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val revenueCat = RevenueCatManager.getInstance(context)

    val result = revenueCat.redeemPromoCode("INVALID_CODE_123")
    assertTrue("Invalid promo code should fail", result.isFailure)
  }

  @Test
  fun `test charisma game engine detects filler words accurately`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val gameManager = com.example.game.CharismaGameManager.getInstance(context)

    val sampleSpeech = "Um so like basically we should actually launch this feature you know"
    val (count, detected) = gameManager.detectFillersInTranscript(sampleSpeech)

    assertTrue("Should detect multiple filler words", count >= 4)
    assertTrue("Should include 'um'", detected.contains("um"))
    assertTrue("Should include 'like'", detected.contains("like"))
    assertTrue("Should include 'basically'", detected.contains("basically"))
  }

  @Test
  fun `test charisma game evaluation awards stars and composure`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val gameManager = com.example.game.CharismaGameManager.getInstance(context)
    val challenge = com.example.data.model.GameCatalog.challenges.first()

    val cleanSpeech = "Pineapples create a magnificent contrast of sweetness against salty tomato sauce and melted cheese which elevates the pizza to art."
    val result = gameManager.evaluateGameRound(challenge, cleanSpeech, 25)

    assertTrue("Clean speech should earn high composure", result.composureScore >= 80)
    assertTrue("Should earn at least 2 stars for articulate answer", result.stars >= 2)
    assertEquals(0, result.fillerCount)
    assertTrue("Should earn positive XP", result.xpEarned > 100)
  }
}

