package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "correct_answers",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = arrayOf("taskId"),
            childColumns = arrayOf("taskId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("taskId")
    ]
)
data class CorrectAnswerEntity(
    @ColumnInfo(name = "taskId")
    val taskId: String,
    @ColumnInfo(name = "value")
    val value: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
