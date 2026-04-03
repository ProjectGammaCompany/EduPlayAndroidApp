package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(
    tableName = "answers",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = arrayOf("taskId"),
            childColumns = arrayOf("taskId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = arrayOf("userId"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("taskId"),
        Index("userId"),
    ]
)
data class AnswerEntity(
    @ColumnInfo(name = "taskId")
    val taskId: String,
    @ColumnInfo(name = "options")
    val options: String, // as json
    @ColumnInfo(name = "userId")
    val userId: String,
    @ColumnInfo(name = "startTime")
    val startTime: String,
    @ColumnInfo(name = "endTime")
    val endTime: String,
    @ColumnInfo(name = "points")
    val points: Int,
    @ColumnInfo(name = "isFinal")
    val isFinal: Boolean,
    @PrimaryKey(autoGenerate = true)
    val answerId: Long = 0L
) {
    constructor(
        taskId: String,
        options: List<String>,
        userId: String,
        startTime: String,
        endTime: String,
        points: Int,
        isFinal: Boolean
    ) : this(
        taskId,
        Gson().toJson(options),
        userId,
        startTime,
        endTime,
        points,
        isFinal
    )
}