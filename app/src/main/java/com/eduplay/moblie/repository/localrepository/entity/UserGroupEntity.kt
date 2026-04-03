package com.eduplay.moblie.repository.localrepository.entity

import androidx.compose.ui.tooling.data.Group
import androidx.compose.ui.tooling.data.UiToolingDataApi
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["userId", "groupId"])
data class UserGroupEntity(
    @ColumnInfo(name = "userId")
    val userId: String,
    @ColumnInfo(name = "groupId")
    val groupId: String
)

data class UserWithGroups @OptIn(UiToolingDataApi::class) constructor(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "groupId",
        associateBy = Junction(UserGroupEntity::class)
    )
    val groups: List<GroupEntity>
)
