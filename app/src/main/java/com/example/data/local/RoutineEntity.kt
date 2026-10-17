package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val botId: String,
    val routineDescription: String,
    val frequency: String = "weekly", // "daily", "weekly", "monthly"
    val dayOfWeek: String? = null,    // "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"
    val lastMentionedStoryDate: String = "" // "YYYY-MM-DD"
)

@Dao
interface RoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<RoutineEntity>)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Query("SELECT * FROM routines WHERE botId = :botId")
    suspend fun getRoutinesForBot(botId: String): List<RoutineEntity>

    @Query("SELECT * FROM routines WHERE botId = :botId AND routineDescription = :desc LIMIT 1")
    suspend fun findRoutineByDescription(botId: String, desc: String): RoutineEntity?

    @Query("DELETE FROM routines WHERE id = :id")
    suspend fun deleteRoutine(id: Long)

    @Query("DELETE FROM routines WHERE botId = :botId")
    suspend fun deleteRoutinesForBot(botId: String)
}
