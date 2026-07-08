package com.cattery.di

import com.cattery.data.local.database.AppDatabase
import com.cattery.data.local.datastore.TokenManager
import com.cattery.data.local.repository.LocalRepository
import com.cattery.data.remote.api.ApiService
import com.cattery.data.remote.client.createKtorClient
import com.cattery.data.remote.repository.RemoteRepository
import com.cattery.domain.usecases.AuthUseCases
import com.cattery.domain.usecases.CatalogUseCases
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
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.create(androidContext()) }
    single { TokenManager(androidContext()) }
    single { LocalRepository(get()) }

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

    single { AuthUseCases(get(), get()) }
    single { CatalogUseCases(get(), get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { CatFemaleListViewModel(get()) }
    viewModel { CatMaleListViewModel(get()) }
    viewModel { LitterListViewModel(get()) }
    viewModel { CatFemaleDetailViewModel(get(), get()) }
    viewModel { CatMaleDetailViewModel(get(), get()) }
    viewModel { LitterDetailViewModel(get(), get()) }
    viewModel { KittenListViewModel(get(), get()) }
    viewModel { KittenDetailViewModel(get(), get()) }
    viewModel { EntityFormViewModel(get(), get()) }
}
