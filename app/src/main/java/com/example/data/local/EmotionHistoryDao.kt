package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EmotionHistoryDao {
    @Insert
    suspend fun insertHistory(entity: EmotionHistoryEntity): Long

    @Query("SELECT * FROM emotion_history WHERE botId = :botId AND characterName IS NULL ORDER BY timestamp ASC")
    suspend fun getHistoryForBot(botId: String): List<EmotionHistoryEntity>

    @Query("SELECT * FROM emotion_history WHERE botId = :botId AND characterName IS NULL ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentHistoryForBot(botId: String, limit: Int = 20): List<EmotionHistoryEntity>

    @Query("SELECT * FROM emotion_history WHERE botId = :botId AND characterName = :characterName ORDER BY timestamp ASC")
    suspend fun getHistoryForCharacter(botId: String, characterName: String): List<EmotionHistoryEntity>

    @Query("SELECT * FROM emotion_history WHERE botId = :botId AND characterName = :characterName ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentHistoryForCharacter(botId: String, characterName: String, limit: Int = 20): List<EmotionHistoryEntity>

    @Query("DELETE FROM emotion_history WHERE botId = :botId AND messageIndex > :messageIndex")
    suspend fun deleteHistoryAfterMessageIndex(botId: String, messageIndex: Int)

    @Query("DELETE FROM emotion_history WHERE botId = :botId AND timestamp > :timestamp")
    suspend fun deleteHistoryAfterTimestamp(botId: String, timestamp: Long)

    @Query("DELETE FROM emotion_history WHERE botId = :botId")
    suspend fun deleteHistoryForBot(botId: String)

    @Query("SELECT * FROM emotion_history WHERE botId = :botId AND characterName IS NULL AND messageIndex <= :messageIndex ORDER BY messageIndex DESC, timestamp DESC LIMIT 1")
    suspend fun getLatestMainHistoryBeforeOrAt(botId: String, messageIndex: Int): EmotionHistoryEntity?

    @Query("SELECT * FROM emotion_history WHERE botId = :botId AND characterName = :characterName AND messageIndex <= :messageIndex ORDER BY messageIndex DESC, timestamp DESC LIMIT 1")
    suspend fun getLatestCharacterHistoryBeforeOrAt(botId: String, characterName: String, messageIndex: Int): EmotionHistoryEntity?

    @Query("SELECT * FROM emotion_history WHERE botId = :botId ORDER BY messageIndex ASC, timestamp ASC")
    suspend fun getAllHistoryForBot(botId: String): List<EmotionHistoryEntity>
}

@Dao
interface SelfCheckFailureLogDao {
    @Insert
    suspend fun insertLog(entity: SelfCheckFailureLogEntity): Long

    @Query("SELECT * FROM self_check_failure_logs WHERE botId = :botId ORDER BY timestamp DESC")
    suspend fun getLogsForBot(botId: String): List<SelfCheckFailureLogEntity>

    @Query("DELETE FROM self_check_failure_logs WHERE botId = :botId AND (messageIndex > :messageIndex OR timestamp > :timestamp)")
    suspend fun deleteLogsAfterMessageIndexOrTimestamp(botId: String, messageIndex: Int, timestamp: Long)

    @Query("DELETE FROM self_check_failure_logs WHERE botId = :botId")
    suspend fun deleteLogsForBot(botId: String)
}
