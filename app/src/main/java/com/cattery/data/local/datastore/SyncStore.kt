package com.cattery.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.syncDataStore: DataStore<Preferences> by preferencesDataStore(name = "sync_store")

class SyncStore(context: Context) {
    private val dataStore = context.syncDataStore

    val lastSyncMillis: Flow<Long> = dataStore.data.map { prefs ->
        prefs[LAST_SYNC_MILLIS] ?: 0L
    }

    suspend fun setLastSyncMillis(value: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_SYNC_MILLIS] = value
        }
    }

    companion object {
        private val LAST_SYNC_MILLIS = longPreferencesKey("last_sync_millis")
    }
}
