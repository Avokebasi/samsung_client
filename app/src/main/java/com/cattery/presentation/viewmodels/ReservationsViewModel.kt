package com.cattery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cattery.domain.models.ReservationDetail
import com.cattery.domain.usecases.CatalogUseCases
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
        _query,
        _isLoading,
        _error,
    ) { reservations, query, loading, error ->
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
            catalogUseCases.refreshReservations()
                .onFailure { _error.value = it.message }
                .onSuccess { _error.value = null }
            _isLoading.value = false
        }
    }
}
