package com.cattery.presentation.screens.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cattery.R
import com.cattery.domain.models.KittenStatus
import com.cattery.presentation.components.CatalogDetailScaffold
import com.cattery.presentation.components.CatalogListItem
import com.cattery.presentation.components.DetailField
import com.cattery.presentation.components.DetailSectionTitle
import com.cattery.presentation.components.PhotoGalleryRow
import com.cattery.presentation.components.petAgeSubtitle
import com.cattery.presentation.theme.BluePrimary
import com.cattery.presentation.util.DateFormatter
import com.cattery.presentation.viewmodels.CatFemaleDetailViewModel
import com.cattery.presentation.viewmodels.CatMaleDetailViewModel
import com.cattery.presentation.viewmodels.KittenDetailViewModel
import com.cattery.presentation.viewmodels.LitterDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CatFemaleDetailScreen(
    onBack: () -> Unit,
    onLitterClick: (Long) -> Unit,
    onEdit: () -> Unit,
    viewModel: CatFemaleDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cat = uiState.cat

    CatalogDetailScaffold(
        title = cat?.name ?: stringResource(R.string.section_cat_females),
        onBack = onBack,
        showEdit = uiState.isBreeder,
        editLabel = stringResource(R.string.edit),
        onEdit = onEdit,
    ) { padding ->
        DetailBody(
            isLoading = uiState.isLoading && cat == null,
            error = uiState.error,
            modifier = Modifier.padding(padding),
        ) {
            cat?.let { female ->
                PhotoGalleryRow(photoUrls = female.photoUrls)
                DetailField(
                    label = stringResource(R.string.field_name),
                    value = female.name,
                )
                DetailField(
                    label = stringResource(R.string.field_birth_date),
                    value = DateFormatter.formatDisplay(female.birthDate),
                )
                DetailField(
                    label = stringResource(R.string.field_age),
                    value = petAgeSubtitle(female.birthDate),
                )
                female.matingDate?.let { matingDate ->
                    DetailField(
                        label = stringResource(R.string.field_mating_date),
                        value = DateFormatter.formatDisplay(matingDate),
                    )
                }
                female.birthDueDate?.let { dueDate ->
                    DetailField(
                        label = stringResource(R.string.field_birth_due_date),
                        value = DateFormatter.formatDisplay(dueDate),
                    )
                }
                if (uiState.litters.isNotEmpty()) {
                    DetailSectionTitle(title = stringResource(R.string.section_litters))
                    uiState.litters.forEach { litter ->
                        CatalogListItem(
                            name = litter.name,
                            subtitle = DateFormatter.formatDisplay(litter.birthDate),
                            photoUrl = litter.photoUrls.firstOrNull(),
                            onClick = { onLitterClick(litter.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CatMaleDetailScreen(
    onBack: () -> Unit,
    onLitterClick: (Long) -> Unit,
    onEdit: () -> Unit,
    viewModel: CatMaleDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cat = uiState.cat

    CatalogDetailScaffold(
        title = cat?.name ?: stringResource(R.string.section_cat_males),
        onBack = onBack,
        showEdit = uiState.isBreeder,
        editLabel = stringResource(R.string.edit),
        onEdit = onEdit,
    ) { padding ->
        DetailBody(
            isLoading = uiState.isLoading && cat == null,
            error = uiState.error,
            modifier = Modifier.padding(padding),
        ) {
            cat?.let { male ->
                PhotoGalleryRow(photoUrls = male.photoUrls)
                DetailField(
                    label = stringResource(R.string.field_name),
                    value = male.name,
                )
                DetailField(
                    label = stringResource(R.string.field_birth_date),
                    value = DateFormatter.formatDisplay(male.birthDate),
                )
                DetailField(
                    label = stringResource(R.string.field_age),
                    value = petAgeSubtitle(male.birthDate),
                )
                if (uiState.litters.isNotEmpty()) {
                    DetailSectionTitle(title = stringResource(R.string.section_litters))
                    uiState.litters.forEach { litter ->
                        CatalogListItem(
                            name = litter.name,
                            subtitle = DateFormatter.formatDisplay(litter.birthDate),
                            photoUrl = litter.photoUrls.firstOrNull(),
                            onClick = { onLitterClick(litter.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LitterDetailScreen(
    onBack: () -> Unit,
    onKittensClick: () -> Unit,
    onEdit: () -> Unit,
    viewModel: LitterDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val litter = uiState.litter

    CatalogDetailScaffold(
        title = litter?.name ?: stringResource(R.string.section_litters),
        onBack = onBack,
        showEdit = uiState.isBreeder,
        editLabel = stringResource(R.string.edit),
        onEdit = onEdit,
        secondaryAction = stringResource(R.string.kittens) to onKittensClick,
    ) { padding ->
        DetailBody(
            isLoading = uiState.isLoading && litter == null,
            error = uiState.error,
            modifier = Modifier.padding(padding),
        ) {
            litter?.let { item ->
                PhotoGalleryRow(photoUrls = item.photoUrls)
                DetailField(
                    label = stringResource(R.string.field_name),
                    value = item.name,
                )
                DetailField(
                    label = stringResource(R.string.field_birth_date),
                    value = DateFormatter.formatDisplay(item.birthDate),
                )
                DetailField(
                    label = stringResource(R.string.field_total_count),
                    value = item.totalCount.toString(),
                )
                DetailField(
                    label = stringResource(R.string.field_male_count),
                    value = item.maleCount.toString(),
                )
                DetailField(
                    label = stringResource(R.string.field_female_count),
                    value = item.femaleCount.toString(),
                )
            }
        }
    }
}

@Composable
fun KittenDetailScreen(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    viewModel: KittenDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val detail = uiState.detail
    val kitten = detail?.kitten
    val buyerAction = when {
        uiState.canReserve -> stringResource(R.string.reserve) to viewModel::reserve
        uiState.canCancel -> stringResource(R.string.cancel_reservation) to viewModel::cancelReservation
        else -> null
    }

    CatalogDetailScaffold(
        title = kitten?.name ?: stringResource(R.string.kitten_detail),
        onBack = onBack,
        showEdit = uiState.isBreeder,
        editLabel = stringResource(R.string.edit),
        onEdit = onEdit,
        primaryAction = buyerAction,
        primaryActionEnabled = !uiState.isActionLoading,
    ) { padding ->
        DetailBody(
            isLoading = uiState.isLoading && kitten == null,
            error = uiState.error,
            modifier = Modifier.padding(padding),
        ) {
            kitten?.let { item ->
                PhotoGalleryRow(photoUrls = item.photoUrls)
                DetailField(
                    label = stringResource(R.string.field_name),
                    value = item.name,
                )
                DetailField(
                    label = stringResource(R.string.field_birth_date),
                    value = DateFormatter.formatDisplay(item.birthDate),
                )
                DetailField(
                    label = stringResource(R.string.field_age),
                    value = petAgeSubtitle(item.birthDate),
                )
                DetailField(
                    label = stringResource(R.string.field_color),
                    value = item.color,
                )
                item.birthWeight?.let { weight ->
                    DetailField(
                        label = stringResource(R.string.field_birth_weight),
                        value = "$weight ${stringResource(R.string.weight_unit)}",
                    )
                }
                DetailField(
                    label = stringResource(R.string.field_status),
                    value = when (item.status) {
                        KittenStatus.FREE -> stringResource(R.string.status_free)
                        KittenStatus.RESERVED -> stringResource(R.string.status_reserved)
                    },
                )
                detail?.let { info ->
                    DetailField(
                        label = stringResource(R.string.field_litter),
                        value = info.litterName,
                    )
                    info.motherName?.let { mother ->
                        DetailField(
                            label = stringResource(R.string.field_mother),
                            value = mother,
                        )
                    }
                    info.fatherName?.let { father ->
                        DetailField(
                            label = stringResource(R.string.field_father),
                            value = father,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailBody(
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    when {
        isLoading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(color = BluePrimary)
            }
        }
        else -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                content()
                error?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    )
                }
            }
        }
    }
}
