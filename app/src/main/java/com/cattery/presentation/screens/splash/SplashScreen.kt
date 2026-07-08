package com.cattery.presentation.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cattery.R
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
        if (!authUseCases.isLoggedIn()) {
            onNavigateToLogin()
            return@LaunchedEffect
        }
        val userResult = authUseCases.getCurrentUser()
        if (userResult.isSuccess) {
            onNavigateToHome()
        } else {
            authUseCases.logout()
            onNavigateToLogin()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = BluePrimary,
        )
        Spacer(modifier = Modifier.height(24.dp))
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = BluePrimary,
            strokeWidth = 3.dp,
        )
    }
}
