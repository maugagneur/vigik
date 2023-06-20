package com.kidor.vigik.ui.biometric.login

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.data.biometric.BiometricRepository
import com.kidor.vigik.data.biometric.model.BiometricAuthenticationStatus
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

/**
 * Business logic of biometric screen.
 */
@HiltViewModel
class BiometricLoginViewModel @Inject constructor(
    private val biometricRepository: BiometricRepository
) : BaseViewModel<BiometricLoginViewAction, BiometricLoginViewState, BiometricLoginViewEvent>() {

    private val _biometricPromptState = MutableStateFlow(BiometricPromptContainerState())
    val biometricPromptState: StateFlow<BiometricPromptContainerState> get() = _biometricPromptState

    init {
        viewModelScope.launch {
            biometricRepository.getBiometricInfo().let {
                Timber.d("Biometric info -> $it")
                _viewState.value = BiometricLoginViewState(
                    isBiometricLoginAvailable = it.isAuthenticationAvailable() && it.biometricTokenIsPresent
                )
            }
        }
    }

    override fun handleAction(viewAction: BiometricLoginViewAction) {
        when (viewAction) {
            is BiometricLoginViewAction.UpdateUsername -> updateUsername(viewAction)
            is BiometricLoginViewAction.UpdatePassword -> updatePassword(viewAction)
            is BiometricLoginViewAction.Login -> login()
            is BiometricLoginViewAction.OnBiometricAuthError -> { Timber.d("OnBiometricAuthError()") }
            is BiometricLoginViewAction.OnBiometricAuthSuccess -> { Timber.d("OnBiometricAuthSuccess()") }
        }
    }

    /**
     * Updates view state based on given [BiometricLoginViewAction.UpdateUsername]
     *
     * @param action The [BiometricLoginViewAction.UpdateUsername].
     */
    private fun updateUsername(action: BiometricLoginViewAction.UpdateUsername) {
        viewState.value?.let { currentViewState ->
            _viewState.value = currentViewState.copy(usernameField = action.username)
        }
    }

    /**
     * Updates view state based on given [BiometricLoginViewAction.UpdatePassword]
     *
     * @param action The [BiometricLoginViewAction.UpdatePassword].
     */
    private fun updatePassword(action: BiometricLoginViewAction.UpdatePassword) {
        viewState.value?.let { currentState ->
            _viewState.value = currentState.copy(passwordField = action.password)
        }
    }

    /**
     * Performs a login attempt with current values of username and password.
     */
    private fun login() {
        viewState.value?.let { currentState ->
            if (isPasswordValid(username = currentState.usernameField, password = currentState.passwordField)) {
                viewModelScope.launch {
                    // FIXME: for now, always show home without biometric
                    _viewEvent.emit(BiometricLoginViewEvent.NavigateToBiometricHome)
                    // Get user token and save it
                    // TODO: biometricRepository.saveUserToken(getUserToken())

                    val biometricInfo = biometricRepository.getBiometricInfo()
                    Timber.d("Biometric Info -> $biometricInfo")

                    when (biometricInfo.biometricAuthenticationStatus) {
                        BiometricAuthenticationStatus.AVAILABLE_BUT_NOT_ENROLLED -> {
                            // Prompts the user to create credentials that the app accepts
                            _viewEvent.emit(
                                BiometricLoginViewEvent.DisplayBiometricEnrollment(
                                    enrollIntent = biometricRepository.getBiometricEnrollIntent()
                                )
                            )
                        }

                        BiometricAuthenticationStatus.READY -> {
                            // TODO: Display biometric prompt
                            /*
                            _viewEvent.emit(
                                BiometricLoginViewEvent.DisplayBiometricPromptForEncryption(
                                    authenticationCallback = biometricRepository.getBiometricAuthenticationCallback(),
                                    promptInfo = biometricRepository.getBiometricPromptInfoForEncryption(),
                                    cryptoObject = biometricRepository.getCryptoObject(
                                        CryptoPurpose.ENCRYPTION,
                                        null
                                    )
                                )
                            )
                            */
                        }

                        else -> Timber.e("Error during biometric status check")
                    }
                }
            } else {
                Timber.w("Invalid username/password")
                _viewState.value = currentState.copy(displayLoginFail = true)
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
     * Returns a mocked user token as we do not have a real backend for authentication.
     */
    private fun getUserToken(): String = UUID.randomUUID().toString()
}
