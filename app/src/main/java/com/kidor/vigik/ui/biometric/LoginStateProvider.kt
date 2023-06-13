package com.kidor.vigik.ui.biometric

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Provides a set of data for the preview of LoginState Composable.
 */
class LoginStateProvider : PreviewParameterProvider<LoginStateData> {
    override val values: Sequence<LoginStateData> = sequenceOf(
        LoginStateData(
            loginState = BiometricViewState.Login(
                usernameField = "",
                passwordField = ""
            )
        ),
        LoginStateData(
            loginState = BiometricViewState.Login(
                usernameField = "Paul Hochon",
                passwordField = "",
                displayLoginFail = true
            )
        ),
        LoginStateData(
            loginState = BiometricViewState.Login(
                usernameField = "Paul Hochon",
                passwordField = "password"
            )
        )
    )
}

/**
 * Data used for the preview of LoginState Composable.
 *
 * @param onUpdateUsername The function called to update the username.
 * @param onUpdatePassword The function called to update the password
 * @param loginState       The [BiometricViewState.Login] emitted from the view model.
 * @param onLoginClick     The function called to log in.
 */
data class LoginStateData(
    val onUpdateUsername: (String) -> Unit = {},
    val onUpdatePassword: (String) -> Unit = {},
    val loginState: BiometricViewState.Login,
    val onLoginClick: () -> Unit = {}
)
