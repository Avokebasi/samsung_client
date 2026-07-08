package com.cattery.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cattery.data.local.database.entities.CatFemaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatFemaleDao {
    @Query("SELECT * FROM cat_females ORDER BY name ASC")
    fun observeAll(): Flow<List<CatFemaleEntity>>

    @Query("SELECT * FROM cat_females WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<CatFemaleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CatFemaleEntity>)

    @Query("SELECT COUNT(*) FROM cat_females")
    suspend fun count(): Int

    @Query("DELETE FROM cat_females")
    suspend fun clear()
}
