package com.cattery.presentation.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cattery.domain.usecases.AuthUseCases
import com.cattery.presentation.theme.BluePrimary
import com.cattery.presentation.theme.WhiteBackground
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    authUseCases: AuthUseCases = koinInject(),
) {
    LaunchedEffect(Unit) {
        authUseCases.init()
        delay(900)
        if (authUseCases.isLoggedIn()) onNavigateToHome() else onNavigateToLogin()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        Text(
            text = "Cattery",
            style = MaterialTheme.typography.headlineLarge,
            color = BluePrimary,
        )
        Spacer(modifier = Modifier.height(24.dp))
        CircularProgressIndicator(color = BluePrimary)
    }
}
