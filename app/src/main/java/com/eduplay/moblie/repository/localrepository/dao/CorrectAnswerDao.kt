package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eduplay.moblie.repository.localrepository.entity.BlockEntity
import com.eduplay.moblie.repository.localrepository.entity.CorrectAnswerEntity

@Dao
interface CorrectAnswerDao {
    @Transaction
    @Insert
    suspend fun insertAnswer(answer: CorrectAnswerEntity)

    @Transaction
    @Update
    suspend fun updateAnswer(answer: CorrectAnswerEntity)

    @Transaction
    @Query("SELECT * FROM correct_answers WHERE taskId = :taskId")
    suspend fun getAnswersByTask(taskId: String,): List<CorrectAnswerEntity>

    @Transaction
    @Query("SELECT * FROM correct_answers WHERE value = :value")
    suspend fun getAnswerByValue(value: String,): CorrectAnswerEntity?
}