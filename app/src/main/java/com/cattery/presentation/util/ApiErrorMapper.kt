package com.cattery.presentation.util

import com.cattery.data.remote.client.UnauthorizedException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class ErrorResponse(
    val message: String,
    val code: String? = null,
)

private val errorJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

fun Throwable.userMessage(default: String): String {
    parseEmbeddedErrorMessage(message)?.let { return it }
    when (this) {
        is ClientRequestException -> {
            return when (response.status.value) {
                400, 401 -> "Неверный логин или пароль"
                404 -> "Не найдено"
                else -> default
            }
        }
        is ServerResponseException -> return default
        is UnauthorizedException -> return "Сессия истекла. Войдите снова"
        else -> {
            val raw = message.orEmpty()
            if (raw.contains("Client request") || raw.contains("HTTP")) return default
            return raw.ifBlank { default }
        }
    }
}

private fun parseEmbeddedErrorMessage(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    val jsonStart = raw.indexOf("{\"message\"")
    if (jsonStart < 0) return null
    val json = raw.substring(jsonStart).substringBefore('\n').trim()
    return runCatching {
        errorJson.decodeFromString<ErrorResponse>(json).message
    }.getOrNull()?.takeIf { it.isNotBlank() }
}
