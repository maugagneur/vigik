package com.kidor.vigik.ui.biometric

import androidx.biometric.BiometricManager
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Business logic of biometric screen.
 */
@HiltViewModel
class BiometricViewModel @Inject constructor(
    biometricManager: BiometricManager
) : BaseViewModel<BiometricViewAction, BiometricViewState, Nothing>() {

    init {
        // Initial state
        _viewState.value = BiometricViewState.Login()
    }

    override fun handleAction(viewAction: BiometricViewAction) {
        when (viewAction) {
            is BiometricViewAction.UpdateUsername -> updateUsername(viewAction)
            is BiometricViewAction.UpdatePassword -> updatePassword(viewAction)
            is BiometricViewAction.Login -> login()
            is BiometricViewAction.Logout -> logout()
        }
    }

    /**
     * Updates view state based on given [BiometricViewAction.UpdateUsername]
     *
     * @param action The [BiometricViewAction.UpdateUsername].
     */
    private fun updateUsername(action: BiometricViewAction.UpdateUsername) {
        viewState.value.let { currentViewState ->
            if (currentViewState is BiometricViewState.Login) {
                _viewState.value = currentViewState.copy(usernameField = action.username)
            }
        }
    }

    /**
     * Updates view state based on given [BiometricViewAction.UpdatePassword]
     *
     * @param action The [BiometricViewAction.UpdatePassword].
     */
    private fun updatePassword(action: BiometricViewAction.UpdatePassword) {
        viewState.value.let { currentState ->
            if (currentState is BiometricViewState.Login) {
                _viewState.value = currentState.copy(passwordField = action.password)
            }
        }
    }

    /**
     * Performs a login attempt with current values of username and password.
     */
    private fun login() {
        viewState.value.let { currentState ->
            if (currentState is BiometricViewState.Login) {
                _viewState.value = if (
                    isPasswordValid(username = currentState.usernameField, password = currentState.passwordField)
                ) {
                    BiometricViewState.Logged
                } else {
                    Timber.e("Fail to log in")
                    currentState.copy(displayLoginFail = true)
                }
            }
        }
    }

    /**
     * Returns true if the password is correct for this username.
     */
    private fun isPasswordValid(username: String, password: String): Boolean {
        // For this application every not blank username/password couple are correct.
        return username.isNotBlank() && password.isNotBlank()
    }

    /**
     * Performs a logout.
     */
    private fun logout() {
        viewState.value.let { currentState ->
            if (currentState is BiometricViewState.Logged) {
                _viewState.value = BiometricViewState.Login()
            }
        }
    }
}
