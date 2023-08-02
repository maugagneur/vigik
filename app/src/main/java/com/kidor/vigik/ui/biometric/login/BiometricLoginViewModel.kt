package com.kidor.vigik.ui.biometric.login

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.data.biometric.BiometricRepository
import com.kidor.vigik.data.biometric.model.BiometricAuthenticationStatus
import com.kidor.vigik.data.crypto.model.CryptoPurpose
import com.kidor.vigik.data.user.UserRepository
import com.kidor.vigik.data.user.model.UserLoginError
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Business logic of biometric login screen.
 */
@HiltViewModel
class BiometricLoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val biometricRepository: BiometricRepository
) : BaseViewModel<BiometricLoginViewAction, BiometricLoginViewState, BiometricLoginViewEvent>() {

    private val _biometricPromptState = MutableSharedFlow<BiometricPromptViewState?>(replay = 1)
    /**
     * State flow of the biometric prompt.
     */
    val biometricPromptState: SharedFlow<BiometricPromptViewState?> get() = _biometricPromptState

    init {
        viewModelScope.launch {
            userRepository.isUserLoggedIn.collect { isUserLoggedIn ->
                if (isUserLoggedIn) {
                    Timber.d("Login succeeded")

                    val biometricInfo = biometricRepository.getBiometricInfo()
                    if (biometricInfo.biometricTokenIsPresent) {
                        // If biometric token is already saved do not prompt biometric enrollment
                        navigateToBiometricHome()
                        return@collect
                    }

                    when (biometricInfo.biometricAuthenticationStatus) {
                        BiometricAuthenticationStatus.AVAILABLE_BUT_NOT_ENROLLED -> {
                            Timber.d("No biometry enrolled on the device -> display settings to enroll biometrics")
                            // Prompts settings to create credentials that the app accepts
                            _viewEvent.emit(
                                BiometricLoginViewEvent.DisplayBiometricEnrollment(
                                    enrollIntent = biometricRepository.getBiometricEnrollIntent()
                                )
                            )
                        }

                        BiometricAuthenticationStatus.READY -> {
                            // Display biometric prompt for enrollment
                            val cryptoObject = biometricRepository.getCryptoObject(purpose = CryptoPurpose.ENCRYPTION)
                            if (cryptoObject != null) {
                                _biometricPromptState.emit(
                                    BiometricPromptViewState(
                                        isVisible = true,
                                        promptInfo = biometricRepository.getBiometricPromptInfo(
                                            purpose = CryptoPurpose.ENCRYPTION
                                        ),
                                        cryptoObject = cryptoObject,
                                        purpose = CryptoPurpose.ENCRYPTION
                                    )
                                )
                            } else {
                                Timber.e("Invalid crypto object for encryption")
                            }
                        }

                        else -> {
                            Timber.e("Biometric not available")
                            navigateToBiometricHome()
                        }
                    }
                }
            }
        }
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
            is BiometricLoginViewAction.LoginWithBiometric -> loginWithBiometric()
            is BiometricLoginViewAction.HideBiometricPrompt -> viewModelScope.launch {
                _biometricPromptState.emit(biometricPromptState.replayCache.firstOrNull()?.copy(isVisible = false))
            }
            is BiometricLoginViewAction.OnBiometricAuthError -> handleBiometricAuthenticationError(viewAction)
            is BiometricLoginViewAction.OnBiometricAuthSuccess -> handleBiometricAuthenticationSuccess(viewAction)
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
        viewModelScope.launch {
            viewState.value?.let { currentState ->
                if (currentState.usernameField.isBlank() || currentState.passwordField.isBlank()) {
                    _viewState.value = currentState.copy(displayLoginFail = true)
                } else {
                    // If we set new username/password then we should invalidate our previous biometric credentials
                    biometricRepository.removeToken()
                    _viewState.value = currentState.copy(isBiometricLoginAvailable = false)

                    val loginError = userRepository.login(
                        username = currentState.usernameField,
                        password = currentState.passwordField
                    )
                    if (loginError == UserLoginError.INVALID_USERNAME_PASSWORD) {
                        Timber.w("Invalid username/password")
                        _viewState.value = currentState.copy(displayLoginFail = true)
                    }
                }
            }
        }
    }

    /**
     * Tells UI to display biometric prompt for login.
     */
    private fun loginWithBiometric() {
        viewModelScope.launch {
            val cryptoObject = biometricRepository.getCryptoObject(purpose = CryptoPurpose.DECRYPTION)
            if (cryptoObject != null) {
                _biometricPromptState.emit(
                    BiometricPromptViewState(
                        isVisible = true,
                        promptInfo = biometricRepository.getBiometricPromptInfo(
                            purpose = CryptoPurpose.DECRYPTION
                        ),
                        cryptoObject = cryptoObject,
                        purpose = CryptoPurpose.DECRYPTION
                    )
                )
            } else {
                Timber.e("Invalid crypto object for decryption")
            }
        }
    }

    /**
     * Reacts to successful biometric authentication.
     *
     * @param viewAction The action sent from the view.
     */
    private fun handleBiometricAuthenticationSuccess(viewAction: BiometricLoginViewAction.OnBiometricAuthSuccess) {
        val authenticationPurpose = viewAction.purpose
        Timber.d("OnBiometricAuthSuccess($authenticationPurpose)")
        viewModelScope.launch {
            when (authenticationPurpose) {
                CryptoPurpose.ENCRYPTION -> {
                    // Enroll biometric token
                    val userToken = userRepository.getUserToken()
                    if (userToken != null) {
                        biometricRepository.encryptAndStoreToken(
                            token = userToken,
                            cryptoObject = viewAction.cryptoObject
                        )
                    } else {
                        Timber.wtf("Unexpected null user token just after login")
                    }

                    // Show home screen
                    navigateToBiometricHome()
                }
                CryptoPurpose.DECRYPTION -> {
                    // Login with token
                    val userToken = biometricRepository.decryptToken(cryptoObject = viewAction.cryptoObject)
                    if (userToken != null) {
                        val loginError = userRepository.loginWithToken(userToken)
                        if (loginError == UserLoginError.INVALID_USER_TOKEN) {
                            Timber.e("Invalid user token")
                            _viewState.value = viewState.value?.copy(displayLoginFail = true)
                        }
                    } else {
                        Timber.e("Fail to decrypt user token")
                    }
                }
            }
        }
    }

    /**
     * Reacts to failed biometric authentication.
     *
     * @param viewAction The action sent from the view.
     */
    private fun handleBiometricAuthenticationError(viewAction: BiometricLoginViewAction.OnBiometricAuthError) {
        Timber.d("OnBiometricAuthError()")
        // Even if biometric authentication did not work during encryption (maybe because user cancelled it)
        // always show the Home
        if (viewAction.purpose == CryptoPurpose.ENCRYPTION) {
            navigateToBiometricHome()
        }
    }

    /**
     * Sends events to navigate to biometric home screen.
     */
    private fun navigateToBiometricHome() {
        viewModelScope.launch {
            _viewEvent.emit(BiometricLoginViewEvent.NavigateToBiometricHome)
        }
    }
}
