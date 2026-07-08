package com.cattery.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cattery.data.local.database.entities.ReservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {
    @Query("SELECT * FROM reservations ORDER BY reservedAt DESC")
    fun observeAll(): Flow<List<ReservationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ReservationEntity>)

    @Query("SELECT COUNT(*) FROM reservations")
    suspend fun count(): Int

    @Query("DELETE FROM reservations")
    suspend fun clear()
}
