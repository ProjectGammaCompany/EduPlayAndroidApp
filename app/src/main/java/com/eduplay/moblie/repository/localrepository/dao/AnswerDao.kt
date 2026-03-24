package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.eduplay.moblie.repository.localrepository.entity.AnswerEntity

@Dao
interface AnswerDao {
    @Transaction
    @Insert
    suspend fun insertAnswer(answerEntity: AnswerEntity)

    @Transaction
    @Query("SELECT * FROM answers WHERE answerId = :id AND userId = :userId")
    suspend fun getAnswerByIdAndUserId(id: String, userId: String): AnswerEntity?
}