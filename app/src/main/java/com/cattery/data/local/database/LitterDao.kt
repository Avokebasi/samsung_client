package com.cattery.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cattery.data.local.database.entities.LitterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LitterDao {
    @Query("SELECT * FROM litters ORDER BY birthDate DESC")
    fun observeAll(): Flow<List<LitterEntity>>

    @Query("SELECT * FROM litters WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<LitterEntity?>

    @Query("SELECT * FROM litters WHERE motherId = :motherId ORDER BY birthDate DESC")
    fun observeByMotherId(motherId: Long): Flow<List<LitterEntity>>

    @Query("SELECT * FROM litters WHERE fatherId = :fatherId ORDER BY birthDate DESC")
    fun observeByFatherId(fatherId: Long): Flow<List<LitterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<LitterEntity>)

    @Query("DELETE FROM litters")
    suspend fun clear()
}
