package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "files",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = arrayOf("eventId"),
            childColumns = arrayOf("eventId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = arrayOf("taskId"),
            childColumns = arrayOf("taskId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("eventId")
    ]
)

data class FileEntity (
    @ColumnInfo(name = "eventId")
    val eventId: String,
    @ColumnInfo(name = "taskId")
    val taskId: String,
    @ColumnInfo(name = "fileName")
    val fileName: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "fileId")
    val id: Int = 0
)