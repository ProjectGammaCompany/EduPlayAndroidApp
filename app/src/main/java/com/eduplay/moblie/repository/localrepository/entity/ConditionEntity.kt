package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.eduplay.moblie.useCases.downloadTaskTypes.DownloadCondition

@Entity(
    tableName = "conditions",
    foreignKeys = [
        ForeignKey(
            entity = BlockEntity::class,
            parentColumns = arrayOf("blockId"),
            childColumns = arrayOf("prevBlockId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BlockEntity::class,
            parentColumns = arrayOf("blockId"),
            childColumns = arrayOf("nextBlockId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("prevBlockId"),
        Index("nextBlockId"),
    ]
)
data class ConditionEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "conditionId")
    val conditionId: String,
    @ColumnInfo(name = "prevBlockId")
    val prevBlockId: String,
    @ColumnInfo(name = "nextBlockId")
    val nextBlockId: String,
    @ColumnInfo(name = "groupName")
    val groupName: String?,
    @ColumnInfo(name = "min")
    val min: Int?,
    @ColumnInfo(name = "max")
    val max: Int?
) {
    constructor(condition: DownloadCondition): this(
        conditionId = condition.conditionId,
        prevBlockId = condition.prevBlockId,
        nextBlockId = condition.nextBlockId,
        groupName = condition.groupName,
        min = if (condition.min != -1) condition.min else null,
        max = if (condition.max != -1) condition.max else null
    )
}
