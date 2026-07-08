package com.cattery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cattery.domain.models.ReservationDetail
import com.cattery.domain.models.UserRole
import com.cattery.domain.usecases.CatalogUseCases
import com.cattery.presentation.util.uiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ReservationsUiState(
    val items: List<ReservationDetail> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = true,
    val isBreeder: Boolean = false,
    val error: String? = null,
)

class ReservationsViewModel(
    private val catalogUseCases: CatalogUseCases,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ReservationsUiState> = combine(
        catalogUseCases.observeReservations(),
        catalogUseCases.observeCurrentUser(),
        _query,
        _isLoading,
        _error,
    ) { reservations, user, query, loading, error ->
        val filtered = if (query.isBlank()) {
            reservations
        } else {
            reservations.filter {
                it.kittenName.contains(query, ignoreCase = true) ||
                    it.litterName.contains(query, ignoreCase = true) ||
                    it.buyerName.contains(query, ignoreCase = true)
            }
        }
        ReservationsUiState(
            items = filtered,
            query = query,
            isLoading = loading,
            isBreeder = user?.role == UserRole.BREEDER,
            error = error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReservationsUiState())

    init {
        refresh()
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = catalogUseCases.refreshReservations().uiError()
            _isLoading.value = false
        }
    }
}
