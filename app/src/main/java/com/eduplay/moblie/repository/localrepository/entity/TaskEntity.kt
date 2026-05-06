package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.useCases.downloadTaskTypes.DownloadTask

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = BlockEntity::class,
        parentColumns = arrayOf("blockId"),
        childColumns = arrayOf("blockId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("blockId")
    ]
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
    val type: Int,
    @ColumnInfo(name = "time")
    val time: Int,
    @ColumnInfo(name = "points")
    val points: Int,
    @ColumnInfo(name = "partialPoints")
    val partialPoints: Boolean,
    @ColumnInfo(name = "taskOrder")
    val taskOrder: Int
) {
    constructor(
        id: String,
        blockId: String,
        name: String,
        description: String,
        type: TaskType,
        time: Int,
        points: Int,
        partialPoints: Boolean,
        taskOrder: Int
    ) : this(
        id,
        blockId,
        name,
        description,
        type.optionNumber,
        time,
        points,
        partialPoints,
        taskOrder
    )

    constructor(task: DownloadTask) : this(
        task.taskId,
        task.blockId,
        task.name,
        task.description,
        task.type,
        task.time,
        task.points,
        task.partialPoints,
        task.taskOrder
    )
}