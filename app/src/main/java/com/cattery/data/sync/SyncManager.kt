package com.cattery.data.sync

import com.cattery.data.local.datastore.SyncStore
import com.cattery.data.local.network.NetworkMonitor
import com.cattery.data.local.repository.LocalRepository
import com.cattery.data.remote.repository.RemoteRepository
import com.cattery.domain.models.SyncOutcome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class SyncState(
    val isOnline: Boolean = true,
    val isSyncing: Boolean = false,
    val lastSyncMillis: Long = 0L,
)

class SyncManager(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val networkMonitor: NetworkMonitor,
    private val syncStore: SyncStore,
    private val applicationScope: CoroutineScope,
) {
    private val syncMutex = Mutex()
    private val _isSyncing = MutableStateFlow(false)

    val syncState: StateFlow<SyncState> = combine(
        networkMonitor.isOnline,
        syncStore.lastSyncMillis,
        _isSyncing,
    ) { isOnline, lastSyncMillis, isSyncing ->
        SyncState(
            isOnline = isOnline,
            isSyncing = isSyncing,
            lastSyncMillis = lastSyncMillis,
        )
    }.stateIn(applicationScope, SharingStarted.Eagerly, SyncState())

    init {
        networkMonitor.start()
        var wasOnline = networkMonitor.isCurrentlyOnline()
        applicationScope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online && !wasOnline) {
                    syncAll()
                }
                wasOnline = online
            }
        }
    }

    suspend fun syncAll(): SyncOutcome {
        if (!networkMonitor.isCurrentlyOnline()) {
            return SyncOutcome.Offline
        }
        return syncMutex.withLock {
            _isSyncing.value = true
            try {
                remoteRepository.refreshCatalog()
                remoteRepository.refreshReservations()
                val now = System.currentTimeMillis()
                syncStore.setLastSyncMillis(now)
                SyncOutcome.Success
            } catch (e: Exception) {
                SyncOutcome.Failed(
                    message = e.message ?: "Ошибка синхронизации",
                    hasCache = localRepository.hasCatalogCache(),
                )
            } finally {
                _isSyncing.value = false
            }
        }
    }

    suspend fun syncCatalog(): SyncOutcome {
        if (!networkMonitor.isCurrentlyOnline()) {
            return SyncOutcome.Offline
        }
        return syncMutex.withLock {
            _isSyncing.value = true
            try {
                remoteRepository.refreshCatalog()
                syncStore.setLastSyncMillis(System.currentTimeMillis())
                SyncOutcome.Success
            } catch (e: Exception) {
                SyncOutcome.Failed(
                    message = e.message ?: "Ошибка синхронизации",
                    hasCache = localRepository.hasCatalogCache(),
                )
            } finally {
                _isSyncing.value = false
            }
        }
    }

    suspend fun syncReservations(): SyncOutcome {
        if (!networkMonitor.isCurrentlyOnline()) {
            return SyncOutcome.Offline
        }
        return syncMutex.withLock {
            _isSyncing.value = true
            try {
                remoteRepository.refreshReservations()
                syncStore.setLastSyncMillis(System.currentTimeMillis())
                SyncOutcome.Success
            } catch (e: Exception) {
                SyncOutcome.Failed(
                    message = e.message ?: "Ошибка синхронизации",
                    hasCache = localRepository.hasReservationCache(),
                )
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun isCurrentlyOnline(): Boolean = networkMonitor.isCurrentlyOnline()
}
