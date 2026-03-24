package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eduplay.moblie.repository.localrepository.entity.ConditionEntity

@Dao
interface ConditionDao {
    @Transaction
    @Insert
    suspend fun insertCondition(condition: ConditionEntity)

    @Transaction
    @Update
    suspend fun updateCondition(condition: ConditionEntity)

    @Transaction
    @Query("SELECT * FROM conditions WHERE conditionId = :id")
    suspend fun getConditionById(id: String): ConditionEntity?
}