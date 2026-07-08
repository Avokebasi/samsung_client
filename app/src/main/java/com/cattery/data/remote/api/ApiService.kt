package com.cattery.data.remote.api

import com.cattery.domain.models.CatFemale
import com.cattery.domain.models.CatMale
import com.cattery.domain.models.Kitten
import com.cattery.domain.models.KittenDetail
import com.cattery.domain.models.KittenStatus
import com.cattery.domain.models.Litter
import com.cattery.domain.models.ReservationDetail
import com.cattery.domain.models.User
import com.cattery.domain.models.UserRole
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class RegisterRequest(
    val name: String,
    val username: String,
    val password: String,
    val role: UserRole,
)

@Serializable
data class AuthResponse(val token: String, val user: User)

@Serializable
data class SaveCatFemaleRequest(
    val name: String,
    val birthDate: String,
    val matingDate: String? = null,
    val photoUrls: List<String> = emptyList(),
)

@Serializable
data class SaveCatMaleRequest(
    val name: String,
    val birthDate: String,
    val photoUrls: List<String> = emptyList(),
)

@Serializable
data class SaveLitterRequest(
    val name: String,
    val birthDate: String,
    val totalCount: Int,
    val maleCount: Int,
    val femaleCount: Int,
    val motherId: Long? = null,
    val fatherId: Long? = null,
    val photoUrls: List<String> = emptyList(),
)

@Serializable
data class SaveKittenRequest(
    val litterId: Long,
    val name: String,
    val birthDate: String,
    val color: String,
    val birthWeight: Double? = null,
    val status: KittenStatus = KittenStatus.FREE,
    val photoUrls: List<String> = emptyList(),
)

class ApiService(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): AuthResponse =
        client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun register(request: RegisterRequest): AuthResponse =
        client.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getCurrentUser(): User = client.get("auth/me").body()

    suspend fun logout() {
        client.post("auth/logout")
    }

    suspend fun getCatFemales(): List<CatFemale> = client.get("cat-females").body()

    suspend fun searchCatFemales(query: String): List<CatFemale> =
        client.get("cat-females/search") { parameter("q", query) }.body()

    suspend fun getCatFemale(id: Long): CatFemale = client.get("cat-females/$id").body()

    suspend fun getCatMales(): List<CatMale> = client.get("cat-males").body()

    suspend fun searchCatMales(query: String): List<CatMale> =
        client.get("cat-males/search") { parameter("q", query) }.body()

    suspend fun getCatMale(id: Long): CatMale = client.get("cat-males/$id").body()

    suspend fun getLitters(): List<Litter> = client.get("litters").body()

    suspend fun searchLitters(query: String): List<Litter> =
        client.get("litters/search") { parameter("q", query) }.body()

    suspend fun getLitter(id: Long): Litter = client.get("litters/$id").body()

    suspend fun getLitterKittens(litterId: Long): List<Kitten> =
        client.get("litters/$litterId/kittens").body()

    suspend fun getKittenDetail(id: Long): KittenDetail = client.get("kittens/$id").body()

    suspend fun searchKittens(query: String): List<Kitten> =
        client.get("kittens/search") { parameter("q", query) }.body()

    suspend fun getReservations(): List<ReservationDetail> = client.get("reservations").body()

    suspend fun createCatFemale(request: SaveCatFemaleRequest): CatFemale =
        client.post("cat-females") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun updateCatFemale(id: Long, request: SaveCatFemaleRequest): CatFemale =
        client.put("cat-females/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun deleteCatFemale(id: Long) {
        client.delete("cat-females/$id")
    }

    suspend fun reserveKitten(id: Long) {
        client.post("kittens/$id/reserve")
    }

    suspend fun cancelKittenReservation(id: Long) {
        client.delete("kittens/$id/reserve")
    }
}
