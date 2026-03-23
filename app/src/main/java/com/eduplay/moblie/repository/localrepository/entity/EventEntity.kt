package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
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
//    @ColumnInfo(name = "cover")
//    val cover: String,
    @ColumnInfo(name = "startDate")
    val startDate: String,
    @ColumnInfo(name = "endDate")
    val endDate: String,
    @ColumnInfo(name = "lastEditionDate")
    val lastEditionDate: String,
    @ColumnInfo(name = "groupEvent")
    val groupEvent: Boolean
)
