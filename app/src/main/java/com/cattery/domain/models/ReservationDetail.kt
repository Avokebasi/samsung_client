package com.cattery.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ReservationDetail(
    val id: Long,
    val kittenId: Long,
    val kittenName: String,
    val kittenPhotoUrls: List<String> = emptyList(),
    val litterId: Long,
    val litterName: String,
    val buyerId: Long,
    val buyerName: String,
    val reservedAt: String,
    val status: ReservationStatus,
)
