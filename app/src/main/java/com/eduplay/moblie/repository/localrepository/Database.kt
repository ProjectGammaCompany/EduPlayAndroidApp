package com.eduplay.moblie.repository.localrepository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eduplay.moblie.repository.localrepository.dao.UserDao
import com.eduplay.moblie.repository.localrepository.entity.UserEntity

@Database(
    entities = [
        UserEntity::class
    ],
    version = 1
)
abstract class Database:  RoomDatabase(), EventDatabase {
    abstract override fun userDao(): UserDao
}