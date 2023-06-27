package com.kidor.vigik.ui.biometric.home

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.data.biometric.BiometricRepository
import com.kidor.vigik.data.user.UserRepository
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Business logic of biometric home screen.
 */
@HiltViewModel
class BiometricHomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val biometricRepository: BiometricRepository
) : BaseViewModel<BiometricHomeViewAction, BiometricHomeViewState, BiometricHomeViewEvent>() {

    init {
        viewModelScope.launch {
            userRepository.isUserLoggedIn.collect { isUserLoggedIn ->
                if (!isUserLoggedIn) {
                    // Logout detected
                    _viewEvent.emit(BiometricHomeViewEvent.NavigateToBiometricLogin)
                }
            }
        }
        viewModelScope.launch {
            _viewState.value = BiometricHomeViewState(
                isBiometricCredentialsSaved = biometricRepository.getBiometricInfo().biometricTokenIsPresent
            )
        }
    }

    override fun handleAction(viewAction: BiometricHomeViewAction) {
        when (viewAction) {
            is BiometricHomeViewAction.RemoveCredentials -> removeCredentials()
            is BiometricHomeViewAction.Logout -> logout()
        }
    }

    /**
     * Removes biometric credentials from persistent storage.
     */
    private fun removeCredentials() {
        viewModelScope.launch {
            biometricRepository.removeToken()
            _viewState.value = BiometricHomeViewState(isBiometricCredentialsSaved = false)
        }
    }

    /**
     * Logs out the user.
     */
    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
