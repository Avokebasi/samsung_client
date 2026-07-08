package com.cattery.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class KittenStatus {
    FREE,
    RESERVED,
}
