package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.eduplay.moblie.models.TaskType

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = BlockEntity::class,
        parentColumns = arrayOf("blockId"),
        childColumns = arrayOf("blockId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "taskId")
    val id: String,
    @ColumnInfo(name = "blockId")
    val blockId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "type")
    val type: TaskType,
    @ColumnInfo(name = "files")
    val files: List<String>,
    @ColumnInfo(name = "time")
    val time: Int,
    @ColumnInfo(name = "points")
    val points: Int,
    @ColumnInfo(name = "partialPoints")
    val partialPoints: Boolean,
    @ColumnInfo(name = "taskOrder")
    val taskOrder: Int
)