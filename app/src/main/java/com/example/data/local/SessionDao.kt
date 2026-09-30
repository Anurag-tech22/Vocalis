package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM rehearsal_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM rehearsal_sessions WHERE id = :id LIMIT 1")
    fun getSessionById(id: Long): Flow<SessionEntity?>

    @Query("SELECT * FROM rehearsal_sessions ORDER BY timestamp ASC")
    fun getSessionsChronological(): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Query("DELETE FROM rehearsal_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)

    @Query("DELETE FROM rehearsal_sessions")
    suspend fun deleteAllSessions()

    @Query("SELECT COUNT(*) FROM rehearsal_sessions WHERE timestamp >= :startOfDayTimestamp")
    fun getSessionsCountSince(startOfDayTimestamp: Long): Flow<Int>
}
