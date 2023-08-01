package com.kidor.vigik.ui.biometric.login

import com.kidor.vigik.ui.base.ViewState

/**
 * State that displayed the login screen with username and password fields.
 *
 * @param usernameField             The username.
 * @param passwordField             The password.
 * @param isBiometricLoginAvailable True if user could use biometric for login.
 * @param displayLoginFail          True if last login attempt failed, otherwise false.
 */
data class BiometricLoginViewState(
    val usernameField: String = "",
    val passwordField: String = "",
    val isBiometricLoginAvailable: Boolean = false,
    val displayLoginFail: Boolean = false
) : ViewState
