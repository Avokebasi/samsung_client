package com.cattery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cattery.data.local.repository.LocalRepository
import com.cattery.domain.usecases.AuthUseCases
import com.cattery.domain.usecases.SyncUseCases
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authUseCases: AuthUseCases,
    private val localRepository: LocalRepository,
    private val syncUseCases: SyncUseCases,
) : ViewModel() {

    fun start(
        onNavigateToLogin: () -> Unit,
        onNavigateToHome: () -> Unit,
    ) {
        viewModelScope.launch {
            authUseCases.init()
            delay(900)
            if (!authUseCases.isLoggedIn()) {
                onNavigateToLogin()
                return@launch
            }
            if (syncUseCases.isCurrentlyOnline()) {
                authUseCases.getCurrentUser()
                    .onSuccess {
                        syncUseCases.syncAll()
                        onNavigateToHome()
                    }
                    .onFailure {
                        if (localRepository.hasCachedUser()) {
                            onNavigateToHome()
                        } else {
                            authUseCases.logout()
                            onNavigateToLogin()
                        }
                    }
            } else if (localRepository.hasCachedUser()) {
                onNavigateToHome()
            } else {
                authUseCases.logout()
                onNavigateToLogin()
            }
        }
    }
}
