package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eduplay.moblie.repository.localrepository.entity.UserEventStatusEntity

@Dao
interface UserEventStatusDao {
    @Transaction
    @Insert
    suspend fun insertStatus(status: UserEventStatusEntity)

    @Transaction
    @Update
    suspend fun updateStatus(status: UserEventStatusEntity)

    @Transaction
    @Delete
    suspend fun deleteStatus(status: UserEventStatusEntity)

    @Transaction
    @Query(
        "SELECT * FROM user_status WHERE userId = :userId AND eventId = :eventId"
    )
    suspend fun getStatusByUserAndEvent(
        userId: String, eventId: String
    ): UserEventStatusEntity?
}