package com.cattery.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cattery.domain.models.ReservationDetail
import com.cattery.domain.models.ReservationStatus

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey val id: Long,
    val kittenId: Long,
    val kittenName: String,
    val kittenPhotoUrls: List<String>,
    val litterId: Long,
    val litterName: String,
    val buyerId: Long,
    val buyerName: String,
    val reservedAt: String,
    val status: ReservationStatus,
)

fun ReservationEntity.toDomain() = ReservationDetail(
    id = id,
    kittenId = kittenId,
    kittenName = kittenName,
    kittenPhotoUrls = kittenPhotoUrls,
    litterId = litterId,
    litterName = litterName,
    buyerId = buyerId,
    buyerName = buyerName,
    reservedAt = reservedAt,
    status = status,
)

fun ReservationDetail.toEntity() = ReservationEntity(
    id = id,
    kittenId = kittenId,
    kittenName = kittenName,
    kittenPhotoUrls = kittenPhotoUrls,
    litterId = litterId,
    litterName = litterName,
    buyerId = buyerId,
    buyerName = buyerName,
    reservedAt = reservedAt,
    status = status,
)
