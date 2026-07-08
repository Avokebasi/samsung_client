package com.cattery.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cattery.domain.models.UserRole
import com.cattery.domain.usecases.CatalogUseCases
import org.koin.compose.koinInject

@Composable
fun HomePlaceholderScreen(
    catalogUseCases: CatalogUseCases = koinInject(),
) {
    val user by catalogUseCases.observeCurrentUser().collectAsState(initial = null)
    val catFemales by catalogUseCases.observeCatFemales().collectAsState(initial = emptyList())
    val catMales by catalogUseCases.observeCatMales().collectAsState(initial = emptyList())
    val litters by catalogUseCases.observeLitters().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        catalogUseCases.refreshAll()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Главный экран",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = user?.let { "${it.name} (${if (it.role == UserRole.BREEDER) "заводчик" else "покупатель"})" }
                ?: "Загрузка…",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            text = "Кошки: ${catFemales.size} · Коты: ${catMales.size} · Помёты: ${litters.size}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
