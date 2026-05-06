package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.eduplay.moblie.useCases.downloadTaskTypes.DownloadEvent
import com.google.gson.Gson

@Entity(
    tableName = "events",
    indices = [
        Index("authorId"),
        Index("title")
    ]
)
data class EventEntity(
    @ColumnInfo(name = "eventId")
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "tags")
    val tags: String,
    @ColumnInfo(name = "cover")
    val cover: String,
    @ColumnInfo(name = "startDate")
    val startDate: String?,
    @ColumnInfo(name = "endDate")
    val endDate: String?,
    @ColumnInfo(name = "lastEditionDate")
    val lastEditionDate: String,
    @ColumnInfo(name = "groupEvent")
    val groupEvent: Boolean,
    @ColumnInfo(name = "authorId")
    val authorId: String
) {
    constructor(
        id: String,
        title: String,
        description: String,
        tags: List<String>,
        cover: String,
        startDate: String?,
        endDate: String?,
        lastEditionDate: String,
        groupEvent: Boolean,
        authorId: List<String>
    ) : this(
        id,
        title,
        description,
        Gson().toJson(tags),
        cover,
        startDate,
        endDate,
        lastEditionDate,
        groupEvent,
        Gson().toJson(authorId)
    )

    constructor(
        event: DownloadEvent,
        coverPath: String,
        tagNames: List<String> = listOf()
    ) : this(
        event.eventId,
        event.title,
        event.description,
        Gson().toJson(tagNames),
        coverPath,
        event.startDate.ifBlank { null },
        event.endDate.ifBlank { null },
        event.lastEditionDate,
        event.groupEvent,
        Gson().toJson(event.authorId)
    )
}
