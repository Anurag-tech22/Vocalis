package com.example.data.model

data class KeynoteScript(
    val id: String,
    val title: String,
    val speaker: String,
    val category: String,
    val targetWpm: Int,
    val lines: List<KeynoteLine>
)

data class KeynoteLine(
    val text: String,
    val cue: String? = null,
    val isPause: Boolean = false,
    val durationSeconds: Int = 4
)

object KeynoteScriptCatalog {
    val allScripts = listOf(
        KeynoteScript(
            id = "jobs_iphone_2007",
            title = "The iPhone Introduction",
            speaker = "Steve Jobs (Macworld 2007)",
            category = "Keynote & Product Launch",
            targetWpm = 135,
            lines = listOf(
                KeynoteLine(
                    text = "This is a day I’ve been looking forward to for two and a half years.",
                    cue = "CALM INTRO • STEADY EYE CONTACT",
                    durationSeconds = 4
                ),
                KeynoteLine(
                    text = "Every once in a while, a revolutionary product comes along that changes everything.",
                    cue = "WEIGHT & GRAVITAS • LOWER VOCAL PITCH",
                    durationSeconds = 5
                ),
                KeynoteLine(
                    text = "In 1984, Apple introduced the Macintosh. It didn't just change Apple; it changed the whole computer industry.",
                    cue = "HISTORICAL ANCHOR • MEASURED TEMPO",
                    durationSeconds = 6
                ),
                KeynoteLine(
                    text = "Today, we are introducing three revolutionary products of this class.",
                    cue = "PROMISE CLUSTER • INTENSIFY ENERGY",
                    durationSeconds = 4
                ),
                KeynoteLine(
                    text = "The first one: a widescreen iPod with touch controls. The second: a revolutionary mobile phone.",
                    cue = "RHYTHMIC CADENCE • LIST FORMAT",
                    durationSeconds = 6
                ),
                KeynoteLine(
                    text = "And the third: a breakthrough internet communications device.",
                    cue = "CRESCENDO • PROJECT HARMONIC ENERGY",
                    durationSeconds = 4
                ),
                KeynoteLine(
                    text = "An iPod... a phone... and an internet communicator.",
                    cue = "TRIADIC REPETITION • DELIBERATE PAUSE",
                    isPause = true,
                    durationSeconds = 5
                ),
                KeynoteLine(
                    text = "Are you getting it? These are not three separate devices. This is one device. And we are calling it iPhone.",
                    cue = "PUNCHLINE REVEAL • MAXIMUM CONVICTION",
                    durationSeconds = 6
                )
            )
        ),
        KeynoteScript(
            id = "nadella_ai_shift",
            title = "The Generational Platform Shift",
            speaker = "Satya Nadella (Microsoft)",
            category = "Visionary Leadership",
            targetWpm = 142,
            lines = listOf(
                KeynoteLine(
                    text = "We are living through a profound, generational platform shift in human computing.",
                    cue = "STRATEGIC ELEVATION • CLEAR ARTICULATION",
                    durationSeconds = 5
                ),
                KeynoteLine(
                    text = "Over the last seventy years, humanity has invented multiple layers of abstraction to harness computation.",
                    cue = "MACRO CONTEXT • LOW JITTER",
                    durationSeconds = 6
                ),
                KeynoteLine(
                    text = "We are moving from a world where humans had to learn the syntax of machines...",
                    cue = "CONTRAST ANCHOR",
                    durationSeconds = 4
                ),
                KeynoteLine(
                    text = "...to a world where computers naturally understand our human intent.",
                    cue = "REVELATION SHIFT • EYE CONTACT",
                    durationSeconds = 5
                ),
                KeynoteLine(
                    text = "Our mission remains foundational: to empower every person and every organization on the planet to achieve more.",
                    cue = "FOUNDATIONAL CALL • STEADY PROJECTION",
                    durationSeconds = 6
                )
            )
        ),
        KeynoteScript(
            id = "huang_gtc_accelerated",
            title = "Accelerated Computing Tipping Point",
            speaker = "Jensen Huang (NVIDIA GTC)",
            category = "Tech Titan Keynote",
            targetWpm = 148,
            lines = listOf(
                KeynoteLine(
                    text = "Welcome to GTC. It is wonderful to see all of you in person.",
                    cue = "WARM WELCOME • SMILE CADENCE",
                    durationSeconds = 3
                ),
                KeynoteLine(
                    text = "Computing has reached the physical limits of general-purpose scaling.",
                    cue = "PROBLEM STYLING • AUTHORITATIVE BARITONE",
                    durationSeconds = 5
                ),
                KeynoteLine(
                    text = "Accelerated computing and generative AI have hit the irreversible tipping point.",
                    cue = "MARKET THESIS • ACCENTUATION",
                    durationSeconds = 5
                ),
                KeynoteLine(
                    text = "New computing platforms, new silicon architectures, and whole new software ecosystems are being born right now.",
                    cue = "CATALYST MOMENTUM",
                    durationSeconds = 6
                ),
                KeynoteLine(
                    text = "The more you buy, the more you save.",
                    cue = "ICONIC TRADEMARK • CONFIDENT SMILE",
                    durationSeconds = 4
                )
            )
        ),
        KeynoteScript(
            id = "yc_seed_pitch",
            title = "High-Stakes Seed & Series A Pitch",
            speaker = "Top 1% Founder Pitch",
            category = "Venture Capital & Raising",
            targetWpm = 140,
            lines = listOf(
                KeynoteLine(
                    text = "Good morning investors. I am the founder of Vocalis.",
                    cue = "BLUF OPENING • CONFIDENT POSTURE",
                    durationSeconds = 3
                ),
                KeynoteLine(
                    text = "Every quarter, enterprise leaders lose three hundred million dollars in lost sales and executive hesitation.",
                    cue = "PROBLEM QUANTIFICATION • GRAVITAS",
                    durationSeconds = 6
                ),
                KeynoteLine(
                    text = "We solve this with real-time neural cognitive speech intelligence on the edge.",
                    cue = "SOLUTION CLARITY • ZERO FLUFF",
                    durationSeconds = 5
                ),
                KeynoteLine(
                    text = "Over the last six months, our ARR grew twenty-two percent month-over-month to two point four million.",
                    cue = "TRACTION PROOF • DELIBERATE PAUSE",
                    isPause = true,
                    durationSeconds = 6
                ),
                KeynoteLine(
                    text = "We are raising four million dollars to capture the executive communication layer. Let's build the future together.",
                    cue = "THE ASK • DIRECT COMMAND",
                    durationSeconds = 5
                )
            )
        )
    )
}
