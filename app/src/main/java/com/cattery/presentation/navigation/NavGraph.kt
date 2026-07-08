package com.cattery.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cattery.R
import com.cattery.data.remote.repository.RemoteRepository
import com.cattery.presentation.screens.auth.LoginScreen
import com.cattery.presentation.screens.auth.RegisterScreen
import com.cattery.presentation.screens.catalog.CatFemaleDetailScreen
import com.cattery.presentation.screens.catalog.CatFemaleListScreen
import com.cattery.presentation.screens.catalog.CatMaleDetailScreen
import com.cattery.presentation.screens.catalog.CatMaleListScreen
import com.cattery.presentation.screens.catalog.EntityFormScreen
import com.cattery.presentation.screens.catalog.KittenDetailScreen
import com.cattery.presentation.screens.catalog.KittenListScreen
import com.cattery.presentation.screens.catalog.LitterDetailScreen
import com.cattery.presentation.screens.catalog.LitterListScreen
import com.cattery.presentation.screens.home.HomeScreen
import com.cattery.presentation.screens.reservations.ReservationsScreen
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
                    onPetClick = { type, id ->
                        when (type) {
                            "female" -> navController.navigate(Routes.catFemaleDetail(id))
                            "male" -> navController.navigate(Routes.catMaleDetail(id))
                            "litter" -> navController.navigate(Routes.litterDetail(id))
                        }
                    },
                )
            }
            composable(Routes.RESERVATIONS) {
                ReservationsScreen(
                    onBack = { navController.popBackStack() },
                    onKittenClick = { id -> navController.navigate(Routes.kittenDetail(id)) },
                )
            }
            composable(Routes.CAT_FEMALES) {
                CatFemaleListScreen(
                    onBack = { navController.popBackStack() },
                    onItemClick = { id -> navController.navigate(Routes.catFemaleDetail(id)) },
                    onAdd = { navController.navigate(Routes.form("cat_female")) },
                )
            }
            composable(
                route = Routes.CAT_FEMALE_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                CatFemaleDetailScreen(
                    onBack = { navController.popBackStack() },
                    onLitterClick = { litterId -> navController.navigate(Routes.litterDetail(litterId)) },
                    onEdit = { navController.navigate(Routes.form("cat_female", entityId = id)) },
                )
            }
            composable(Routes.CAT_MALES) {
                CatMaleListScreen(
                    onBack = { navController.popBackStack() },
                    onItemClick = { id -> navController.navigate(Routes.catMaleDetail(id)) },
                    onAdd = { navController.navigate(Routes.form("cat_male")) },
                )
            }
            composable(
                route = Routes.CAT_MALE_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                CatMaleDetailScreen(
                    onBack = { navController.popBackStack() },
                    onLitterClick = { litterId -> navController.navigate(Routes.litterDetail(litterId)) },
                    onEdit = { navController.navigate(Routes.form("cat_male", entityId = id)) },
                )
            }
            composable(Routes.LITTERS) {
                LitterListScreen(
                    onBack = { navController.popBackStack() },
                    onItemClick = { id -> navController.navigate(Routes.litterDetail(id)) },
                    onAdd = { navController.navigate(Routes.form("litter")) },
                )
            }
            composable(
                route = Routes.LITTER_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) { backStackEntry ->
                val litterId = backStackEntry.arguments?.getLong("id") ?: 0L
                LitterDetailScreen(
                    onBack = { navController.popBackStack() },
                    onKittensClick = { navController.navigate(Routes.litterKittens(litterId)) },
                    onEdit = { navController.navigate(Routes.form("litter", entityId = litterId)) },
                )
            }
            composable(
                route = Routes.LITTER_KITTENS,
                arguments = listOf(navArgument("litterId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val litterId = backStackEntry.arguments?.getLong("litterId") ?: 0L
                KittenListScreen(
                    onBack = { navController.popBackStack() },
                    onItemClick = { id -> navController.navigate(Routes.kittenDetail(id)) },
                    onAdd = { navController.navigate(Routes.form("kitten", litterId = litterId)) },
                )
            }
            composable(
                route = Routes.KITTEN_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                KittenDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Routes.form("kitten", entityId = id)) },
                )
            }
            composable(
                route = Routes.FORM,
                arguments = listOf(
                    navArgument("entityType") { type = NavType.StringType },
                    navArgument("entityId") {
                        type = NavType.LongType
                        defaultValue = Routes.NO_ID
                    },
                    navArgument("litterId") {
                        type = NavType.LongType
                        defaultValue = Routes.NO_ID
                    },
                ),
            ) { backStackEntry ->
                val entityType = backStackEntry.arguments?.getString("entityType").orEmpty()
                val entityId = backStackEntry.arguments?.getLong("entityId") ?: Routes.NO_ID
                val isEdit = entityId != Routes.NO_ID
                val title = when (entityType) {
                    "cat_female" -> stringResource(
                        if (isEdit) R.string.form_edit_cat_female else R.string.form_add_cat_female,
                    )
                    "cat_male" -> stringResource(
                        if (isEdit) R.string.form_edit_cat_male else R.string.form_add_cat_male,
                    )
                    "litter" -> stringResource(
                        if (isEdit) R.string.form_edit_litter else R.string.form_add_litter,
                    )
                    "kitten" -> stringResource(
                        if (isEdit) R.string.form_edit_kitten else R.string.form_add_kitten,
                    )
                    else -> stringResource(R.string.add)
                }
                EntityFormScreen(
                    title = title,
                    onBack = { navController.popBackStack() },
                    onCompleted = { navController.popBackStack() },
                )
            }
        }
    }
}
