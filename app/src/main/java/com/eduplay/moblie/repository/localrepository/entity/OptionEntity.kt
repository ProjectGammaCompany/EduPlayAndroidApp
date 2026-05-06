package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.eduplay.moblie.useCases.downloadTaskTypes.DownloadOption

@Entity(
    tableName = "options",
    foreignKeys = [ForeignKey(
        entity = TaskEntity::class,
        parentColumns = arrayOf("taskId"),
        childColumns = arrayOf("taskId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("taskId")
    ]
)
data class OptionEntity(
    @ColumnInfo(name = "optionId")
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "taskId")
    val taskId: String,
    @ColumnInfo(name = "value")
    val value: String,
) {
    constructor(option: DownloadOption) : this(
        option.optionId,
        option.taskId,
        option.value
    )
}