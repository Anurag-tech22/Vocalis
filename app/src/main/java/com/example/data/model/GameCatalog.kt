package com.example.data.model

object GameCatalog {
    val challenges = listOf(
        // 1. No-Filler Gauntlet
        GameChallenge(
            id = "nofiller_pizza",
            title = "Pineapple on Pizza Defense",
            category = GameCategory.NO_FILLER,
            promptQuestion = "You have 30 seconds! Passionately defend why pineapples belong on pizza. Whatever you do, DO NOT say 'um', 'uh', 'like', or 'basically'!",
            timeLimitSeconds = 30,
            targetKeypoints = listOf("sweet and savory balance", "culinary innovation", "texture harmony"),
            scenarioHumor = "An Italian chef is glaring at you with a rolling pin.",
            opponentName = "Chef Luigi",
            opponentTitle = "Traditionalist Master Chef",
            difficultyStars = 1
        ),
        GameChallenge(
            id = "nofiller_mondays",
            title = "Monday Morning Motivation",
            category = GameCategory.NO_FILLER,
            promptQuestion = "Convince a room of exhausted coworkers that Monday is actually the greatest day of the entire week. Speak with pure conviction — zero vocal fillers allowed!",
            timeLimitSeconds = 35,
            targetKeypoints = listOf("clean slate", "fresh momentum", "high productivity"),
            scenarioHumor = "Coworkers are clutching coffee mugs in skeptical disbelief.",
            opponentName = "Skeptical Sarah",
            opponentTitle = "Senior Sleep-Deprived Engineer",
            difficultyStars = 2
        ),
        GameChallenge(
            id = "nofiller_quantum",
            title = "Quantum Physics to a Puppy",
            category = GameCategory.NO_FILLER,
            promptQuestion = "Explain Schrödinger's Cat and superposition in 45 seconds as if talking to a curious golden retriever. Every 'um' will deduct composure!",
            timeLimitSeconds = 45,
            targetKeypoints = listOf("box is closed", "treat is both eaten and uneaten", "observation collapses wave"),
            scenarioHumor = "The puppy is tilting its head and waiting for a tennis ball.",
            opponentName = "Barnaby",
            opponentTitle = "Curious Golden Retriever",
            difficultyStars = 3,
            isProOnly = true
        ),

        // 2. Shark Tank Blitz
        GameChallenge(
            id = "shark_shoe_umbrella",
            title = "Shoe Umbrellas Shark Pitch",
            category = GameCategory.SHARK_TANK,
            promptQuestion = "Pitch 'Shoe-Brella' — mini attachable umbrellas for sneakers to keep them clean in puddles. You have 45 seconds to get a $100k investment!",
            timeLimitSeconds = 45,
            targetKeypoints = listOf("sneakerhead market", "weather protection", "margins and manufacturing"),
            scenarioHumor = "Shark Barbara is frowning and looking at her designer boots.",
            opponentName = "Kevin 'Mr. Wonderful'",
            opponentTitle = "Ruthless Venture Investor",
            difficultyStars = 2
        ),
        GameChallenge(
            id = "shark_snooze_fine",
            title = "The Snooze-Tax Alarm Clock",
            category = GameCategory.SHARK_TANK,
            promptQuestion = "Pitch an alarm clock that charges your credit card $20 and donates it to your worst political rival every time you hit snooze. Sell the habit psychology!",
            timeLimitSeconds = 45,
            targetKeypoints = listOf("loss aversion", "accountability psychology", "recurring subscription fee"),
            scenarioHumor = "Mark Cuban leans forward looking intrigued.",
            opponentName = "Mark Cuban",
            opponentTitle = "Tech Billionaire Shark",
            difficultyStars = 2
        ),
        GameChallenge(
            id = "shark_socks_cloud",
            title = "Left-Sock Cloud Subscription",
            category = GameCategory.SHARK_TANK,
            promptQuestion = "Pitch an on-demand drone delivery service for the single missing sock you always lose in the laundry dryer. Explain the unit economics in 60s!",
            timeLimitSeconds = 60,
            targetKeypoints = listOf("dryer mystery pain point", "instant drone dispatch", "monetization via paired subscriptions"),
            scenarioHumor = "All 5 sharks have their hands on their buzzers.",
            opponentName = "Lori Greiner",
            opponentTitle = "Queen of QVC & Retail",
            difficultyStars = 3,
            isProOnly = true
        ),

        // 3. Crisis De-escalation
        GameChallenge(
            id = "crisis_black_friday",
            title = "Black Friday Server Meltdown",
            category = GameCategory.DE_ESCALATE,
            promptQuestion = "An e-commerce CEO is screaming because the checkout crashed at midnight on Black Friday. You have 40 seconds to calm them down and explain the failover plan!",
            timeLimitSeconds = 40,
            targetKeypoints = listOf("acknowledge the revenue impact", "state exact recovery ETA", "reassure with database backup"),
            scenarioHumor = "The CEO's face is turning bright tomato red through Zoom.",
            opponentName = "Gordon Sterling",
            opponentTitle = "Panicking E-Commerce CEO",
            difficultyStars = 2
        ),
        GameChallenge(
            id = "crisis_stolen_gnome",
            title = "The Garden Gnome Hostage Crisis",
            category = GameCategory.DE_ESCALATE,
            promptQuestion = "Your intense neighbor Mrs. Gable claims your golden retriever buried her antique ceramic gnome. Defuse her rage without paying $500!",
            timeLimitSeconds = 35,
            targetKeypoints = listOf("empathy for heirloom", "offer to help search the yard", "propose collaborative compromise"),
            scenarioHumor = "Mrs. Gable is tapping her garden trowel threateningly.",
            opponentName = "Mrs. Gable",
            opponentTitle = "Neighborhood HOA Enforcer",
            difficultyStars = 1
        ),
        GameChallenge(
            id = "crisis_impossible_deadline",
            title = "The 48-Hour Miracle Demand",
            category = GameCategory.DE_ESCALATE,
            promptQuestion = "The VP of Sales promised a client a feature that takes 2 months, insisting you ship it by Monday morning. De-escalate and hold your ground!",
            timeLimitSeconds = 45,
            targetKeypoints = listOf("firm boundary on tech debt", "offer phased MVP alternative", "protect team integrity"),
            scenarioHumor = "Sales VP is pacing back and forth waving a signed contract.",
            opponentName = "Brad Vance",
            opponentTitle = "High-Pressure Sales VP",
            difficultyStars = 3,
            isProOnly = true
        ),

        // 4. Impromptu Storyteller
        GameChallenge(
            id = "story_coffee_spaceship",
            title = "Coffee, Spaceship & Stolen Stapler",
            category = GameCategory.STORY_DUEL,
            promptQuestion = "You must tell a funny, cohesive 45-second micro-story seamlessly connecting these 3 random elements: a spilled espresso, an alien starship, and a red stapler!",
            timeLimitSeconds = 45,
            targetKeypoints = listOf("seamless narrative arc", "humorous punchline", "incorporate all 3 items naturally"),
            scenarioHumor = "Audience is leaning in waiting for a plot twist.",
            opponentName = "The Narrative Bot",
            opponentTitle = "AI Standup Comedy Judge",
            difficultyStars = 2
        ),
        GameChallenge(
            id = "story_dino_crypto",
            title = "T-Rex, Bitcoin & Burned Toast",
            category = GameCategory.STORY_DUEL,
            promptQuestion = "Invent a 45-second dramatic pitch connecting a hungry Tyrannosaurus Rex, a cold cryptocurrency hard wallet, and a slice of severely burned rye toast!",
            timeLimitSeconds = 45,
            targetKeypoints = listOf("high drama pacing", "creative causality", "surprising resolution"),
            scenarioHumor = "T-Rex is roaring in the background.",
            opponentName = "Professor Chronos",
            opponentTitle = "Time-Traveling Critic",
            difficultyStars = 3,
            isProOnly = true
        ),

        // 5. Awkward Silence Saver
        GameChallenge(
            id = "silence_ceo_elevator",
            title = "30 Seconds in the CEO Elevator",
            category = GameCategory.SILENCE_BREAKER,
            promptQuestion = "You step into an elevator with your company's visionary CEO on floor 40. Floor 1 is in 30 seconds. Break the dead silence with a sharp, memorable insight!",
            timeLimitSeconds = 30,
            targetKeypoints = listOf("thoughtful observation", "avoid weather clichés", "show curiosity about company mission"),
            scenarioHumor = "Awkward elevator chime rings as floor indicator slowly counts down.",
            opponentName = "Helena Vance",
            opponentTitle = "Fortune 500 Chief Executive",
            difficultyStars = 2
        ),
        GameChallenge(
            id = "silence_soup_spill",
            title = "First Date Soup Disaster",
            category = GameCategory.SILENCE_BREAKER,
            promptQuestion = "On a first date at a fancy restaurant, you accidentally splash tomato bisque onto the table. Break the horrifying 5-second silence with wit and charm!",
            timeLimitSeconds = 25,
            targetKeypoints = listOf("self-deprecating humor", "charm without defensiveness", "pivot conversation back to date"),
            scenarioHumor = "Waiter and date are staring at the orange puddle in complete silence.",
            opponentName = "Maya",
            opponentTitle = "Your Intimidatingly Cool Date",
            difficultyStars = 1
        )
    )
}
