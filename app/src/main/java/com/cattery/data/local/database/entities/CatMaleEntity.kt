package com.cattery.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cattery.domain.models.CatMale

@Entity(tableName = "cat_males")
data class CatMaleEntity(
    @PrimaryKey val id: Long,
    val ownerId: Long,
    val name: String,
    val birthDate: String,
    val color: String,
    val photoUrls: List<String>,
)

fun CatMaleEntity.toDomain() = CatMale(
    id = id,
    ownerId = ownerId,
    name = name,
    birthDate = birthDate,
    color = color,
    photoUrls = photoUrls,
)

fun CatMale.toEntity() = CatMaleEntity(
    id = id,
    ownerId = ownerId,
    name = name,
    birthDate = birthDate,
    color = color,
    photoUrls = photoUrls,
)
