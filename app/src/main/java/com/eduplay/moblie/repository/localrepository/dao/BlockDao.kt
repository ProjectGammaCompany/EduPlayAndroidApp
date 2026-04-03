package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eduplay.moblie.repository.localrepository.entity.BlockEntity

@Dao
interface BlockDao {
    @Transaction
    @Insert
    suspend fun insertBlock(block: BlockEntity)

    @Transaction
    @Update
    suspend fun updateBlock(block: BlockEntity)

    @Transaction
    @Query("SELECT * FROM blocks WHERE blockId = :id")
    suspend fun getBlockById(id: String): BlockEntity?

    @Transaction
    @Query("SELECT * FROM blocks WHERE eventId = :eventId AND blockOrder = :blockOrder")
    suspend fun getBlockByEventIdAndBlockOrder(eventId: String, blockOrder: Int): BlockEntity?

    @Transaction
    @Query("""
        SELECT SUM(points) FROM tasks
        JOIN blocks ON tasks.blockId = blocks.blockId 
        WHERE blocks.blockId = :blockId
    """)
    suspend fun getPointsInBlockById(blockId: String): Int
}