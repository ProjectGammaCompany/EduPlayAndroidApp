package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(
    tableName = "events",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = arrayOf("userId"),
        childColumns = arrayOf("authorId"),
        onDelete = ForeignKey.NO_ACTION
    )],
    indices = [
        Index("authorId")
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
    val startDate: String,
    @ColumnInfo(name = "endDate")
    val endDate: String,
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
         startDate: String,
         endDate: String,
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
}
