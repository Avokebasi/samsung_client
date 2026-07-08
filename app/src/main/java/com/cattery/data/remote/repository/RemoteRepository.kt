package com.cattery.data.remote.repository

import com.cattery.data.local.datastore.TokenManager
import com.cattery.data.local.repository.LocalRepository
import com.cattery.data.remote.api.ApiService
import com.cattery.data.remote.api.LoginRequest
import com.cattery.data.remote.api.RegisterRequest
import com.cattery.data.remote.api.SaveCatFemaleRequest
import com.cattery.data.remote.api.SaveCatMaleRequest
import com.cattery.data.remote.api.SaveKittenRequest
import com.cattery.data.remote.api.SaveLitterRequest
import com.cattery.domain.models.CatFemale
import com.cattery.domain.models.CatMale
import com.cattery.domain.models.Kitten
import com.cattery.domain.models.KittenDetail
import com.cattery.domain.models.Litter
import com.cattery.domain.models.User
import com.cattery.domain.models.UserRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RemoteRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val localRepository: LocalRepository,
) {
    private val _unauthorizedEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val unauthorizedEvents: SharedFlow<Unit> = _unauthorizedEvents.asSharedFlow()

    fun notifyUnauthorized() {
        _unauthorizedEvents.tryEmit(Unit)
    }

    suspend fun login(username: String, password: String): User {
        val response = apiService.login(LoginRequest(username, password))
        tokenManager.saveToken(response.token)
        localRepository.cacheUser(response.user)
        return response.user
    }

    suspend fun register(name: String, username: String, password: String, role: UserRole): User {
        val response = apiService.register(RegisterRequest(name, username, password, role))
        tokenManager.saveToken(response.token)
        localRepository.cacheUser(response.user)
        return response.user
    }

    suspend fun getCurrentUser(): User {
        val user = apiService.getCurrentUser()
        localRepository.cacheUser(user)
        return user
    }

    suspend fun updateAvatar(avatarUrl: String): User {
        val user = apiService.updateAvatar(avatarUrl)
        localRepository.cacheUser(user)
        return user
    }

    suspend fun refreshCatalog() {
        val catFemales = apiService.getCatFemales()
        val catMales = apiService.getCatMales()
        val litters = apiService.getLitters()
        val kittens = litters.flatMap { litter ->
            apiService.getLitterKittens(litter.id)
        }
        localRepository.syncCatalog(catFemales, catMales, litters, kittens)
    }

    suspend fun refreshReservations() {
        val reservations = apiService.getReservations()
        localRepository.cacheReservations(reservations)
    }

    suspend fun searchCatFemales(query: String): List<CatFemale> {
        val items = apiService.searchCatFemales(query)
        items.forEach { localRepository.upsertCatFemale(it) }
        return items
    }

    suspend fun searchCatMales(query: String): List<CatMale> {
        val items = apiService.searchCatMales(query)
        items.forEach { localRepository.upsertCatMale(it) }
        return items
    }

    suspend fun searchLitters(query: String): List<Litter> {
        val items = apiService.searchLitters(query)
        localRepository.upsertLitters(items)
        return items
    }

    suspend fun fetchCatFemale(id: Long): CatFemale {
        val item = apiService.getCatFemale(id)
        localRepository.upsertCatFemale(item)
        return item
    }

    suspend fun fetchCatFemaleLitters(id: Long): List<Litter> {
        val items = apiService.getCatFemaleLitters(id)
        localRepository.upsertLitters(items)
        return items
    }

    suspend fun fetchCatMale(id: Long): CatMale {
        val item = apiService.getCatMale(id)
        localRepository.upsertCatMale(item)
        return item
    }

    suspend fun fetchCatMaleLitters(id: Long): List<Litter> {
        val items = apiService.getCatMaleLitters(id)
        localRepository.upsertLitters(items)
        return items
    }

    suspend fun fetchLitter(id: Long): Litter {
        val item = apiService.getLitter(id)
        localRepository.upsertLitter(item)
        return item
    }

    suspend fun fetchLitterKittens(litterId: Long): List<Kitten> {
        val items = apiService.getLitterKittens(litterId)
        localRepository.upsertKittens(items)
        return items
    }

    suspend fun fetchKittenDetail(id: Long): KittenDetail {
        val detail = apiService.getKittenDetail(id)
        localRepository.upsertKitten(detail.kitten)
        return detail
    }

    suspend fun saveCatFemale(id: Long?, request: SaveCatFemaleRequest): CatFemale {
        val item = if (id == null) apiService.createCatFemale(request) else apiService.updateCatFemale(id, request)
        localRepository.upsertCatFemale(item)
        return item
    }

    suspend fun deleteCatFemale(id: Long) {
        apiService.deleteCatFemale(id)
        refreshCatalog()
    }

    suspend fun saveCatMale(id: Long?, request: SaveCatMaleRequest): CatMale {
        val item = if (id == null) apiService.createCatMale(request) else apiService.updateCatMale(id, request)
        localRepository.upsertCatMale(item)
        return item
    }

    suspend fun deleteCatMale(id: Long) {
        apiService.deleteCatMale(id)
        refreshCatalog()
    }

    suspend fun saveLitter(id: Long?, request: SaveLitterRequest): Litter {
        val item = if (id == null) apiService.createLitter(request) else apiService.updateLitter(id, request)
        localRepository.upsertLitter(item)
        return item
    }

    suspend fun deleteLitter(id: Long) {
        apiService.deleteLitter(id)
        refreshCatalog()
    }

    suspend fun saveKitten(id: Long?, request: SaveKittenRequest): Kitten {
        val item = if (id == null) apiService.createKitten(request) else apiService.updateKitten(id, request)
        localRepository.upsertKitten(item)
        return item
    }

    suspend fun deleteKitten(id: Long) {
        apiService.deleteKitten(id)
        refreshCatalog()
    }

    suspend fun logout() {
        runCatching { apiService.logout() }
        tokenManager.clearToken()
        localRepository.clearAll()
    }
}
