package com.example.data.model

object LeadershipCatalog {
    val scenarios = listOf(
        LeadershipScenario(
            id = "lead_system_design_faang",
            category = "Tech Giants",
            title = "FAANG Staff Engineer System Design Defense",
            description = "Defend high-throughput sharding, CAP theorem trade-offs, and fallback caches against an adversarial Principal Architect.",
            counterpartPersona = "Marcus (Google/Meta Principal Architect)",
            promptGoal = "Defending distributed cache invalidation and database partition tolerances during an L6/L7 Staff Tech Screen",
            coreSkillTested = "Architectural Rigor & Trade-Off BLUF",
            difficulty = DifficultyTone.TOUGH
        ),
        LeadershipScenario(
            id = "lead_yc_pitch",
            category = "Silicon Valley",
            title = "Y Combinator 60-Second Demo Day Pitch",
            description = "Pitch your high-growth AI SaaS to Tier-1 Silicon Valley VCs. State your wedge, MoM traction, TAM, and capital ask in 60s.",
            counterpartPersona = "Garry (YC Partner & Early-Stage Investor)",
            promptGoal = "Delivering a 60-second venture pitch for an AI B2B developer tool with \$45k MRR and 25% MoM organic growth",
            coreSkillTested = "High-Conviction Elevator Pitch & TAM Precision",
            difficulty = DifficultyTone.TOUGH
        ),
        LeadershipScenario(
            id = "lead_sev1_incident",
            category = "Tech Giants",
            title = "Sev-1 Incident Post-Mortem & Blast Radius",
            description = "Lead the executive stakeholder review after a cascading database outage affected 12 million global users. Deliver blameless accountability.",
            counterpartPersona = "SVP of Infrastructure & Platform Reliability",
            promptGoal = "Conducting a transparent, blameless Sev-1 production outage executive debrief with concrete MTTR and SLA mitigations",
            coreSkillTested = "Executive Crisis Command & SLA Assurance",
            difficulty = DifficultyTone.TOUGH
        ),
        LeadershipScenario(
            id = "lead_comp_negotiation",
            category = "Silicon Valley",
            title = "Executive Compensation & Equity Refresher",
            description = "Negotiate a 25% total compensation increase and annual equity refreshers based on verified multi-million ARR contributions.",
            counterpartPersona = "VP of People & Engineering Compensation Committee",
            promptGoal = "Negotiating executive base salary, performance bonus, and RSU equity refreshers with verified market benchmarking",
            coreSkillTested = "Value-Anchored Negotiation & Gravitas",
            difficulty = DifficultyTone.STANDARD
        ),
        LeadershipScenario(
            id = "lead_feedback_defensive",
            category = "Feedback",
            title = "Feedback to a Defensive Senior Teammate",
            description = "Address critical code review friction and defensiveness with a senior engineer who resists peer feedback.",
            counterpartPersona = "Alex (Senior Engineer, easily defensive)",
            promptGoal = "Giving constructive feedback to a defensive senior engineer about collaborative code reviews",
            coreSkillTested = "Radical Candor & De-escalation",
            difficulty = DifficultyTone.STANDARD
        ),
        LeadershipScenario(
            id = "lead_boundaries_workload",
            category = "Boundaries",
            title = "Setting Boundaries with an Executive",
            description = "A VP asks your team to work through the weekend for an unscheduled sprint. Hold your team's boundary professionally.",
            counterpartPersona = "Claire (VP of Operations, high pressure)",
            promptGoal = "Setting firm boundaries with a VP demanding mandatory weekend work without prior notice",
            coreSkillTested = "Assertive Boundary Enforcement",
            difficulty = DifficultyTone.TOUGH
        ),
        LeadershipScenario(
            id = "lead_saying_no_scope",
            category = "Saying No",
            title = "Saying 'No' to Scope Creep Before Launch",
            description = "A key stakeholder wants three 'small' features right before launch date. Practice saying no without burning bridges.",
            counterpartPersona = "David (Product Lead, eager to add scope)",
            promptGoal = "Saying no to last-minute feature requests before release while offering trade-off alternatives",
            coreSkillTested = "Trade-Off Negotiation",
            difficulty = DifficultyTone.STANDARD
        ),
        LeadershipScenario(
            id = "lead_peer_conflict",
            category = "Conflict",
            title = "Resolving Direct Report Feud",
            description = "Two tech leads have broken down communication over architectural ownership. Re-align them toward shared goals.",
            counterpartPersona = "Jordan (Frustrated Tech Lead)",
            promptGoal = "Mediating a bitter technical feud between two senior direct reports in a 1-on-1 check-in",
            coreSkillTested = "Conflict Mediation & Alignment",
            difficulty = DifficultyTone.STANDARD
        ),
        LeadershipScenario(
            id = "lead_missed_deadlines",
            category = "Performance",
            title = "Addressing Chronic Sprint Slips",
            description = "Have an empathetic yet accountability-driven discussion with an engineer who repeatedly misses commitments.",
            counterpartPersona = "Taylor (Overwhelmed Mid-level Developer)",
            promptGoal = "Discussing repeated missed sprint deadlines and establishing a concrete turnaround plan",
            coreSkillTested = "Empathetic Accountability",
            difficulty = DifficultyTone.SUPPORTIVE
        ),
        LeadershipScenario(
            id = "lead_unpopular_pivot",
            category = "Change",
            title = "Announcing a Strategic Product Pivot",
            description = "The executive board sunsetted the project your team worked on for 6 months. Deliver the news and rally morale.",
            counterpartPersona = "Skeptical Engineering Team Lead",
            promptGoal = "Communicating executive cancellation of a flagship project and re-orienting the team on new priorities",
            coreSkillTested = "Transparent Morale Leadership",
            difficulty = DifficultyTone.TOUGH
        )
    )
}
