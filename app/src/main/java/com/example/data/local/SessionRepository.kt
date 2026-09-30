package com.example.data.local

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class SessionRepository(private val sessionDao: SessionDao) {

    val allSessions: Flow<List<SessionEntity>> = sessionDao.getAllSessions()

    val chronologicalSessions: Flow<List<SessionEntity>> = sessionDao.getSessionsChronological()

    fun getSessionById(id: Long): Flow<SessionEntity?> = sessionDao.getSessionById(id)

    suspend fun insertSession(session: SessionEntity): Long = sessionDao.insertSession(session)

    suspend fun deleteSession(id: Long) = sessionDao.deleteSessionById(id)

    suspend fun clearHistory() = sessionDao.deleteAllSessions()

    fun getTodaySessionsCount(): Flow<Int> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return sessionDao.getSessionsCountSince(calendar.timeInMillis)
    }
}
