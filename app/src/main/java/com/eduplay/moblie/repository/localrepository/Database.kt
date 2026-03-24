package com.eduplay.moblie.repository.localrepository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eduplay.moblie.repository.localrepository.dao.AnswerDao
import com.eduplay.moblie.repository.localrepository.dao.BlockDao
import com.eduplay.moblie.repository.localrepository.dao.ConditionDao
import com.eduplay.moblie.repository.localrepository.dao.EventDao
import com.eduplay.moblie.repository.localrepository.dao.GroupDao
import com.eduplay.moblie.repository.localrepository.dao.OptionDao
import com.eduplay.moblie.repository.localrepository.dao.TaskDao
import com.eduplay.moblie.repository.localrepository.dao.UserDao
import com.eduplay.moblie.repository.localrepository.dao.UserEventStatusDao
import com.eduplay.moblie.repository.localrepository.entity.AnswerEntity
import com.eduplay.moblie.repository.localrepository.entity.BlockEntity
import com.eduplay.moblie.repository.localrepository.entity.ConditionEntity
import com.eduplay.moblie.repository.localrepository.entity.EventEntity
import com.eduplay.moblie.repository.localrepository.entity.GroupEntity
import com.eduplay.moblie.repository.localrepository.entity.OptionEntity
import com.eduplay.moblie.repository.localrepository.entity.TaskEntity
import com.eduplay.moblie.repository.localrepository.entity.UserEntity
import com.eduplay.moblie.repository.localrepository.entity.UserEventStatusEntity

@Database(
    entities = [
        AnswerEntity::class,
        BlockEntity::class,
        ConditionEntity::class,
        EventEntity::class,
        GroupEntity::class,
        OptionEntity::class,
        TaskEntity::class,
        UserEventStatusEntity::class,
        UserEntity::class
    ],
    version = 1
)
abstract class Database : RoomDatabase(), EventDatabase {
    abstract override fun userDao(): UserDao
    abstract override fun answerDao(): AnswerDao
    abstract override fun blockDao(): BlockDao
    abstract override fun conditionDao(): ConditionDao
    abstract override fun eventDao(): EventDao
    abstract override fun groupDao(): GroupDao
    abstract override fun optionDao(): OptionDao
    abstract override fun taskDao(): TaskDao
    abstract override fun userEventStatus(): UserEventStatusDao
}