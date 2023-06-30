package com.kidor.vigik.ui.biometric.home

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.CollectViewEvent
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme

internal const val BIOMETRIC_CREDENTIALS_STATUS_ICON_TEST_TAG = "biometric_credentials_status_icon"

/**
 * View that display the section dedicated to biometric home.
 */
@Composable
fun BiometricHomeScreen(
    viewModel: BiometricHomeViewModel = hiltViewModel(),
    navigateToBiometricLogin: () -> Unit = {}
) {
    ObserveViewState(viewModel) { state ->
        BiometricHome(
            biometricHomeStateData = BiometricHomeStateData(
                isBiometricCredentialSaved = state.isBiometricCredentialsSaved,
                onRemoveCredentialClick = { viewModel.handleAction(BiometricHomeViewAction.RemoveCredentials) },
                onLogoutClick = { viewModel.handleAction(BiometricHomeViewAction.Logout) }
            )
        )
    }

    CollectViewEvent(viewModel) { event ->
        when (event) {
            BiometricHomeViewEvent.NavigateToBiometricLogin -> navigateToBiometricLogin()
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun BiometricHome(
    @PreviewParameter(BiometricHomeStateProvider::class) biometricHomeStateData: BiometricHomeStateData
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.biometric_home_credential_status_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = AppTheme.dimensions.textSizeMedium
            )
            Spacer(modifier = Modifier.width(AppTheme.dimensions.commonSpaceSmall))
            if (biometricHomeStateData.isBiometricCredentialSaved) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Biometric credential saved",
                    modifier = Modifier.testTag(BIOMETRIC_CREDENTIALS_STATUS_ICON_TEST_TAG),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Biometric credential not saved",
                    modifier = Modifier.testTag(BIOMETRIC_CREDENTIALS_STATUS_ICON_TEST_TAG),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        if (biometricHomeStateData.isBiometricCredentialSaved) {
            Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceLarge))
            Button(onClick = { biometricHomeStateData.onRemoveCredentialClick() }) {
                Text(
                    text = stringResource(id = R.string.biometric_home_credential_remove_button_label).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceXXLarge))
        Text(
            text = stringResource(id = R.string.biometric_home_login_success_label),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceLarge))
        Button(onClick = { biometricHomeStateData.onLogoutClick() }) {
            Text(
                text = stringResource(id = R.string.biometric_home_logout_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeLarge
            )
        }
    }
}
