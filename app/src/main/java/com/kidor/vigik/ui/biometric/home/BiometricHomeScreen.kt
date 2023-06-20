package com.kidor.vigik.ui.biometric.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.CollectViewEvent
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display the section dedicated to biometric home.
 */
@Composable
fun BiometricHomeScreen(
    viewModel: BiometricHomeViewModel = hiltViewModel(),
    navigateToBiometricLogin: () -> Unit = {}
) {
    BiometricHome(
        loggedStateData = BiometricHomeStateData(
            onLogoutClick = { viewModel.handleAction(BiometricHomeViewAction.Logout) }
        )
    )

    CollectViewEvent(viewModel) { event ->
        when (event) {
            BiometricHomeViewEvent.NavigateToBiometricLogin -> navigateToBiometricLogin()
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
fun BiometricHome(@PreviewParameter(BiometricHomeStateProvider::class) loggedStateData: BiometricHomeStateData) {
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