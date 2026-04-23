package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.eduplay.moblie.repository.localrepository.entity.FileEntity

@Dao
interface FileDao {
    @Insert
    suspend fun insertFile(file: FileEntity)

    @Delete
    suspend fun deleteFile(file: FileEntity)

    @Transaction
    @Query("""SELECT fileName FROM files WHERE taskId = :taskId""")
    suspend fun getFilesByTaskId(taskId: String): List<String>

    @Transaction
    @Query("""SELECT fileName FROM files WHERE eventId = :eventId""")
    suspend fun getFilesByEventId(eventId: String): List<String>
}