package com.cattery.data.remote.client

import com.cattery.BuildConfig
import com.cattery.data.local.datastore.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class UnauthorizedException : Exception("Unauthorized")

fun createKtorClient(
    tokenManager: TokenManager,
    onUnauthorized: () -> Unit,
): HttpClient {
    val authPlugin = createClientPlugin("BearerAuth") {
        onRequest { request, _ ->
            val token = tokenManager.getTokenSync()
            if (!token.isNullOrBlank()) {
                request.headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    return HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                },
            )
        }

        install(Logging) {
            level = LogLevel.INFO
        }

        install(authPlugin)

        defaultRequest {
            url(BuildConfig.API_BASE_URL.trimEnd('/') + "/")
        }

        HttpResponseValidator {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    runBlocking { tokenManager.clearToken() }
                    onUnauthorized()
                    throw UnauthorizedException()
                }
            }
        }

        expectSuccess = true
    }
}
