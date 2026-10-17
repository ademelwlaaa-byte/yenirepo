package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PromptViolationLogDao {
    @Insert
    suspend fun insertLog(log: PromptViolationLogEntity)

    @Query("SELECT * FROM prompt_violation_logs WHERE botId = :botId ORDER BY timestamp DESC")
    suspend fun getLogsForBot(botId: String): List<PromptViolationLogEntity>

    @Query("DELETE FROM prompt_violation_logs WHERE botId = :botId AND timestamp > :timestamp")
    suspend fun deleteLogsAfterTimestamp(botId: String, timestamp: Long)

    @Query("DELETE FROM prompt_violation_logs WHERE botId = :botId")
    suspend fun deleteLogsForBot(botId: String)
}
