package com.cattery.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cattery.domain.models.CatFemale

@Entity(tableName = "cat_females")
data class CatFemaleEntity(
    @PrimaryKey val id: Long,
    val ownerId: Long,
    val name: String,
    val birthDate: String,
    val matingDate: String?,
    val birthDueDate: String?,
    val photoUrls: List<String>,
)

fun CatFemaleEntity.toDomain() = CatFemale(
    id = id,
    ownerId = ownerId,
    name = name,
    birthDate = birthDate,
    matingDate = matingDate,
    birthDueDate = birthDueDate,
    photoUrls = photoUrls,
)

fun CatFemale.toEntity() = CatFemaleEntity(
    id = id,
    ownerId = ownerId,
    name = name,
    birthDate = birthDate,
    matingDate = matingDate,
    birthDueDate = birthDueDate,
    photoUrls = photoUrls,
)
