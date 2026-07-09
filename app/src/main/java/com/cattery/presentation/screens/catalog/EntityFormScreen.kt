package com.cattery.presentation.screens.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cattery.R
import com.cattery.domain.models.KittenStatus
import com.cattery.presentation.components.AvatarPickerDialog
import com.cattery.presentation.components.DeleteConfirmDialog
import com.cattery.presentation.components.FormDateField
import com.cattery.presentation.components.FormTextField
import com.cattery.presentation.components.PetPhoto
import com.cattery.presentation.theme.BluePrimary
import com.cattery.presentation.theme.TextPrimary
import com.cattery.presentation.theme.WhiteBackground
import com.cattery.presentation.util.rememberImageCaptureHandlers
import com.cattery.presentation.viewmodels.EntityFormViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityFormScreen(
    title: String,
    onBack: () -> Unit,
    onCompleted: () -> Unit,
    allowTypeSwitch: Boolean = false,
    onTypeSwitch: (String) -> Unit = {},
    viewModel: EntityFormViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPhotoPicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val imageHandlers = rememberImageCaptureHandlers { uri ->
        viewModel.addPhoto(uri)
    }

    LaunchedEffect(uiState.completed) {
        if (uiState.completed) onCompleted()
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.delete()
            },
            onDismiss = { showDeleteDialog = false },
        )
    }

    if (showPhotoPicker) {
        AvatarPickerDialog(
            onDismiss = { showPhotoPicker = false },
            onGallery = imageHandlers.launchGallery,
            onCamera = imageHandlers.launchCamera,
        )
    }

    Scaffold(
        containerColor = WhiteBackground,
        topBar = {
            TopAppBar(
                title = { Text(title, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = BluePrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhiteBackground),
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = viewModel::save,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        contentColor = WhiteBackground,
                    ),
                ) {
                    Text(if (uiState.isEdit) stringResource(R.string.save) else stringResource(R.string.add))
                }
                if (uiState.isEdit) {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        enabled = !uiState.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                }
            }
        },
    ) { padding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (allowTypeSwitch) {
                        FormTypeTabs(
                            selectedType = uiState.entityType,
                            onTypeSelected = onTypeSwitch,
                        )
                    }
                    FormTextField(
                        value = uiState.name,
                        onValueChange = viewModel::updateName,
                        label = stringResource(
                            if (uiState.entityType == "litter") {
                                R.string.field_litter_name
                            } else {
                                R.string.field_name
                            },
                        ),
                    )
                    FormDateField(
                        isoValue = uiState.birthDate,
                        onIsoValueChange = viewModel::updateBirthDate,
                        label = stringResource(R.string.field_birth_date),
                    )
                    if (uiState.entityType == "cat_female" || uiState.entityType == "cat_male") {
                        FormTextField(
                            value = uiState.color,
                            onValueChange = viewModel::updateColor,
                            label = stringResource(R.string.field_color),
                        )
                    }
                    when (uiState.entityType) {
                        "cat_female" -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = stringResource(R.string.had_mating),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary,
                                )
                                Switch(
                                    checked = uiState.hadMating,
                                    onCheckedChange = viewModel::updateHadMating,
                                )
                            }
                            if (uiState.hadMating) {
                                FormDateField(
                                    isoValue = uiState.matingDate,
                                    onIsoValueChange = viewModel::updateMatingDate,
                                    label = stringResource(R.string.field_mating_date),
                                )
                            }
                        }
                        "litter" -> {
                            FormTextField(
                                value = uiState.totalCount,
                                onValueChange = viewModel::updateTotalCount,
                                label = stringResource(R.string.field_total_count),
                            )
                            FormTextField(
                                value = uiState.maleCount,
                                onValueChange = viewModel::updateMaleCount,
                                label = stringResource(R.string.field_male_count),
                            )
                            FormTextField(
                                value = uiState.femaleCount,
                                onValueChange = viewModel::updateFemaleCount,
                                label = stringResource(R.string.field_female_count),
                            )
                            FormDropdown(
                                label = stringResource(R.string.field_mother),
                                options = listOf(null to stringResource(R.string.not_selected)) +
                                    uiState.catFemales.map { it.id to it.name },
                                selectedKey = uiState.motherId,
                                onSelected = viewModel::updateMotherId,
                            )
                            FormDropdown(
                                label = stringResource(R.string.field_father),
                                options = listOf(null to stringResource(R.string.not_selected)) +
                                    uiState.catMales.map { it.id to it.name },
                                selectedKey = uiState.fatherId,
                                onSelected = viewModel::updateFatherId,
                            )
                        }
                        "kitten" -> {
                            FormTextField(
                                value = uiState.color,
                                onValueChange = viewModel::updateColor,
                                label = stringResource(R.string.field_color),
                            )
                            if (uiState.isBreeder) {
                                FormTextField(
                                    value = uiState.birthWeight,
                                    onValueChange = viewModel::updateBirthWeight,
                                    label = stringResource(R.string.field_birth_weight),
                                )
                            }
                            FormDropdown(
                                label = stringResource(R.string.field_litter),
                                options = uiState.litters.map { it.id to it.name },
                                selectedKey = uiState.litterId,
                                onSelected = viewModel::updateLitterId,
                            )
                            FormDropdown(
                                label = stringResource(R.string.field_status),
                                options = listOf(
                                    KittenStatus.FREE to stringResource(R.string.status_free),
                                    KittenStatus.RESERVED to stringResource(R.string.status_reserved),
                                ),
                                selectedKey = uiState.status,
                                onSelected = { status -> status?.let(viewModel::updateStatus) },
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.photos),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                    ) {
                        items(uiState.photoUrls, key = { it }) { url ->
                            PhotoItem(
                                url = url,
                                onRemove = { viewModel.removePhoto(url) },
                            )
                        }
                    }
                    OutlinedButton(onClick = { showPhotoPicker = true }) {
                        Text(stringResource(R.string.add_photo))
                    }
                    uiState.error?.let { error ->
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    if (uiState.isSaving) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                }
            }
        }
    }
}

private data class FormTypeOption(
    val type: String,
    val labelRes: Int,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormTypeTabs(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
) {
    val options = listOf(
        FormTypeOption("cat_female", R.string.form_tab_cat_female),
        FormTypeOption("cat_male", R.string.form_tab_cat_male),
        FormTypeOption("litter", R.string.form_tab_litter),
        FormTypeOption("kitten", R.string.form_tab_kitten),
    )
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { onTypeSelected(option.type) },
                selected = selectedType == option.type,
            ) {
                Text(stringResource(option.labelRes))
            }
        }
    }
}

@Composable
private fun PhotoItem(
    url: String,
    onRemove: () -> Unit,
) {
    Row(verticalAlignment = Alignment.Top) {
        PetPhoto(photoUrl = url, size = 88.dp)
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = BluePrimary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> FormDropdown(
    label: String,
    options: List<Pair<T, String>>,
    selectedKey: T?,
    onSelected: (T?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selectedKey }?.second.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
    ) {
        FormTextField(
            value = selectedLabel,
            onValueChange = {},
            label = label,
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { (key, title) ->
                DropdownMenuItem(
                    text = { Text(title) },
                    onClick = {
                        onSelected(key)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
