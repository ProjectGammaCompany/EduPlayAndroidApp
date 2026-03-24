package com.eduplay.moblie.repository.localrepository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val groupName: String,
    @ColumnInfo(name = "min")
    val min: Int,
    @ColumnInfo(name = "max")
    val max: Int
)
