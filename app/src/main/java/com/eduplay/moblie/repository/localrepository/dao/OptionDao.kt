package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eduplay.moblie.repository.localrepository.entity.OptionEntity

@Dao
interface OptionDao {
    @Transaction
    @Insert
    suspend fun insertOption(option: OptionEntity)

    @Transaction
    @Update
    suspend fun updateOption(option: OptionEntity)

    @Transaction
    @Query("SELECT * FROM options WHERE taskId = :taskId")
    suspend fun getOptionsByTaskId(taskId: String): List<OptionEntity>
}