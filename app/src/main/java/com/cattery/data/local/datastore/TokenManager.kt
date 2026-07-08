package com.cattery.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

private val Context.secureTokenStore: DataStore<Preferences> by preferencesDataStore(
    name = "encrypted_token_store",
)

class TokenManager(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedBackupFile: EncryptedFile = EncryptedFile.Builder(
        context,
        File(context.filesDir, "token_backup.enc"),
        masterKey,
        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
    ).build()

    private val dataStore = context.secureTokenStore
    private val mutex = Mutex()

    private val _tokenState = MutableStateFlow<String?>(null)
    val tokenFlow: Flow<String?> = _tokenState.asStateFlow()

    suspend fun init() {
        val token = dataStore.data.map { it[KEY_ACCESS_TOKEN] }.first()
        _tokenState.value = token ?: readEncryptedBackup()
    }

    suspend fun saveToken(token: String) {
        mutex.withLock {
            dataStore.edit { it[KEY_ACCESS_TOKEN] = token }
            writeEncryptedBackup(token)
            _tokenState.value = token
        }
    }

    suspend fun clearToken() {
        mutex.withLock {
            dataStore.edit { it.remove(KEY_ACCESS_TOKEN) }
            deleteEncryptedBackup()
            _tokenState.value = null
        }
    }

    suspend fun getToken(): String? = _tokenState.value ?: dataStore.data.map { it[KEY_ACCESS_TOKEN] }.first()

    fun getTokenSync(): String? = _tokenState.value

    private fun writeEncryptedBackup(token: String) {
        runCatching {
            encryptedBackupFile.openFileOutput().use { it.write(token.toByteArray()) }
        }
    }

    private fun readEncryptedBackup(): String? = runCatching {
        if (!File(context.filesDir, "token_backup.enc").exists()) return null
        encryptedBackupFile.openFileInput().use { String(it.readBytes()) }
    }.getOrNull()

    private fun deleteEncryptedBackup() {
        runCatching { File(context.filesDir, "token_backup.enc").delete() }
    }

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
    }
}
