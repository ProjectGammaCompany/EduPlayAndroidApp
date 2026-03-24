package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eduplay.moblie.repository.localrepository.entity.TaskEntity

@Dao
interface TaskDao {
    @Transaction
    @Insert
    suspend fun insertTask(task: TaskEntity)

    @Transaction
    @Update
    suspend fun updateEvent(task: TaskEntity)

    @Transaction
    @Query("SELECT * FROM tasks WHERE blockId = :blockId AND taskOrder = :order")
    suspend fun getTaskByBlockIdAndOrder(blockId: String, order: Int): TaskEntity?
}