package com.cattery.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cattery.domain.models.CatFemale
import com.cattery.domain.models.CatMale
import com.cattery.domain.models.Litter
import com.cattery.domain.models.User
import com.cattery.domain.models.UserRole
import com.cattery.data.local.images.ImageDataUrlEncoder
import com.cattery.domain.usecases.AuthUseCases
import com.cattery.domain.usecases.CatalogUseCases
import com.cattery.domain.usecases.SyncUseCases
import com.cattery.presentation.util.uiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeUiState(
    val user: User? = null,
    val localAvatarUri: String? = null,
    val catFemales: List<CatFemale> = emptyList(),
    val catMales: List<CatMale> = emptyList(),
    val litters: List<Litter> = emptyList(),
    val isLoading: Boolean = true,
    val isOnline: Boolean = true,
    val isSyncing: Boolean = false,
    val lastSyncMillis: Long = 0L,
    val error: String? = null,
) {
    val isBreeder: Boolean get() = user?.role == UserRole.BREEDER

    val displayAvatar: String?
        get() = localAvatarUri ?: user?.avatarUrl
}

class HomeViewModel(
    private val catalogUseCases: CatalogUseCases,
    syncUseCases: SyncUseCases,
    private val authUseCases: AuthUseCases,
    private val imageDataUrlEncoder: ImageDataUrlEncoder,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)
    private val _localAvatarUri = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        combine(
            catalogUseCases.observeCurrentUser(),
            catalogUseCases.observeCatFemales(),
            catalogUseCases.observeCatMales(),
            catalogUseCases.observeLitters(),
        ) { user, females, males, litters ->
            HomeUiState(
                user = user,
                catFemales = females,
                catMales = males,
                litters = litters,
            )
        },
        syncUseCases.syncState,
        _localAvatarUri,
        _isLoading,
        _error,
    ) { state, syncState, localAvatar, loading, error ->
        state.copy(
            localAvatarUri = localAvatar,
            isLoading = loading,
            isOnline = syncState.isOnline,
            isSyncing = syncState.isSyncing,
            lastSyncMillis = syncState.lastSyncMillis,
            error = error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = catalogUseCases.refreshAll().uiError()
            _isLoading.value = false
        }
    }

    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            val dataUrl = withContext(Dispatchers.IO) {
                imageDataUrlEncoder.encode(uri)
            }
            if (dataUrl == null) {
                _error.value = "Не удалось обработать фото"
                return@launch
            }
            _localAvatarUri.value = dataUrl
            catalogUseCases.updateAvatar(dataUrl)
                .onSuccess { user ->
                    _localAvatarUri.value = if (user.avatarUrl.isNullOrBlank()) dataUrl else null
                }
                .onFailure {
                    _localAvatarUri.value = null
                    _error.value = it.message
                }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authUseCases.logout()
            onLoggedOut()
        }
    }
}
