package com.cattery.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cattery.domain.models.CatFemale
import com.cattery.domain.models.CatMale
import com.cattery.domain.models.Litter
import com.cattery.domain.models.User
import com.cattery.domain.models.UserRole
import com.cattery.domain.usecases.CatalogUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val user: User? = null,
    val localAvatarUri: String? = null,
    val catFemales: List<CatFemale> = emptyList(),
    val catMales: List<CatMale> = emptyList(),
    val litters: List<Litter> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val isBreeder: Boolean get() = user?.role == UserRole.BREEDER

    val displayAvatar: String?
        get() = localAvatarUri ?: user?.avatarUrl
}

class HomeViewModel(
    private val catalogUseCases: CatalogUseCases,
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
        _localAvatarUri,
        _isLoading,
        _error,
    ) { state, localAvatar, loading, error ->
        state.copy(
            localAvatarUri = localAvatar,
            isLoading = loading,
            error = error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            catalogUseCases.refreshAll()
                .onFailure { _error.value = it.message }
                .onSuccess { _error.value = null }
            _isLoading.value = false
        }
    }

    fun updateAvatar(uri: Uri) {
        val uriString = uri.toString()
        _localAvatarUri.value = uriString
        viewModelScope.launch {
            catalogUseCases.updateAvatar(uriString)
                .onFailure { _error.value = it.message }
        }
    }
}
