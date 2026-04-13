package com.eduplay.moblie.repository.localrepository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.eduplay.moblie.repository.localrepository.entity.GroupEntity
import com.eduplay.moblie.repository.localrepository.entity.UserGroupEntity

@Dao
interface GroupDao {
    @Transaction
    @Insert
    suspend fun insertGroup(group: GroupEntity)

    @Transaction
    @Insert
    suspend fun insertUserGroup(usergroup: UserGroupEntity)

    @Transaction
    @Update
    suspend fun updateGroup(group: GroupEntity)

    @Transaction
    @Query("SELECT * FROM `groups` WHERE groupId = :id")
    suspend fun getGroupById(id: String): GroupEntity?

    @Transaction
    @Query("SELECT * FROM `groups` WHERE login = :login")
    suspend fun getGroupByLogin(login: String): GroupEntity?

    @Transaction
    @Query("SELECT * FROM `groups` WHERE login = :login AND eventId = :eventId")
    suspend fun getGroupByEventIdAndLogin(eventId: String, login: String): GroupEntity?
}