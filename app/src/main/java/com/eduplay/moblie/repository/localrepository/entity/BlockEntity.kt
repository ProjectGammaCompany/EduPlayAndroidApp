package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.eduplay.moblie.useCases.downloadTaskTypes.DownloadBlock

@Entity(
    tableName = "blocks", foreignKeys = [ForeignKey(
        entity = EventEntity::class,
        parentColumns = arrayOf("eventId"),
        childColumns = arrayOf("eventId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("eventId")
    ]
)
data class BlockEntity(
    @PrimaryKey
    @ColumnInfo(name = "blockId")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "blockOrder")
    val blockOrder: Int,
    @ColumnInfo(name = "isParallel")
    val isParallel: Boolean,
    @ColumnInfo(name = "showPoints")
    val showPoints: Boolean,
    @ColumnInfo(name = "showAnswers")
    val showAnswers: Boolean,
    @ColumnInfo(name = "partialPoints")
    val partialPoints: Boolean,
    @ColumnInfo(name = "eventId")
    val eventId: String
) {
    constructor(block: DownloadBlock) : this(
        id = block.blockId,
        name = block.name,
        blockOrder = block.blockOrder,
        isParallel = block.isParallel,
        showPoints = block.showPoints,
        showAnswers = block.showAnswers,
        partialPoints = block.partialPoints,
        eventId = block.eventId
    )
}