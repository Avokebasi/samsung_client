package com.cattery.data.remote.repository

import com.cattery.data.local.datastore.TokenManager
import com.cattery.data.local.repository.LocalRepository
import com.cattery.data.remote.api.ApiService
import com.cattery.data.remote.api.LoginRequest
import com.cattery.data.remote.api.RegisterRequest
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

    suspend fun logout() {
        runCatching { apiService.logout() }
        tokenManager.clearToken()
        localRepository.clearAll()
    }
}
