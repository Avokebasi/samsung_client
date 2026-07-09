package com.cattery.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.cattery.presentation.components.AvatarPickerDialog
import com.cattery.presentation.components.HomeSectionHeader
import com.cattery.presentation.components.LastSyncLabel
import com.cattery.presentation.components.PetScrollCard
import com.cattery.presentation.components.RefreshableContent
import com.cattery.presentation.components.UserAvatar
import com.cattery.presentation.components.litterSubtitle
import com.cattery.presentation.components.petAgeSubtitle
import com.cattery.presentation.theme.BluePrimary
import com.cattery.presentation.theme.TextSecondary
import com.cattery.presentation.theme.WhiteBackground
import com.cattery.presentation.util.DateFormatter
import com.cattery.presentation.util.rememberImageCaptureHandlers
import com.cattery.presentation.viewmodels.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToReservations: () -> Unit,
    onNavigateToAddForm: () -> Unit,
    onNavigateToCatFemales: () -> Unit,
    onNavigateToCatMales: () -> Unit,
    onNavigateToLitters: () -> Unit,
    onPetClick: (String, Long) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAvatarPicker by remember { mutableStateOf(false) }

    val imageHandlers = rememberImageCaptureHandlers { uri ->
        viewModel.updateAvatar(uri)
    }

    if (showAvatarPicker) {
        AvatarPickerDialog(
            onDismiss = { showAvatarPicker = false },
            onGallery = imageHandlers.launchGallery,
            onCamera = imageHandlers.launchCamera,
        )
    }

    if (uiState.isLoading && uiState.user == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(color = BluePrimary)
        }
        return
    }

    RefreshableContent(
        isRefreshing = uiState.isSyncing,
        onRefresh = viewModel::refresh,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = onNavigateToReservations,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BluePrimary,
                            contentColor = WhiteBackground,
                        ),
                    ) {
                        Text(
                            stringResource(
                                if (uiState.isBreeder) R.string.reservations else R.string.my_reservations,
                            ),
                        )
                    }
                    if (uiState.isBreeder) {
                        OutlinedButton(onClick = onNavigateToAddForm) {
                            Text(stringResource(R.string.add))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    UserAvatar(
                        avatarUrl = uiState.user?.avatarUrl,
                        localUri = uiState.localAvatarUri,
                        onClick = { showAvatarPicker = true },
                        size = 52.dp,
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick = { viewModel.logout(onLogout) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.logout))
                }
            }

        if (uiState.lastSyncMillis > 0L) {
            item {
                LastSyncLabel(
                    formattedTime = DateFormatter.formatEpochMillis(uiState.lastSyncMillis),
                )
            }
        }

        item {
            HomeSectionHeader(
                title = stringResource(R.string.section_cat_females),
                onClick = onNavigateToCatFemales,
            )
        }
        item {
            if (uiState.catFemales.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_cat_females),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.catFemales, key = { it.id }) { cat ->
                        PetScrollCard(
                            name = cat.name,
                            subtitle = petAgeSubtitle(cat.birthDate),
                            photoUrl = cat.photoUrls.firstOrNull(),
                            onClick = { onPetClick("female", cat.id) },
                        )
                    }
                }
            }
        }

        item {
            HomeSectionHeader(
                title = stringResource(R.string.section_cat_males),
                onClick = onNavigateToCatMales,
            )
        }
        item {
            if (uiState.catMales.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_cat_males),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.catMales, key = { it.id }) { cat ->
                        PetScrollCard(
                            name = cat.name,
                            subtitle = petAgeSubtitle(cat.birthDate),
                            photoUrl = cat.photoUrls.firstOrNull(),
                            onClick = { onPetClick("male", cat.id) },
                        )
                    }
                }
            }
        }

        item {
            HomeSectionHeader(
                title = stringResource(R.string.section_litters),
                onClick = onNavigateToLitters,
            )
        }
        item {
            if (uiState.litters.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_litters),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.litters, key = { it.id }) { litter ->
                        PetScrollCard(
                            name = litter.name,
                            subtitle = litterSubtitle(litter.birthDate),
                            photoUrl = litter.photoUrls.firstOrNull(),
                            onClick = { onPetClick("litter", litter.id) },
                        )
                    }
                }
            }
        }

        uiState.error?.let { error ->
            item {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        }
    }
}
