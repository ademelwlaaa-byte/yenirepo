package com.example.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE botId = :botId AND LENGTH(TRIM(text)) > 0 ORDER BY timestamp ASC")
    fun getMessagesForBot(botId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE botId = :botId AND LENGTH(TRIM(text)) > 0 ORDER BY timestamp ASC")
    suspend fun getMessagesForBotList(botId: String): List<MessageEntity>

    @Query("SELECT COUNT(*) FROM messages WHERE botId = :botId AND LENGTH(TRIM(text)) > 0")
    suspend fun getMessageCountForBot(botId: String): Int

    @Query("SELECT * FROM messages WHERE LENGTH(TRIM(text)) > 0")
    suspend fun getAllMessagesList(): List<MessageEntity>

    @Upsert
    suspend fun insertMessage(message: MessageEntity)

    @Upsert
    suspend fun insertAllMessages(messages: List<MessageEntity>)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: String)

    @Query("DELETE FROM messages WHERE botId = :botId")
    suspend fun deleteMessagesForBot(botId: String)

    @Query("DELETE FROM messages WHERE text IS NULL OR LENGTH(TRIM(text)) = 0")
    suspend fun deleteEmptyMessages()

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()

    @Query("SELECT * FROM messages WHERE botId = :botId AND LENGTH(TRIM(text)) > 0 AND text LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    suspend fun searchMessages(botId: String, query: String): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE botId = :botId AND LENGTH(TRIM(text)) > 0 ORDER BY timestamp ASC LIMIT :limit OFFSET :offset")
    suspend fun getMessagesPaged(botId: String, limit: Int, offset: Int): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE botId = :botId AND isStarred = 1 AND LENGTH(TRIM(text)) > 0 ORDER BY timestamp DESC")
    suspend fun getStarredMessages(botId: String): List<MessageEntity>

    @Query("UPDATE messages SET isStarred = :starred WHERE id = :msgId")
    suspend fun setMessageStarred(msgId: String, starred: Boolean)
}
