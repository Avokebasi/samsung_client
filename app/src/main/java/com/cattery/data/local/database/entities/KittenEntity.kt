package com.cattery.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cattery.domain.models.Kitten
import com.cattery.domain.models.KittenStatus

@Entity(tableName = "kittens")
data class KittenEntity(
    @PrimaryKey val id: Long,
    val litterId: Long,
    val name: String,
    val birthDate: String,
    val color: String,
    val birthWeight: Double?,
    val status: KittenStatus,
    val photoUrls: List<String>,
)

fun KittenEntity.toDomain() = Kitten(
    id = id,
    litterId = litterId,
    name = name,
    birthDate = birthDate,
    color = color,
    birthWeight = birthWeight,
    status = status,
    photoUrls = photoUrls,
)

fun Kitten.toEntity() = KittenEntity(
    id = id,
    litterId = litterId,
    name = name,
    birthDate = birthDate,
    color = color,
    birthWeight = birthWeight,
    status = status,
    photoUrls = photoUrls,
)
