package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "memory_checkpoints")
data class MemoryCheckpointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val botId: String,
    val checkpointNumber: Int,
    val messageRangeStart: Int,
    val messageRangeEnd: Int,
    val summaryText: String,
    val embedding: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isTemporary: Boolean = false   // true = kesit yedek, özet gelince yerinde güncellenir
)

@Dao
interface MemoryCheckpointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckpoint(checkpoint: MemoryCheckpointEntity): Long

    @Query("SELECT * FROM memory_checkpoints WHERE botId = :botId ORDER BY checkpointNumber DESC")
    suspend fun getCheckpointsForBot(botId: String): List<MemoryCheckpointEntity>

    @Query("SELECT * FROM memory_checkpoints WHERE botId = :botId ORDER BY checkpointNumber DESC LIMIT :limit")
    suspend fun getRecentCheckpoints(botId: String, limit: Int = 3): List<MemoryCheckpointEntity>

    @Query("SELECT MAX(checkpointNumber) FROM memory_checkpoints WHERE botId = :botId")
    suspend fun getMaxCheckpointNumber(botId: String): Int?

    @Query("DELETE FROM memory_checkpoints WHERE botId = :botId AND (messageRangeStart > :messageIndex OR timestamp > :timestamp)")
    suspend fun deleteCheckpointsAfterMessageIndexOrTimestamp(botId: String, messageIndex: Int, timestamp: Long)

    @Query("DELETE FROM memory_checkpoints WHERE botId = :botId")
    suspend fun deleteCheckpointsForBot(botId: String)

    @Query("SELECT * FROM memory_checkpoints WHERE botId = :botId AND isTemporary = 1 ORDER BY checkpointNumber DESC LIMIT 1")
    suspend fun getLatestTemporaryCheckpoint(botId: String): MemoryCheckpointEntity?

    @Query("UPDATE memory_checkpoints SET summaryText = :summaryText, embedding = :embedding, isTemporary = 0 WHERE id = :id")
    suspend fun upgradeTemporaryCheckpoint(id: Long, summaryText: String, embedding: String)
}
