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

    @Transaction
    @Query("""
        SELECT *
        FROM answers 
        JOIN tasks ON answers.taskId = tasks.taskId
        JOIN blocks ON tasks.blockId = blocks.blockId
        WHERE blocks.eventId = :eventId AND answers.userId = :userId AND isFinal = 1
    """)
    suspend fun getAnswerByEventAndUserId(eventId: String, userId: String): List<AnswerEntity>

    @Transaction
    @Query(
        """
        SELECT SUM(answers.points) 
        FROM answers 
        JOIN tasks ON answers.taskId = tasks.taskId
        JOIN blocks ON tasks.blockId = blocks.blockId
        WHERE blocks.eventId = :eventId AND answers.userId = :userId
        """
    )
    suspend fun getTotalPointsForEvent(eventId: String, userId: String): Int

    @Transaction
    @Query(
        """
        DELETE
        FROM answers
        WHERE EXISTS 
            (SELECT * FROM tasks JOIN blocks ON tasks.blockId = blocks.blockId 
            WHERE answers.taskId = tasks.taskId AND blocks.blockId = :blockId AND answers.userId = :userId)
        """
    )
    suspend fun deleteAllAnswersInBlock(blockId: String, userId: String): Int
}