package com.cattery.data.local.repository

import com.cattery.data.local.database.AppDatabase
import com.cattery.data.local.database.entities.toEntity
import com.cattery.data.local.database.entities.toDomain
import com.cattery.data.local.images.LocalPhotoStore
import com.cattery.domain.models.CatFemale
import com.cattery.domain.models.CatMale
import com.cattery.domain.models.Kitten
import com.cattery.domain.models.Litter
import com.cattery.domain.models.ReservationDetail
import com.cattery.domain.models.User
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalRepository(
    private val database: AppDatabase,
    private val photoStore: LocalPhotoStore,
) {
    private val userDao = database.userDao()
    private val catFemaleDao = database.catFemaleDao()
    private val catMaleDao = database.catMaleDao()
    private val litterDao = database.litterDao()
    private val kittenDao = database.kittenDao()
    private val reservationDao = database.reservationDao()

    fun observeCurrentUser(): Flow<User?> =
        userDao.observeCurrentUser().map { it?.toDomain() }

    fun observeCatFemales(): Flow<List<CatFemale>> =
        catFemaleDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeCatFemale(id: Long): Flow<CatFemale?> =
        catFemaleDao.observeById(id).map { it?.toDomain() }

    fun observeCatMales(): Flow<List<CatMale>> =
        catMaleDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeCatMale(id: Long): Flow<CatMale?> =
        catMaleDao.observeById(id).map { it?.toDomain() }

    fun observeLitters(): Flow<List<Litter>> =
        litterDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeLitter(id: Long): Flow<Litter?> =
        litterDao.observeById(id).map { it?.toDomain() }

    fun observeLittersByMother(motherId: Long): Flow<List<Litter>> =
        litterDao.observeByMotherId(motherId).map { list -> list.map { it.toDomain() } }

    fun observeLittersByFather(fatherId: Long): Flow<List<Litter>> =
        litterDao.observeByFatherId(fatherId).map { list -> list.map { it.toDomain() } }

    fun observeKittensByLitter(litterId: Long): Flow<List<Kitten>> =
        kittenDao.observeByLitterId(litterId).map { list -> list.map { it.toDomain() } }

    fun observeKitten(id: Long): Flow<Kitten?> =
        kittenDao.observeById(id).map { it?.toDomain() }

    fun observeReservations(): Flow<List<ReservationDetail>> =
        reservationDao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun cacheUser(user: User) {
        userDao.upsert(user.toEntity())
    }

    suspend fun clearUser() {
        userDao.clear()
    }

    suspend fun syncCatalog(
        catFemales: List<CatFemale>,
        catMales: List<CatMale>,
        litters: List<Litter>,
        kittens: List<Kitten>,
    ) {
        database.withTransaction {
            catFemaleDao.clear()
            catMaleDao.clear()
            litterDao.clear()
            kittenDao.clear()
            if (catFemales.isNotEmpty()) {
                catFemaleDao.upsertAll(catFemales.map { it.withLocalPhotos().toEntity() })
            }
            if (catMales.isNotEmpty()) {
                catMaleDao.upsertAll(catMales.map { it.withLocalPhotos().toEntity() })
            }
            if (litters.isNotEmpty()) {
                litterDao.upsertAll(litters.map { it.withLocalPhotos().toEntity() })
            }
            if (kittens.isNotEmpty()) {
                kittenDao.upsertAll(kittens.map { it.withLocalPhotos().toEntity() })
            }
        }
    }

    suspend fun cacheReservations(items: List<ReservationDetail>) {
        database.withTransaction {
            reservationDao.clear()
            if (items.isNotEmpty()) {
                reservationDao.upsertAll(items.map { it.withLocalPhotos().toEntity() })
            }
        }
    }

    suspend fun upsertCatFemale(item: CatFemale) {
        catFemaleDao.upsertAll(listOf(item.withLocalPhotos().toEntity()))
    }

    suspend fun upsertCatMale(item: CatMale) {
        catMaleDao.upsertAll(listOf(item.withLocalPhotos().toEntity()))
    }

    suspend fun upsertLitter(item: Litter) {
        litterDao.upsertAll(listOf(item.withLocalPhotos().toEntity()))
    }

    suspend fun upsertLitters(items: List<Litter>) {
        if (items.isNotEmpty()) litterDao.upsertAll(items.map { it.withLocalPhotos().toEntity() })
    }

    suspend fun upsertKitten(item: Kitten) {
        kittenDao.upsertAll(listOf(item.withLocalPhotos().toEntity()))
    }

    suspend fun upsertKittens(items: List<Kitten>) {
        if (items.isNotEmpty()) kittenDao.upsertAll(items.map { it.withLocalPhotos().toEntity() })
    }

    suspend fun hasCachedUser(): Boolean = userDao.getCurrentUser() != null

    suspend fun hasCatalogCache(): Boolean =
        catFemaleDao.count() + catMaleDao.count() + litterDao.count() + kittenDao.count() > 0

    suspend fun hasReservationCache(): Boolean = reservationDao.count() > 0

    suspend fun clearAll() {
        database.withTransaction {
            userDao.clear()
            catFemaleDao.clear()
            catMaleDao.clear()
            litterDao.clear()
            kittenDao.clear()
            reservationDao.clear()
        }
        photoStore.clearAll()
    }

    private fun CatFemale.withLocalPhotos(): CatFemale =
        copy(photoUrls = photoStore.persistRemotePhotos(photoUrls))

    private fun CatMale.withLocalPhotos(): CatMale =
        copy(photoUrls = photoStore.persistRemotePhotos(photoUrls))

    private fun Litter.withLocalPhotos(): Litter =
        copy(photoUrls = photoStore.persistRemotePhotos(photoUrls))

    private fun Kitten.withLocalPhotos(): Kitten =
        copy(photoUrls = photoStore.persistRemotePhotos(photoUrls))

    private fun ReservationDetail.withLocalPhotos(): ReservationDetail =
        copy(kittenPhotoUrls = photoStore.persistRemotePhotos(kittenPhotoUrls))
}
