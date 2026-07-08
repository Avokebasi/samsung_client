package com.cattery.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cattery.R
import com.cattery.data.remote.repository.RemoteRepository
import com.cattery.presentation.screens.auth.LoginScreen
import com.cattery.presentation.screens.auth.RegisterScreen
import com.cattery.presentation.screens.catalog.CatalogPlaceholderScreen
import com.cattery.presentation.screens.home.HomeScreen
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
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToReservations = { navController.navigate(Routes.RESERVATIONS) },
                    onNavigateToCatFemales = { navController.navigate(Routes.CAT_FEMALES) },
                    onNavigateToCatMales = { navController.navigate(Routes.CAT_MALES) },
                    onNavigateToLitters = { navController.navigate(Routes.LITTERS) },
                    onPetClick = { _, _ -> },
                )
            }
            composable(Routes.RESERVATIONS) {
                CatalogPlaceholderScreen(
                    title = stringResource(R.string.reservations),
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.CAT_FEMALES) {
                CatalogPlaceholderScreen(
                    title = stringResource(R.string.section_cat_females),
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.CAT_MALES) {
                CatalogPlaceholderScreen(
                    title = stringResource(R.string.section_cat_males),
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.LITTERS) {
                CatalogPlaceholderScreen(
                    title = stringResource(R.string.section_litters),
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
