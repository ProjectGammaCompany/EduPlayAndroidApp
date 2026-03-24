package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eduplay.moblie.repository.localrepository.entity.TaskEntity
import com.eduplay.moblie.repository.localrepository.entity.UserEventStatusEntity

@Dao
interface UserEventStatusDao {
    @Transaction
    @Insert
    suspend fun insertStatus(task: UserEventStatusEntity)

    @Transaction
    @Update
    suspend fun updateStatus(task: UserEventStatusEntity)

    @Transaction
    @Delete
    suspend fun deleteStatus(task: UserEventStatusEntity)

    @Transaction
    @Query(
        "SELECT * FROM user_status " + "WHERE userId = :userId AND eventId = :eventId"
    )
    suspend fun getStatusByUserAndEvent(
        userId: String, eventId: String
    ): UserEventStatusEntity?
}