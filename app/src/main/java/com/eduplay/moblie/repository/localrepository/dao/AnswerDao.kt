package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eduplay.moblie.repository.localrepository.entity.AnswerEntity

@Dao
interface AnswerDao {
    @Transaction
    @Insert
    suspend fun insertAnswer(answerEntity: AnswerEntity)

    @Transaction
    @Update
    suspend fun updateAnswer(answerEntity: AnswerEntity)

    @Transaction
    @Query("SELECT * FROM answers WHERE taskId = :taskId AND userId = :userId")
    suspend fun getAnswerByTaskAndUserId(taskId: String, userId: String): AnswerEntity?
}