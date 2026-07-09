package com.cattery.presentation.screens.catalog

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
import com.cattery.presentation.components.CatalogListItem
import com.cattery.presentation.components.CatalogScaffold
import com.cattery.presentation.components.CatalogSearchField
import com.cattery.presentation.components.RefreshableContent
import com.cattery.presentation.theme.BluePrimary
import com.cattery.presentation.viewmodels.CatalogListUiState
import com.cattery.presentation.viewmodels.CatFemaleListViewModel
import com.cattery.presentation.viewmodels.CatMaleListViewModel
import com.cattery.presentation.viewmodels.KittenListViewModel
import com.cattery.presentation.viewmodels.LitterListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CatFemaleListScreen(
    onBack: () -> Unit,
    onItemClick: (Long) -> Unit,
    onAdd: () -> Unit,
    viewModel: CatFemaleListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CatalogListContent(
        title = stringResource(R.string.section_cat_females),
        emptyMessage = stringResource(R.string.empty_cat_females),
        uiState = uiState,
        onBack = onBack,
        onQueryChange = viewModel::onQueryChange,
        onItemClick = onItemClick,
        onAdd = onAdd,
        isRefreshing = uiState.isLoading && uiState.items.isNotEmpty(),
        onRefresh = viewModel::refresh,
    )
}

@Composable
fun CatMaleListScreen(
    onBack: () -> Unit,
    onItemClick: (Long) -> Unit,
    onAdd: () -> Unit,
    viewModel: CatMaleListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CatalogListContent(
        title = stringResource(R.string.section_cat_males),
        emptyMessage = stringResource(R.string.empty_cat_males),
        uiState = uiState,
        onBack = onBack,
        onQueryChange = viewModel::onQueryChange,
        onItemClick = onItemClick,
        onAdd = onAdd,
        isRefreshing = uiState.isLoading && uiState.items.isNotEmpty(),
        onRefresh = viewModel::refresh,
    )
}

@Composable
fun LitterListScreen(
    onBack: () -> Unit,
    onItemClick: (Long) -> Unit,
    onAdd: () -> Unit,
    viewModel: LitterListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CatalogListContent(
        title = stringResource(R.string.section_litters),
        emptyMessage = stringResource(R.string.empty_litters),
        uiState = uiState,
        onBack = onBack,
        onQueryChange = viewModel::onQueryChange,
        onItemClick = onItemClick,
        onAdd = onAdd,
        isRefreshing = uiState.isLoading && uiState.items.isNotEmpty(),
        onRefresh = viewModel::refresh,
    )
}

@Composable
fun KittenListScreen(
    onBack: () -> Unit,
    onItemClick: (Long) -> Unit,
    onAdd: () -> Unit,
    viewModel: KittenListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CatalogListContent(
        title = stringResource(R.string.section_kittens),
        emptyMessage = stringResource(R.string.empty_kittens),
        uiState = uiState,
        onBack = onBack,
        onQueryChange = viewModel::onQueryChange,
        onItemClick = onItemClick,
        onAdd = onAdd,
        isRefreshing = uiState.isLoading && uiState.items.isNotEmpty(),
        onRefresh = viewModel::refresh,
    )
}

@Composable
private fun CatalogListContent(
    title: String,
    emptyMessage: String,
    uiState: CatalogListUiState,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onItemClick: (Long) -> Unit,
    onAdd: () -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    CatalogScaffold(
        title = title,
        onBack = onBack,
        showAdd = uiState.isBreeder,
        addLabel = stringResource(R.string.add),
        onAdd = onAdd,
    ) { padding ->
        RefreshableContent(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
            CatalogSearchField(
                query = uiState.query,
                onQueryChange = onQueryChange,
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
                    CatalogEmptyState(message = emptyMessage)
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(uiState.items, key = { it.id }) { item ->
                            CatalogListItem(
                                name = item.name,
                                subtitle = item.subtitle,
                                photoUrl = item.photoUrl,
                                onClick = { onItemClick(item.id) },
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
}
