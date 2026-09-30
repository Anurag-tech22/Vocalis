package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rehearsal_sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goal: String,
    val tone: String,
    val scenarioType: String,
    val personaName: String,
    val personaRole: String,
    val finalScore: Int,
    val verdict: String,
    val timestamp: Long = System.currentTimeMillis(),
    val turnCount: Int = 1,
    val transcriptJson: String = "[]",
    val readinessLevel: String = "Prepared"
)
