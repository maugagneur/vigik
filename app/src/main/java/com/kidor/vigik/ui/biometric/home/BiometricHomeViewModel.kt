package com.kidor.vigik.ui.biometric.home

import androidx.lifecycle.viewModelScope
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
    private val userRepository: UserRepository
) : BaseViewModel<BiometricHomeViewAction, Nothing, BiometricHomeViewEvent>() {

    init {
        viewModelScope.launch {
            userRepository.isUserLoggedIn.collect { isUserLoggedIn ->
                if (!isUserLoggedIn) {
                    // Logout detected
                    _viewEvent.emit(BiometricHomeViewEvent.NavigateToBiometricLogin)
                }
            }
        }
    }

    override fun handleAction(viewAction: BiometricHomeViewAction) {
        when (viewAction) {
            is BiometricHomeViewAction.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
