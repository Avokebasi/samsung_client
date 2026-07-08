package com.cattery.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cattery.data.remote.repository.RemoteRepository
import com.cattery.presentation.screens.auth.LoginPlaceholderScreen
import com.cattery.presentation.screens.auth.RegisterPlaceholderScreen
import com.cattery.presentation.screens.home.HomePlaceholderScreen
import com.cattery.presentation.screens.splash.SplashScreen
import com.cattery.presentation.theme.WhiteBackground
import org.koin.compose.koinInject

@Composable
fun CatteryNavGraph(
    remoteRepository: RemoteRepository = koinInject(),
) {
    val navController = rememberNavController()

    LaunchedEffect(remoteRepository) {
        remoteRepository.unauthorizedEvents.collect {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = WhiteBackground,
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.LOGIN) {
                LoginPlaceholderScreen()
            }
            composable(Routes.REGISTER) {
                RegisterPlaceholderScreen()
            }
            composable(Routes.HOME) {
                HomePlaceholderScreen()
            }
        }
    }
}
