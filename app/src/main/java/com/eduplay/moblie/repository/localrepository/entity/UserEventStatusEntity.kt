package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_status",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = arrayOf("userId"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = arrayOf("eventId"),
            childColumns = arrayOf("eventId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BlockEntity::class,
            parentColumns = arrayOf("blockId"),
            childColumns = arrayOf("blockId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = arrayOf("taskId"),
            childColumns = arrayOf("taskId"),
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index("userId"),
        Index("eventId"),
        Index("blockId"),
        Index("taskId"),
    ]
)
data class UserEventStatusEntity(
    @ColumnInfo(name = "userId")
    val userId: String,
    @ColumnInfo(name = "eventId")
    val eventId: String,
    @ColumnInfo(name = "blockId")
    val blockId: String,
    @ColumnInfo(name = "taskId")
    val taskId: String,
    @ColumnInfo(name = "isFinished")
    val isFinished: Boolean,
    @ColumnInfo(name = "taskStartTime")
    val taskStartTime: Boolean,
    @PrimaryKey(autoGenerate = true)
    val id: Int
)