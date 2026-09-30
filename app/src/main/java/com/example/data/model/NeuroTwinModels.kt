package com.example.data.model

data class LogicPyramidNode(
    val id: String,
    val title: String,
    val claim: String,
    val evidenceType: String,
    val confidenceScore: Int,
    val isMeceValid: Boolean = true,
    val subClaims: List<String> = emptyList()
)

data class LogicFractureAlert(
    val fallacyType: String,
    val excerpt: String,
    val countermove: String,
    val severity: String
)

data class CharismaTwinDuel(
    val scenarioTitle: String,
    val userExcerpt: String,
    val executiveTwinAudioScript: String,
    val blufPrinciple: String,
    val convictionMultiplier: Float,
    val brevitySavedPercent: Int,
    val powerVerbComparison: Pair<String, String>
)

object NeuroTwinCatalog {
    val sampleDuels = listOf(
        CharismaTwinDuel(
            scenarioTitle = "Asking for a 25% Compensation Increase",
            userExcerpt = "Um, hi Sarah, so I was hoping maybe we could talk about compensation? I feel like I've been doing a lot of work and inflation is high, so maybe if there's budget room...",
            executiveTwinAudioScript = "Sarah, over the past three quarters, my team shipped the unified billing pipeline, which drove thirty-four percent year-over-year revenue expansion. Given this sustained business impact, I am targeting an adjustment to one hundred and ninety-five thousand base. Let's discuss the roadmap to finalize this before Q3 close.",
            blufPrinciple = "BLUF: Anchor with quantifiable business impact before issuing an exact target number.",
            convictionMultiplier = 3.6f,
            brevitySavedPercent = 38,
            powerVerbComparison = Pair("I was hoping maybe...", "I am targeting an adjustment...")
        ),
        CharismaTwinDuel(
            scenarioTitle = "Defending a Missed Engineering Deadline",
            userExcerpt = "Sorry everyone, but QA found some bugs and frontend had merge conflicts and the third-party API was slow so that's why we couldn't release on Friday.",
            executiveTwinAudioScript = "The release is rescheduled for Tuesday at 09:00 hours. During final staging, we isolated two critical edge cases in the payment gateway. Shipping Friday would have exposed forty thousand users to transaction drops. We have resolved the root cause and the deployment pipeline is locked for verification.",
            blufPrinciple = "Executive Accountability: State new delivery milestone first, followed by value protection rationale.",
            convictionMultiplier = 4.1f,
            brevitySavedPercent = 45,
            powerVerbComparison = Pair("Sorry everyone, but...", "The release is rescheduled for Tuesday...")
        ),
        CharismaTwinDuel(
            scenarioTitle = "Pitching a Risky Architectural Refactor to VP",
            userExcerpt = "Our technical debt is really bad and our code is getting messy, so we kind of need to rewrite the core service or developers are going to get mad.",
            executiveTwinAudioScript = "We have reached an operational inflection point. The legacy monolithic architecture is currently adding seven days to every feature release and consuming twenty percent of our sprint velocity. By executing a four-week phased modularization, we will reduce deployment cycle times by half and eliminate sixty percent of regression incidents.",
            blufPrinciple = "Financial & Velocity Framing: Translate tech debt into release velocity and developer capacity.",
            convictionMultiplier = 3.8f,
            brevitySavedPercent = 32,
            powerVerbComparison = Pair("Code is messy and people might get mad", "Reduce deployment cycle times by half")
        )
    )

    val samplePyramid = listOf(
        LogicPyramidNode(
            id = "apex",
            title = "APEX CONCLUSION (BLUF)",
            claim = "Migrate 100% of core search infrastructure to the real-time vector pipeline by end of Q3.",
            evidenceType = "Strategic Directive",
            confidenceScore = 96,
            isMeceValid = true,
            subClaims = listOf(
                "Eliminates 3 legacy search clusters",
                "Decreases p99 query latency from 240ms to 42ms"
            )
        ),
        LogicPyramidNode(
            id = "pillar_1",
            title = "PILLAR 1: CUSTOMER RETENTION",
            claim = "Sub-50ms search latency directly drives an 18% lift in session conversion based on internal A/B experiments.",
            evidenceType = "Empirical Data",
            confidenceScore = 92,
            isMeceValid = true
        ),
        LogicPyramidNode(
            id = "pillar_2",
            title = "PILLAR 2: INFRASTRUCTURE COST",
            claim = "Decommissioning the self-hosted Elasticsearch clusters saves $140,000 annually in compute and maintenance overhead.",
            evidenceType = "Financial Metric",
            confidenceScore = 95,
            isMeceValid = true
        ),
        LogicPyramidNode(
            id = "pillar_3",
            title = "PILLAR 3: EXECUTION FEASIBILITY",
            claim = "Dual-write canary is already operating in staging with zero data loss across 10 million test queries.",
            evidenceType = "Risk Mitigation",
            confidenceScore = 89,
            isMeceValid = true
        )
    )

    val sampleFractures = listOf(
        LogicFractureAlert(
            fallacyType = "Hedging Qualifier",
            excerpt = "\"I kind of feel like maybe it's the right choice...\"",
            countermove = "Eliminate \"feel like\". Assert: \"The data demonstrates this is the optimal path forward.\"",
            severity = "CRITICAL"
        ),
        LogicFractureAlert(
            fallacyType = "Unsubstantiated Assumption",
            excerpt = "\"Everyone knows our current vendor is too expensive...\"",
            countermove = "Cite the contract differential: \"Our current vendor renewal sits at $240K versus $160K for the modern alternative.\"",
            severity = "MODERATE"
        )
    )
}
