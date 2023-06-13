package com.kidor.vigik.ui.biometric

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display the section dedicated to biometric.
 */
@Composable
fun BiometricScreen(viewModel: BiometricViewModel = hiltViewModel()) {
    ObserveViewState(viewModel) { state ->
        when (state) {
            is BiometricViewState.DisplayBiometricStatus -> BiometricStatus(state.status)
            is BiometricViewState.Login -> LoginState(
                LoginStateData(
                    onUpdateUsername = { viewModel.handleAction(BiometricViewAction.UpdateUsername(it)) },
                    onUpdatePassword = { viewModel.handleAction(BiometricViewAction.UpdatePassword(it)) },
                    loginState = state,
                    onLoginClick = { viewModel.handleAction(BiometricViewAction.Login) }
                )
            )
            is BiometricViewState.Logged -> LoggedState(
                loggedStateData = LoggedStateData(
                    onLogoutClick = { viewModel.handleAction(BiometricViewAction.Logout) }
                )
            )
        }
    }
}

@Composable
private fun BiometricStatus(status: BiometricAuthenticateStatus) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.name,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
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
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceLarge))
        Button(
            onClick = { loginStateData.onLoginClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.biometric_login_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeLarge
            )
        }
        if (loginStateData.loginState.displayLoginFail) {
            Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceLarge))
            Row(
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
@Preview(widthDp = 400, heightDp = 700)
private fun LoggedState(@PreviewParameter(LoggedStateProvider::class) loggedStateData: LoggedStateData) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.biometric_login_success_label),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceLarge))
        Button(onClick = { loggedStateData.onLogoutClick() }) {
            Text(
                text = stringResource(id = R.string.biometric_logout_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeLarge
            )
        }
    }
}
