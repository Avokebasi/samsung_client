package com.cattery.domain.models

sealed interface SyncOutcome {
    data object Success : SyncOutcome
    data object Offline : SyncOutcome
    data class Failed(val message: String, val hasCache: Boolean) : SyncOutcome
}
