package com.cattery.di

import com.cattery.data.local.database.AppDatabase
import com.cattery.data.local.images.ImageDataUrlEncoder
import com.cattery.data.local.datastore.SyncStore
import com.cattery.data.local.datastore.TokenManager
import com.cattery.data.local.network.NetworkMonitor
import com.cattery.data.local.repository.LocalRepository
import com.cattery.data.remote.api.ApiService
import com.cattery.data.remote.client.createKtorClient
import com.cattery.data.remote.repository.RemoteRepository
import com.cattery.data.sync.SyncManager
import com.cattery.domain.usecases.AuthUseCases
import com.cattery.domain.usecases.CatalogUseCases
import com.cattery.domain.usecases.SyncUseCases
import com.cattery.presentation.viewmodels.AuthViewModel
import com.cattery.presentation.viewmodels.CatFemaleDetailViewModel
import com.cattery.presentation.viewmodels.CatFemaleListViewModel
import com.cattery.presentation.viewmodels.CatMaleDetailViewModel
import com.cattery.presentation.viewmodels.CatMaleListViewModel
import com.cattery.presentation.viewmodels.EntityFormViewModel
import com.cattery.presentation.viewmodels.HomeViewModel
import com.cattery.presentation.viewmodels.KittenDetailViewModel
import com.cattery.presentation.viewmodels.KittenListViewModel
import com.cattery.presentation.viewmodels.LitterDetailViewModel
import com.cattery.presentation.viewmodels.LitterListViewModel
import com.cattery.presentation.viewmodels.ReservationsViewModel
import com.cattery.presentation.viewmodels.SplashViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.create(androidContext()) }
    single { TokenManager(androidContext()) }
    single { SyncStore(androidContext()) }
    single { NetworkMonitor(androidContext()) }
    single { LocalRepository(get()) }
    single { ImageDataUrlEncoder(androidContext()) }

    single(named("appScope")) { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    single {
        val tokenManager: TokenManager = get()
        val localRepository: LocalRepository = get()
        lateinit var remoteRepository: RemoteRepository
        val httpClient = createKtorClient(tokenManager) {
            remoteRepository.notifyUnauthorized()
        }
        val apiService = ApiService(httpClient)
        remoteRepository = RemoteRepository(apiService, tokenManager, localRepository)
        remoteRepository
    }

    single(createdAtStart = true) {
        SyncManager(
            remoteRepository = get(),
            localRepository = get(),
            networkMonitor = get(),
            syncStore = get(),
            applicationScope = get(named("appScope")),
        )
    }

    single { AuthUseCases(get(), get()) }
    single { SyncUseCases(get()) }
    single { CatalogUseCases(get(), get(), get()) }

    viewModel { SplashViewModel(get(), get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { CatFemaleListViewModel(get()) }
    viewModel { CatMaleListViewModel(get()) }
    viewModel { LitterListViewModel(get()) }
    viewModel { CatFemaleDetailViewModel(get(), get()) }
    viewModel { CatMaleDetailViewModel(get(), get()) }
    viewModel { LitterDetailViewModel(get(), get()) }
    viewModel { KittenListViewModel(get(), get()) }
    viewModel { KittenDetailViewModel(get(), get()) }
    viewModel { EntityFormViewModel(get(), get()) }
    viewModel { ReservationsViewModel(get()) }
}
