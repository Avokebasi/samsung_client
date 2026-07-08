package com.cattery.data.local.repository

import com.cattery.data.local.database.AppDatabase
import com.cattery.data.local.database.entities.toEntity
import com.cattery.data.local.database.entities.toDomain
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

    fun observeCatMales(): Flow<List<CatMale>> =
        catMaleDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeLitters(): Flow<List<Litter>> =
        litterDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeKittensByLitter(litterId: Long): Flow<List<Kitten>> =
        kittenDao.observeByLitterId(litterId).map { list -> list.map { it.toDomain() } }

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
            if (catFemales.isNotEmpty()) catFemaleDao.upsertAll(catFemales.map { it.toEntity() })
            if (catMales.isNotEmpty()) catMaleDao.upsertAll(catMales.map { it.toEntity() })
            if (litters.isNotEmpty()) litterDao.upsertAll(litters.map { it.toEntity() })
            if (kittens.isNotEmpty()) kittenDao.upsertAll(kittens.map { it.toEntity() })
        }
    }

    suspend fun cacheReservations(items: List<ReservationDetail>) {
        database.withTransaction {
            reservationDao.clear()
            if (items.isNotEmpty()) {
                reservationDao.upsertAll(items.map { it.toEntity() })
            }
        }
    }

    suspend fun clearAll() {
        database.withTransaction {
            userDao.clear()
            catFemaleDao.clear()
            catMaleDao.clear()
            litterDao.clear()
            kittenDao.clear()
            reservationDao.clear()
        }
    }
}
