package com.cattery.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class KittenDetail(
    val kitten: Kitten,
    val litterName: String,
    val motherName: String? = null,
    val fatherName: String? = null,
)
