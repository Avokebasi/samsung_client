package com.cattery.presentation.screens.reservations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.cattery.presentation.components.CatalogEmptyState
import com.cattery.presentation.components.CatalogScaffold
import com.cattery.presentation.components.CatalogSearchField
import com.cattery.presentation.components.ReservationListItem
import com.cattery.presentation.theme.BluePrimary
import com.cattery.presentation.viewmodels.ReservationsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReservationsScreen(
    onBack: () -> Unit,
    onKittenClick: (Long) -> Unit,
    viewModel: ReservationsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CatalogScaffold(
        title = stringResource(R.string.reservations),
        onBack = onBack,
        showAdd = false,
        addLabel = "",
        onAdd = {},
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            CatalogSearchField(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                placeholder = stringResource(R.string.search_hint),
                modifier = Modifier.padding(vertical = 8.dp),
            )
            when {
                uiState.isLoading && uiState.items.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                }
                uiState.items.isEmpty() -> {
                    CatalogEmptyState(message = stringResource(R.string.empty_reservations))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(uiState.items, key = { it.id }) { item ->
                            ReservationListItem(
                                reservation = item,
                                onClick = { onKittenClick(item.kittenId) },
                            )
                        }
                    }
                }
            }
            uiState.error?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}
