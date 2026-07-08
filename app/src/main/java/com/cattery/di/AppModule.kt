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
}
