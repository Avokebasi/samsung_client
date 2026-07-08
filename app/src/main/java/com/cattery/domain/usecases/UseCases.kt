package com.cattery.domain.usecases

import com.cattery.data.local.datastore.TokenManager
import com.cattery.data.local.repository.LocalRepository
import com.cattery.data.remote.repository.RemoteRepository
import com.cattery.domain.models.CatFemale
import com.cattery.domain.models.CatMale
import com.cattery.domain.models.Litter
import com.cattery.domain.models.ReservationDetail
import com.cattery.domain.models.User
import com.cattery.domain.models.UserRole
import kotlinx.coroutines.flow.Flow

class AuthUseCases(
    private val remoteRepository: RemoteRepository,
    private val tokenManager: TokenManager,
) {
    val tokenFlow: Flow<String?> = tokenManager.tokenFlow

    suspend fun init() = tokenManager.init()

    suspend fun isLoggedIn(): Boolean = !tokenManager.getToken().isNullOrBlank()

    suspend fun login(username: String, password: String): Result<User> = runCatching {
        remoteRepository.login(username, password)
    }

    suspend fun register(name: String, username: String, password: String, role: UserRole): Result<User> =
        runCatching {
            remoteRepository.register(name, username, password, role)
        }

    suspend fun getCurrentUser(): Result<User> = runCatching {
        remoteRepository.getCurrentUser()
    }

    suspend fun logout() {
        remoteRepository.logout()
    }
}

class CatalogUseCases(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
) {
    fun observeCatFemales(): Flow<List<CatFemale>> = localRepository.observeCatFemales()

    fun observeCatMales(): Flow<List<CatMale>> = localRepository.observeCatMales()

    fun observeLitters(): Flow<List<Litter>> = localRepository.observeLitters()

    fun observeReservations(): Flow<List<ReservationDetail>> = localRepository.observeReservations()

    fun observeCurrentUser(): Flow<User?> = localRepository.observeCurrentUser()

    suspend fun refreshAll(): Result<Unit> = runCatching {
        remoteRepository.refreshCatalog()
        remoteRepository.refreshReservations()
    }

    suspend fun updateAvatar(avatarUrl: String): Result<User> = runCatching {
        remoteRepository.updateAvatar(avatarUrl)
    }
}
