package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eduplay.moblie.repository.localrepository.entity.GroupEntity

@Dao
interface GroupDao {
    @Transaction
    @Insert
    suspend fun insertGroup(group: GroupEntity)

    @Transaction
    @Update
    suspend fun updateGroup(group: GroupEntity)

    @Transaction
    @Query("SELECT * FROM `groups` WHERE groupId = :id")
    suspend fun getGroupById(id: String): GroupEntity?
}