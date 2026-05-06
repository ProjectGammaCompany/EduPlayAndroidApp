package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.eduplay.moblie.useCases.downloadTaskTypes.DownloadGroup

@Entity(
    tableName = "groups",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = arrayOf("eventId"),
            childColumns = arrayOf("eventId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("eventId")
    ]
)
data class GroupEntity(
    @PrimaryKey
    @ColumnInfo(name = "groupId")
    val groupId: String,
    @ColumnInfo(name = "eventId")
    val eventId: String,
    @ColumnInfo(name = "login")
    val login: String,
    @ColumnInfo(name = "password")
    val password: String
) {
    constructor(group: DownloadGroup) : this(
        groupId = group.groupId,
        eventId = group.eventId,
        login = group.login,
        password = group.password
    )
}