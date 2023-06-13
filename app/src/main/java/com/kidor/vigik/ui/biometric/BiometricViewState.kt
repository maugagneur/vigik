package com.kidor.vigik.ui.biometric

import com.kidor.vigik.ui.base.ViewState

/**
 * Possible states of biometric's view.
 */
sealed class BiometricViewState : ViewState() {

    /**
     * State that displayed the login screen with username and password fields.
     *
     * @param usernameField    The username.
     * @param passwordField    The password.
     * @param displayLoginFail True if last login attempt failed, otherwise false.
     */
    data class Login(
        val usernameField: String = "",
        val passwordField: String = "",
        val displayLoginFail: Boolean = false
    ) : BiometricViewState()

    /**
     * State that is displayed when the login is done.
     */
    object Logged : BiometricViewState()

    /**
     * State that displayed the biometric's status.
     *
     * @param status The biometric status.
     */
    data class DisplayBiometricStatus(val status: BiometricAuthenticateStatus) : BiometricViewState()
}
