package com.cattery.domain.usecases

import com.cattery.data.local.datastore.TokenManager
import com.cattery.data.local.repository.LocalRepository
import com.cattery.data.remote.repository.RemoteRepository
import com.cattery.domain.models.CatFemale
import com.cattery.domain.models.CatMale
import com.cattery.domain.models.Kitten
import com.cattery.domain.models.KittenDetail
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

    fun observeCatFemale(id: Long): Flow<CatFemale?> = localRepository.observeCatFemale(id)

    fun observeCatMale(id: Long): Flow<CatMale?> = localRepository.observeCatMale(id)

    fun observeLitter(id: Long): Flow<Litter?> = localRepository.observeLitter(id)

    fun observeLittersByMother(motherId: Long): Flow<List<Litter>> =
        localRepository.observeLittersByMother(motherId)

    fun observeLittersByFather(fatherId: Long): Flow<List<Litter>> =
        localRepository.observeLittersByFather(fatherId)

    fun observeKittensByLitter(litterId: Long): Flow<List<Kitten>> =
        localRepository.observeKittensByLitter(litterId)

    fun observeKitten(id: Long): Flow<Kitten?> = localRepository.observeKitten(id)

    suspend fun searchCatFemales(query: String): Result<List<CatFemale>> = runCatching {
        remoteRepository.searchCatFemales(query)
    }

    suspend fun searchCatMales(query: String): Result<List<CatMale>> = runCatching {
        remoteRepository.searchCatMales(query)
    }

    suspend fun searchLitters(query: String): Result<List<Litter>> = runCatching {
        remoteRepository.searchLitters(query)
    }

    suspend fun loadCatFemale(id: Long): Result<CatFemale> = runCatching {
        remoteRepository.fetchCatFemale(id)
    }

    suspend fun loadCatFemaleLitters(id: Long): Result<List<Litter>> = runCatching {
        remoteRepository.fetchCatFemaleLitters(id)
    }

    suspend fun loadCatMale(id: Long): Result<CatMale> = runCatching {
        remoteRepository.fetchCatMale(id)
    }

    suspend fun loadCatMaleLitters(id: Long): Result<List<Litter>> = runCatching {
        remoteRepository.fetchCatMaleLitters(id)
    }

    suspend fun loadLitter(id: Long): Result<Litter> = runCatching {
        remoteRepository.fetchLitter(id)
    }

    suspend fun loadLitterKittens(litterId: Long): Result<List<Kitten>> = runCatching {
        remoteRepository.fetchLitterKittens(litterId)
    }

    suspend fun loadKittenDetail(id: Long): Result<KittenDetail> = runCatching {
        remoteRepository.fetchKittenDetail(id)
    }
}
