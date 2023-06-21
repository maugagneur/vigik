package com.kidor.vigik.ui.biometric.login

import android.content.Intent
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.R
import com.kidor.vigik.data.crypto.model.CryptoPurpose
import com.kidor.vigik.extensions.findActivity
import com.kidor.vigik.ui.base.CollectViewEvent
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme
import timber.log.Timber

/**
 * View that display the section dedicated to biometric login.
 */
@Composable
fun BiometricLoginScreen(
    viewModel: BiometricLoginViewModel = hiltViewModel(),
    startBiometricEnrollment: (enrollIntent: Intent) -> Unit = {},
    navigateToBiometricHome: () -> Unit = {}
) {
    ObserveViewState(viewModel) { state ->
        LoginState(
            LoginStateData(
                onUpdateUsername = { viewModel.handleAction(BiometricLoginViewAction.UpdateUsername(it)) },
                onUpdatePassword = { viewModel.handleAction(BiometricLoginViewAction.UpdatePassword(it)) },
                loginState = state,
                onLoginClick = { viewModel.handleAction(BiometricLoginViewAction.Login) },
                onBiometricLoginClick = { viewModel.handleAction(BiometricLoginViewAction.LoginWithBiometric) }
            )
        )
    }

    viewModel.biometricPromptState.collectAsState(null).value?.let {
        BiometricPromptView(
            state = it,
            hidePrompt = { viewModel.handleAction(BiometricLoginViewAction.HideBiometricPrompt) },
            onAuthError = { purpose ->
                viewModel.handleAction(BiometricLoginViewAction.OnBiometricAuthError(purpose))
            },
            onAuthSuccess = { cryptoObject, purpose ->
                viewModel.handleAction(BiometricLoginViewAction.OnBiometricAuthSuccess(cryptoObject, purpose))
            }
        )
    }

    CollectViewEvent(viewModel) { event ->
        when (event) {
            is BiometricLoginViewEvent.DisplayBiometricEnrollment -> startBiometricEnrollment(event.enrollIntent)
            is BiometricLoginViewEvent.NavigateToBiometricHome -> navigateToBiometricHome()
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
private fun LoginState(@PreviewParameter(LoginStateProvider::class) loginStateData: LoginStateData) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = AppTheme.dimensions.commonSpaceXLarge,
                end = AppTheme.dimensions.commonSpaceXLarge
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = loginStateData.loginState.usernameField,
            onValueChange = { loginStateData.onUpdateUsername(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.biometric_username_hint)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceMedium))
        OutlinedTextField(
            value = loginStateData.loginState.passwordField,
            onValueChange = { loginStateData.onUpdatePassword(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.biometric_password_hint)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus(force = true)
                loginStateData.onLoginClick()
            })
        )
        Button(
            onClick = { loginStateData.onLoginClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimensions.commonSpaceLarge)
        ) {
            Text(
                text = stringResource(id = R.string.biometric_login_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeLarge
            )
        }
        BiometricLoginButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimensions.commonSpaceSmall),
            visible = loginStateData.loginState.isBiometricLoginAvailable,
            onClick = { loginStateData.onBiometricLoginClick() }
        )
        if (loginStateData.loginState.displayLoginFail) {
            Row(
                modifier = Modifier.padding(top = AppTheme.dimensions.commonSpaceLarge),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(AppTheme.dimensions.commonSpaceSmall))
                Text(
                    text = stringResource(id = R.string.biometric_login_error_label),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun BiometricLoginButton(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onClick: () -> Unit
) {
    if (visible) {
        OutlinedButton(
            modifier = modifier,
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Biometric login button",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(
                text = stringResource(id = R.string.biometric_login_with_biometric_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeLarge
            )
        }
    }
}

@Composable
private fun BiometricPromptView(
    state: BiometricPromptViewState,
    hidePrompt: () -> Unit = {},
    onAuthError: (purpose: CryptoPurpose) -> Unit = { _: CryptoPurpose -> },
    onAuthSuccess: (cryptoObject: BiometricPrompt.CryptoObject, purpose: CryptoPurpose) -> Unit =
        { _: BiometricPrompt.CryptoObject, _: CryptoPurpose -> }
) {
    val callback = remember(state) {
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                hidePrompt()
                Timber.w("onAuthenticationError(errorCode = $errorCode, errString = $errString)")
                onAuthError(state.purpose)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                hidePrompt()
                val cryptoObject = result.cryptoObject
                if (cryptoObject != null) {
                    onAuthSuccess(cryptoObject, state.purpose)
                } else {
                    onAuthError(state.purpose)
                }
            }
        }
    }

    if (state.isVisible) {
        LocalContext.current.findActivity()?.let {
            val biometricPrompt = BiometricPrompt(it, ContextCompat.getMainExecutor(it), callback)
            biometricPrompt.authenticate(state.promptInfo, state.cryptoObject)
        }
    }
}
