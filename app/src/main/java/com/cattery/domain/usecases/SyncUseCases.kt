package com.cattery.domain.usecases

import com.cattery.data.sync.SyncManager
import com.cattery.data.sync.SyncState
import com.cattery.domain.models.SyncOutcome
import kotlinx.coroutines.flow.StateFlow

class SyncUseCases(
    private val syncManager: SyncManager,
) {
    val syncState: StateFlow<SyncState> = syncManager.syncState

    suspend fun syncAll(): SyncOutcome = syncManager.syncAll()

    suspend fun syncReservations(): SyncOutcome = syncManager.syncReservations()

    fun isCurrentlyOnline(): Boolean = syncManager.isCurrentlyOnline()
}
