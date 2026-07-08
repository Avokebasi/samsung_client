package com.cattery.data.local.database

import androidx.room.TypeConverter
import com.cattery.domain.models.KittenStatus
import com.cattery.domain.models.ReservationStatus
import com.cattery.domain.models.UserRole
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class CatteryConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = UserRole.valueOf(value)

    @TypeConverter
    fun fromKittenStatus(status: KittenStatus): String = status.name

    @TypeConverter
    fun toKittenStatus(value: String): KittenStatus = KittenStatus.valueOf(value)

    @TypeConverter
    fun fromReservationStatus(status: ReservationStatus): String = status.name

    @TypeConverter
    fun toReservationStatus(value: String): ReservationStatus = ReservationStatus.valueOf(value)

    @TypeConverter
    fun fromPhotoUrls(urls: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), urls)

    @TypeConverter
    fun toPhotoUrls(raw: String): List<String> =
        runCatching {
            json.decodeFromString(ListSerializer(String.serializer()), raw)
        }.getOrDefault(emptyList())
}
