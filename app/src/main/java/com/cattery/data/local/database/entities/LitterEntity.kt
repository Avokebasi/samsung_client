package com.cattery.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cattery.domain.models.Litter

@Entity(tableName = "litters")
data class LitterEntity(
    @PrimaryKey val id: Long,
    val ownerId: Long,
    val name: String,
    val birthDate: String,
    val totalCount: Int,
    val maleCount: Int,
    val femaleCount: Int,
    val motherId: Long?,
    val fatherId: Long?,
    val photoUrls: List<String>,
)

fun LitterEntity.toDomain() = Litter(
    id = id,
    ownerId = ownerId,
    name = name,
    birthDate = birthDate,
    totalCount = totalCount,
    maleCount = maleCount,
    femaleCount = femaleCount,
    motherId = motherId,
    fatherId = fatherId,
    photoUrls = photoUrls,
)

fun Litter.toEntity() = LitterEntity(
    id = id,
    ownerId = ownerId,
    name = name,
    birthDate = birthDate,
    totalCount = totalCount,
    maleCount = maleCount,
    femaleCount = femaleCount,
    motherId = motherId,
    fatherId = fatherId,
    photoUrls = photoUrls,
)
