package com.cattery.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cattery.domain.models.CatFemale
import com.cattery.domain.models.CatMale
import com.cattery.domain.models.Kitten
import com.cattery.domain.models.KittenDetail
import com.cattery.domain.models.KittenStatus
import com.cattery.domain.models.Litter
import com.cattery.domain.models.UserRole
import com.cattery.domain.usecases.CatalogUseCases
import com.cattery.presentation.components.petAgeSubtitle
import com.cattery.presentation.util.uiError
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogListUiState(
    val items: List<CatalogListItemData> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = true,
    val isBreeder: Boolean = false,
    val error: String? = null,
)

data class CatalogListItemData(
    val id: Long,
    val name: String,
    val subtitle: String,
    val photoUrl: String?,
)

class CatFemaleListViewModel(
    private val catalogUseCases: CatalogUseCases,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<List<CatFemale>?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)
    private var searchJob: Job? = null

    val uiState: StateFlow<CatalogListUiState> = combine(
        combine(
            catalogUseCases.observeCatFemales(),
            catalogUseCases.observeCurrentUser(),
            _query,
            _searchResults,
        ) { females, user, query, searchResults ->
            Triple(females, user, query to searchResults)
        },
        _isLoading,
        _error,
    ) { data, loading, error ->
        val (females, user, queryData) = data
        val (query, searchResults) = queryData
        val source = if (query.isBlank()) females else searchResults ?: emptyList()
        CatalogListUiState(
            items = source.map {
                CatalogListItemData(
                    id = it.id,
                    name = it.name,
                    subtitle = petAgeSubtitle(it.birthDate),
                    photoUrl = it.photoUrls.firstOrNull(),
                )
            },
            query = query,
            isLoading = loading,
            isBreeder = user?.role == UserRole.BREEDER,
            error = error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogListUiState())

    init {
        refresh()
    }

    fun onQueryChange(query: String) {
        _query.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = null
            return
        }
        searchJob = viewModelScope.launch {
            catalogUseCases.searchCatFemales(query)
                .onSuccess { _searchResults.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = catalogUseCases.refreshAll().uiError()
            _isLoading.value = false
        }
    }
}

class CatMaleListViewModel(
    private val catalogUseCases: CatalogUseCases,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<List<CatMale>?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)
    private var searchJob: Job? = null

    val uiState: StateFlow<CatalogListUiState> = combine(
        combine(
            catalogUseCases.observeCatMales(),
            catalogUseCases.observeCurrentUser(),
            _query,
            _searchResults,
        ) { males, user, query, searchResults ->
            Triple(males, user, query to searchResults)
        },
        _isLoading,
        _error,
    ) { data, loading, error ->
        val (males, user, queryData) = data
        val (query, searchResults) = queryData
        val source = if (query.isBlank()) males else searchResults ?: emptyList()
        CatalogListUiState(
            items = source.map {
                CatalogListItemData(
                    id = it.id,
                    name = it.name,
                    subtitle = petAgeSubtitle(it.birthDate),
                    photoUrl = it.photoUrls.firstOrNull(),
                )
            },
            query = query,
            isLoading = loading,
            isBreeder = user?.role == UserRole.BREEDER,
            error = error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogListUiState())

    init {
        refresh()
    }

    fun onQueryChange(query: String) {
        _query.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = null
            return
        }
        searchJob = viewModelScope.launch {
            catalogUseCases.searchCatMales(query)
                .onSuccess { _searchResults.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = catalogUseCases.refreshAll().uiError()
            _isLoading.value = false
        }
    }
}

class LitterListViewModel(
    private val catalogUseCases: CatalogUseCases,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<List<Litter>?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)
    private var searchJob: Job? = null

    val uiState: StateFlow<CatalogListUiState> = combine(
        combine(
            catalogUseCases.observeLitters(),
            catalogUseCases.observeCurrentUser(),
            _query,
            _searchResults,
        ) { litters, user, query, searchResults ->
            Triple(litters, user, query to searchResults)
        },
        _isLoading,
        _error,
    ) { data, loading, error ->
        val (litters, user, queryData) = data
        val (query, searchResults) = queryData
        val source = if (query.isBlank()) litters else searchResults ?: emptyList()
        CatalogListUiState(
            items = source.map {
                CatalogListItemData(
                    id = it.id,
                    name = it.name,
                    subtitle = petAgeSubtitle(it.birthDate),
                    photoUrl = it.photoUrls.firstOrNull(),
                )
            },
            query = query,
            isLoading = loading,
            isBreeder = user?.role == UserRole.BREEDER,
            error = error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogListUiState())

    init {
        refresh()
    }

    fun onQueryChange(query: String) {
        _query.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = null
            return
        }
        searchJob = viewModelScope.launch {
            catalogUseCases.searchLitters(query)
                .onSuccess { _searchResults.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = catalogUseCases.refreshAll().uiError()
            _isLoading.value = false
        }
    }
}

data class CatFemaleDetailUiState(
    val cat: CatFemale? = null,
    val litters: List<Litter> = emptyList(),
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val isBreeder: Boolean = false,
    val deleted: Boolean = false,
    val error: String? = null,
)

class CatFemaleDetailViewModel(
    private val catalogUseCases: CatalogUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val catId: Long = savedStateHandle.get<Long>("id") ?: 0L
    private val _isLoading = MutableStateFlow(true)
    private val _isDeleting = MutableStateFlow(false)
    private val _deleted = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CatFemaleDetailUiState> = combine(
        combine(
            catalogUseCases.observeCatFemale(catId),
            catalogUseCases.observeLittersByMother(catId),
            catalogUseCases.observeCurrentUser(),
        ) { cat, litters, user ->
            Triple(cat, litters, user)
        },
        _isLoading,
        _isDeleting,
        _deleted,
        _error,
    ) { data, loading, deleting, deleted, error ->
        val (cat, litters, user) = data
        CatFemaleDetailUiState(
            cat = cat,
            litters = litters,
            isLoading = loading,
            isDeleting = deleting,
            isBreeder = user?.role == UserRole.BREEDER,
            deleted = deleted,
            error = error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatFemaleDetailUiState())

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            catalogUseCases.loadCatFemale(catId)
                .onFailure { _error.value = it.message }
            catalogUseCases.loadCatFemaleLitters(catId)
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun delete() {
        viewModelScope.launch {
            _isDeleting.value = true
            _error.value = null
            catalogUseCases.deleteCatFemale(catId)
                .onSuccess { _deleted.value = true }
                .onFailure { _error.value = it.message }
            _isDeleting.value = false
        }
    }
}

data class CatMaleDetailUiState(
    val cat: CatMale? = null,
    val litters: List<Litter> = emptyList(),
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val isBreeder: Boolean = false,
    val deleted: Boolean = false,
    val error: String? = null,
)

class CatMaleDetailViewModel(
    private val catalogUseCases: CatalogUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val catId: Long = savedStateHandle.get<Long>("id") ?: 0L
    private val _isLoading = MutableStateFlow(true)
    private val _isDeleting = MutableStateFlow(false)
    private val _deleted = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CatMaleDetailUiState> = combine(
        combine(
            catalogUseCases.observeCatMale(catId),
            catalogUseCases.observeLittersByFather(catId),
            catalogUseCases.observeCurrentUser(),
        ) { cat, litters, user ->
            Triple(cat, litters, user)
        },
        _isLoading,
        _isDeleting,
        _deleted,
        _error,
    ) { data, loading, deleting, deleted, error ->
        val (cat, litters, user) = data
        CatMaleDetailUiState(
            cat = cat,
            litters = litters,
            isLoading = loading,
            isDeleting = deleting,
            isBreeder = user?.role == UserRole.BREEDER,
            deleted = deleted,
            error = error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatMaleDetailUiState())

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            catalogUseCases.loadCatMale(catId)
                .onFailure { _error.value = it.message }
            catalogUseCases.loadCatMaleLitters(catId)
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun delete() {
        viewModelScope.launch {
            _isDeleting.value = true
            _error.value = null
            catalogUseCases.deleteCatMale(catId)
                .onSuccess { _deleted.value = true }
                .onFailure { _error.value = it.message }
            _isDeleting.value = false
        }
    }
}

data class LitterDetailUiState(
    val litter: Litter? = null,
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val isBreeder: Boolean = false,
    val deleted: Boolean = false,
    val error: String? = null,
)

class LitterDetailViewModel(
    private val catalogUseCases: CatalogUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val litterId: Long = savedStateHandle.get<Long>("id") ?: 0L
    private val _isLoading = MutableStateFlow(true)
    private val _isDeleting = MutableStateFlow(false)
    private val _deleted = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<LitterDetailUiState> = combine(
        combine(
            catalogUseCases.observeLitter(litterId),
            catalogUseCases.observeCurrentUser(),
        ) { litter, user ->
            litter to user
        },
        _isLoading,
        _isDeleting,
        _deleted,
        _error,
    ) { data, loading, deleting, deleted, error ->
        val (litter, user) = data
        LitterDetailUiState(
            litter = litter,
            isLoading = loading,
            isDeleting = deleting,
            isBreeder = user?.role == UserRole.BREEDER,
            deleted = deleted,
            error = error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LitterDetailUiState())

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            catalogUseCases.loadLitter(litterId)
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun delete() {
        viewModelScope.launch {
            _isDeleting.value = true
            _error.value = null
            catalogUseCases.deleteLitter(litterId)
                .onSuccess { _deleted.value = true }
                .onFailure { _error.value = it.message }
            _isDeleting.value = false
        }
    }
}

class KittenListViewModel(
    private val catalogUseCases: CatalogUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val litterId: Long = savedStateHandle.get<Long>("litterId") ?: 0L
    private val _query = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CatalogListUiState> = combine(
        catalogUseCases.observeKittensByLitter(litterId),
        catalogUseCases.observeCurrentUser(),
        _query,
        _isLoading,
        _error,
    ) { kittens, user, query, loading, error ->
        val filtered = if (query.isBlank()) {
            kittens
        } else {
            kittens.filter { it.name.contains(query, ignoreCase = true) }
        }
        CatalogListUiState(
            items = filtered.map {
                CatalogListItemData(
                    id = it.id,
                    name = it.name,
                    subtitle = it.color,
                    photoUrl = it.photoUrls.firstOrNull(),
                )
            },
            query = query,
            isLoading = loading,
            isBreeder = user?.role == UserRole.BREEDER,
            error = error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogListUiState())

    init {
        load()
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun refresh() {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            catalogUseCases.loadLitterKittens(litterId)
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}

data class KittenDetailUiState(
    val detail: KittenDetail? = null,
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val isBreeder: Boolean = false,
    val isBuyer: Boolean = false,
    val canReserve: Boolean = false,
    val canCancel: Boolean = false,
    val deleted: Boolean = false,
    val error: String? = null,
)

private data class KittenDetailLocalState(
    val isLoading: Boolean,
    val isActionLoading: Boolean,
    val isDeleting: Boolean,
    val deleted: Boolean,
    val error: String?,
)

class KittenDetailViewModel(
    private val catalogUseCases: CatalogUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val kittenId: Long = savedStateHandle.get<Long>("id") ?: 0L
    private val _detail = MutableStateFlow<KittenDetail?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _isActionLoading = MutableStateFlow(false)
    private val _isDeleting = MutableStateFlow(false)
    private val _deleted = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<KittenDetailUiState> = combine(
        combine(
            _detail,
            catalogUseCases.observeCurrentUser(),
            catalogUseCases.observeReservations(),
        ) { detail, user, reservations ->
            Triple(detail, user, reservations)
        },
        combine(
            _isLoading,
            _isActionLoading,
            _isDeleting,
            _deleted,
            _error,
        ) { loading, actionLoading, deleting, deleted, error ->
            KittenDetailLocalState(loading, actionLoading, deleting, deleted, error)
        },
    ) { data, local ->
        val (detail, user, reservations) = data
        val isBreeder = user?.role == UserRole.BREEDER
        val isBuyer = user?.role == UserRole.BUYER
        val kitten = detail?.kitten
        KittenDetailUiState(
            detail = detail,
            isLoading = local.isLoading,
            isActionLoading = local.isActionLoading,
            isDeleting = local.isDeleting,
            isBreeder = isBreeder,
            isBuyer = isBuyer,
            canReserve = isBuyer && kitten?.status == KittenStatus.FREE,
            canCancel = isBuyer && reservations.any { it.kittenId == kittenId },
            deleted = local.deleted,
            error = local.error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), KittenDetailUiState())

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            catalogUseCases.loadKittenDetail(kittenId)
                .onSuccess { _detail.value = it }
                .onFailure { error -> _error.value = error.message }
            catalogUseCases.refreshReservations()
            _isLoading.value = false
        }
    }

    fun reserve() {
        viewModelScope.launch {
            _isActionLoading.value = true
            _error.value = null
            catalogUseCases.reserveKitten(kittenId)
                .onSuccess { load() }
                .onFailure { _error.value = it.message }
            _isActionLoading.value = false
        }
    }

    fun cancelReservation() {
        viewModelScope.launch {
            _isActionLoading.value = true
            _error.value = null
            catalogUseCases.cancelKittenReservation(kittenId)
                .onSuccess { load() }
                .onFailure { _error.value = it.message }
            _isActionLoading.value = false
        }
    }

    fun delete() {
        viewModelScope.launch {
            _isDeleting.value = true
            _error.value = null
            catalogUseCases.deleteKitten(kittenId)
                .onSuccess { _deleted.value = true }
                .onFailure { _error.value = it.message }
            _isDeleting.value = false
        }
    }
}
