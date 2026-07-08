package com.cattery.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cattery.domain.models.User
import com.cattery.domain.models.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long,
    val username: String,
    val name: String,
    val avatarUrl: String?,
    val role: UserRole,
)

fun UserEntity.toDomain() = User(
    id = id,
    username = username,
    name = name,
    avatarUrl = avatarUrl,
    role = role,
)

fun User.toEntity() = UserEntity(
    id = id,
    username = username,
    name = name,
    avatarUrl = avatarUrl,
    role = role,
)
