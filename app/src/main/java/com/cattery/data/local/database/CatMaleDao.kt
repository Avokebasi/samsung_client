package com.cattery.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cattery.data.local.database.entities.CatMaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatMaleDao {
    @Query("SELECT * FROM cat_males ORDER BY name ASC")
    fun observeAll(): Flow<List<CatMaleEntity>>

    @Query("SELECT * FROM cat_males WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<CatMaleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CatMaleEntity>)

    @Query("SELECT COUNT(*) FROM cat_males")
    suspend fun count(): Int

    @Query("DELETE FROM cat_males")
    suspend fun clear()
}
