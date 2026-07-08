package com.cattery.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cattery.data.local.database.entities.CatFemaleEntity
import com.cattery.data.local.database.entities.CatMaleEntity
import com.cattery.data.local.database.entities.KittenEntity
import com.cattery.data.local.database.entities.LitterEntity
import com.cattery.data.local.database.entities.ReservationEntity
import com.cattery.data.local.database.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CatFemaleEntity::class,
        CatMaleEntity::class,
        LitterEntity::class,
        KittenEntity::class,
        ReservationEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(CatteryConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun catFemaleDao(): CatFemaleDao
    abstract fun catMaleDao(): CatMaleDao
    abstract fun litterDao(): LitterDao
    abstract fun kittenDao(): KittenDao
    abstract fun reservationDao(): ReservationDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "cattery.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
