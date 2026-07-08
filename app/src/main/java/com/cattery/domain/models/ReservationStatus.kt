package com.cattery.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class ReservationStatus {
    ACTIVE,
    CANCELLED,
}
