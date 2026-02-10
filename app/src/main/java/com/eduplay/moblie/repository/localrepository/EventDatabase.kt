package com.eduplay.moblie.repository.localrepository

import com.eduplay.moblie.repository.localrepository.dao.UserDao

interface EventDatabase {
    fun userDao(): UserDao
}