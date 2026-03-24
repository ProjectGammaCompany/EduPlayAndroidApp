package com.eduplay.moblie.repository.localrepository

import com.eduplay.moblie.repository.localrepository.dao.AnswerDao
import com.eduplay.moblie.repository.localrepository.dao.BlockDao
import com.eduplay.moblie.repository.localrepository.dao.ConditionDao
import com.eduplay.moblie.repository.localrepository.dao.EventDao
import com.eduplay.moblie.repository.localrepository.dao.GroupDao
import com.eduplay.moblie.repository.localrepository.dao.OptionDao
import com.eduplay.moblie.repository.localrepository.dao.TaskDao
import com.eduplay.moblie.repository.localrepository.dao.UserDao
import com.eduplay.moblie.repository.localrepository.dao.UserEventStatusDao

interface EventDatabase {
    fun userDao(): UserDao
    fun answerDao(): AnswerDao
    fun blockDao(): BlockDao
    fun eventDao(): EventDao
    fun groupDao(): GroupDao
    fun optionDao(): OptionDao
    fun taskDao(): TaskDao
    fun userEventStatus(): UserEventStatusDao
    fun conditionDao(): ConditionDao
}