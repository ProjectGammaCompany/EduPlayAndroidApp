package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import androidx.room.Update
import com.eduplay.moblie.repository.localrepository.entity.EventEntity

@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: EventEntity)

    @Transaction
    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Transaction
    @Query("SELECT * FROM events  WHERE eventId = :id")
    suspend fun getEventById(id: String): EventEntity?

    @Transaction
    @RawQuery
    suspend fun getEventsByArguments(query: RoomRawQuery): List<EventEntity>

    @Transaction
    @Query(
        "SELECT * FROM events  " +
                "WHERE eventId = (SELECT eventId FROM user_status WHERE userId = :userId AND isFinished = 1 LIMIT 1)" +
                "ORDER BY startDate LIMIT :limit OFFSET :offset"
    )
    suspend fun getCompletedEventByUserId(
        userId: String,
        limit: Int,
        offset: Int
    ): List<EventEntity>

    @Transaction
    @Query(
        "SELECT * FROM events  WHERE instr(authorId, :userId) " +
                "ORDER BY startDate LIMIT :limit OFFSET :offset"
    )
    suspend fun getEventByAuthor(userId: String, limit: Int, offset: Int): List<EventEntity>
}