package com.cattery.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cattery.data.local.database.entities.KittenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KittenDao {
    @Query("SELECT * FROM kittens WHERE litterId = :litterId ORDER BY name ASC")
    fun observeByLitterId(litterId: Long): Flow<List<KittenEntity>>

    @Query("SELECT * FROM kittens WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<KittenEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<KittenEntity>)

    @Query("DELETE FROM kittens")
    suspend fun clear()
}
