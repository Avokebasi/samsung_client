package com.cattery.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cattery.data.remote.api.SaveCatFemaleRequest
import com.cattery.data.remote.api.SaveCatMaleRequest
import com.cattery.data.remote.api.SaveKittenRequest
import com.cattery.data.remote.api.SaveLitterRequest
import com.cattery.domain.models.CatFemale
import com.cattery.domain.models.CatMale
import com.cattery.domain.models.KittenStatus
import com.cattery.domain.models.Litter
import com.cattery.domain.usecases.CatalogUseCases
import com.cattery.presentation.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EntityFormUiState(
    val entityType: String = "",
    val isEdit: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val name: String = "",
    val birthDate: String = "",
    val hadMating: Boolean = false,
    val matingDate: String = "",
    val color: String = "",
    val birthWeight: String = "",
    val totalCount: String = "",
    val maleCount: String = "",
    val femaleCount: String = "",
    val motherId: Long? = null,
    val fatherId: Long? = null,
    val litterId: Long? = null,
    val status: KittenStatus = KittenStatus.FREE,
    val photoUrls: List<String> = emptyList(),
    val catFemales: List<CatFemale> = emptyList(),
    val catMales: List<CatMale> = emptyList(),
    val litters: List<Litter> = emptyList(),
    val error: String? = null,
    val completed: Boolean = false,
)

class EntityFormViewModel(
    private val catalogUseCases: CatalogUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val entityType: String = savedStateHandle.get<String>("entityType").orEmpty()
    private val entityId: Long = savedStateHandle.get<Long>("entityId") ?: Routes.NO_ID
    private val presetLitterId: Long = savedStateHandle.get<Long>("litterId") ?: Routes.NO_ID

    private val _state = MutableStateFlow(
        EntityFormUiState(
            entityType = entityType,
            isEdit = entityId != Routes.NO_ID,
            litterId = presetLitterId.takeIf { it != Routes.NO_ID },
        ),
    )

    val uiState: StateFlow<EntityFormUiState> = combine(
        _state,
        catalogUseCases.observeCatFemales(),
        catalogUseCases.observeCatMales(),
        catalogUseCases.observeLitters(),
    ) { state, females, males, litters ->
        state.copy(
            catFemales = females,
            catMales = males,
            litters = litters,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EntityFormUiState())

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            if (entityId != Routes.NO_ID) {
                when (entityType) {
                    "cat_female" -> catalogUseCases.loadCatFemale(entityId)
                        .onSuccess { fillCatFemale(it) }
                        .onFailure { setError(it.message) }
                    "cat_male" -> catalogUseCases.loadCatMale(entityId)
                        .onSuccess { fillCatMale(it) }
                        .onFailure { setError(it.message) }
                    "litter" -> catalogUseCases.loadLitter(entityId)
                        .onSuccess { fillLitter(it) }
                        .onFailure { setError(it.message) }
                    "kitten" -> catalogUseCases.loadKittenDetail(entityId)
                        .onSuccess { fillKitten(it.kitten) }
                        .onFailure { setError(it.message) }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun fillCatFemale(cat: CatFemale) {
        _state.update {
            it.copy(
                name = cat.name,
                birthDate = cat.birthDate,
                hadMating = cat.matingDate != null,
                matingDate = cat.matingDate.orEmpty(),
                photoUrls = cat.photoUrls,
            )
        }
    }

    private fun fillCatMale(cat: CatMale) {
        _state.update {
            it.copy(
                name = cat.name,
                birthDate = cat.birthDate,
                photoUrls = cat.photoUrls,
            )
        }
    }

    private fun fillLitter(litter: Litter) {
        _state.update {
            it.copy(
                name = litter.name,
                birthDate = litter.birthDate,
                totalCount = litter.totalCount.toString(),
                maleCount = litter.maleCount.toString(),
                femaleCount = litter.femaleCount.toString(),
                motherId = litter.motherId,
                fatherId = litter.fatherId,
                photoUrls = litter.photoUrls,
            )
        }
    }

    private fun fillKitten(kitten: com.cattery.domain.models.Kitten) {
        _state.update {
            it.copy(
                name = kitten.name,
                birthDate = kitten.birthDate,
                color = kitten.color,
                birthWeight = kitten.birthWeight?.toString().orEmpty(),
                litterId = kitten.litterId,
                status = kitten.status,
                photoUrls = kitten.photoUrls,
            )
        }
    }

    private fun setError(message: String?) {
        _state.update { it.copy(error = message) }
    }

    fun updateName(value: String) = _state.update { it.copy(name = value, error = null) }
    fun updateBirthDate(value: String) = _state.update { it.copy(birthDate = value, error = null) }
    fun updateHadMating(value: Boolean) = _state.update {
        it.copy(hadMating = value, matingDate = if (value) it.matingDate else "", error = null)
    }
    fun updateMatingDate(value: String) = _state.update { it.copy(matingDate = value, error = null) }
    fun updateColor(value: String) = _state.update { it.copy(color = value, error = null) }
    fun updateBirthWeight(value: String) = _state.update { it.copy(birthWeight = value, error = null) }
    fun updateTotalCount(value: String) = _state.update { it.copy(totalCount = value, error = null) }
    fun updateMaleCount(value: String) = _state.update { it.copy(maleCount = value, error = null) }
    fun updateFemaleCount(value: String) = _state.update { it.copy(femaleCount = value, error = null) }
    fun updateMotherId(value: Long?) = _state.update { it.copy(motherId = value, error = null) }
    fun updateFatherId(value: Long?) = _state.update { it.copy(fatherId = value, error = null) }
    fun updateLitterId(value: Long?) = _state.update { it.copy(litterId = value, error = null) }
    fun updateStatus(value: KittenStatus) = _state.update { it.copy(status = value, error = null) }

    fun addPhoto(url: String) {
        _state.update { it.copy(photoUrls = it.photoUrls + url, error = null) }
    }

    fun removePhoto(url: String) {
        _state.update { it.copy(photoUrls = it.photoUrls.filterNot { photo -> photo == url }) }
    }

    fun save() {
        val state = _state.value
        if (state.name.isBlank() || state.birthDate.isBlank()) {
            setError("Заполните кличку и дату рождения")
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val id = entityId.takeIf { it != Routes.NO_ID }
            val result = when (entityType) {
                "cat_female" -> catalogUseCases.saveCatFemale(
                    id,
                    SaveCatFemaleRequest(
                        name = state.name.trim(),
                        birthDate = state.birthDate.trim(),
                        matingDate = if (state.hadMating) state.matingDate.trim().ifBlank { null } else null,
                        photoUrls = state.photoUrls,
                    ),
                )
                "cat_male" -> catalogUseCases.saveCatMale(
                    id,
                    SaveCatMaleRequest(
                        name = state.name.trim(),
                        birthDate = state.birthDate.trim(),
                        photoUrls = state.photoUrls,
                    ),
                )
                "litter" -> {
                    val total = state.totalCount.toIntOrNull()
                    val males = state.maleCount.toIntOrNull()
                    val females = state.femaleCount.toIntOrNull()
                    if (total == null || males == null || females == null) {
                        setError("Укажите корректные числа в помёте")
                        _state.update { it.copy(isSaving = false) }
                        return@launch
                    }
                    catalogUseCases.saveLitter(
                        id,
                        SaveLitterRequest(
                            name = state.name.trim(),
                            birthDate = state.birthDate.trim(),
                            totalCount = total,
                            maleCount = males,
                            femaleCount = females,
                            motherId = state.motherId,
                            fatherId = state.fatherId,
                            photoUrls = state.photoUrls,
                        ),
                    )
                }
                "kitten" -> {
                    val litterId = state.litterId
                    if (litterId == null) {
                        setError("Выберите помёт")
                        _state.update { it.copy(isSaving = false) }
                        return@launch
                    }
                    if (state.color.isBlank()) {
                        setError("Укажите окрас")
                        _state.update { it.copy(isSaving = false) }
                        return@launch
                    }
                    catalogUseCases.saveKitten(
                        id,
                        SaveKittenRequest(
                            litterId = litterId,
                            name = state.name.trim(),
                            birthDate = state.birthDate.trim(),
                            color = state.color.trim(),
                            birthWeight = state.birthWeight.trim().toDoubleOrNull(),
                            status = state.status,
                            photoUrls = state.photoUrls,
                        ),
                    )
                }
                else -> Result.failure(IllegalArgumentException("Неизвестный тип"))
            }
            result
                .onSuccess { _state.update { it.copy(isSaving = false, completed = true) } }
                .onFailure { error ->
                    _state.update { it.copy(isSaving = false, error = error.message) }
                }
        }
    }

    fun delete() {
        if (entityId == Routes.NO_ID) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val result = when (entityType) {
                "cat_female" -> catalogUseCases.deleteCatFemale(entityId)
                "cat_male" -> catalogUseCases.deleteCatMale(entityId)
                "litter" -> catalogUseCases.deleteLitter(entityId)
                "kitten" -> catalogUseCases.deleteKitten(entityId)
                else -> Result.failure(IllegalArgumentException("Неизвестный тип"))
            }
            result
                .onSuccess { _state.update { it.copy(isSaving = false, completed = true) } }
                .onFailure { error ->
                    _state.update { it.copy(isSaving = false, error = error.message) }
                }
        }
    }
}
