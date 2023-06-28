package com.kidor.vigik.ui.hub

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display all sections of the application.
 */
@Composable
fun HubScreen(
    viewModel: HubViewModel = hiltViewModel(),
    navigateToScanTag: () -> Unit = {},
    navigateToTagHistory: () -> Unit = {},
    navigateToEmulateTag: () -> Unit = {},
    navigateToBiometric: () -> Unit = {},
    navigateToRestApi: () -> Unit = {}
) {
    ObserveViewState(viewModel) { state ->
        if (state is HubViewState.Default) {
            DefaultState(
                DefaultStateData(
                    onScanClick = { viewModel.handleAction(HubViewAction.DisplayScanTagView) },
                    onHistoryClick = { viewModel.handleAction(HubViewAction.DisplayTagHistoryView) },
                    onEmulateClick = { viewModel.handleAction(HubViewAction.DisplayEmulateTagView) },
                    onBiometricClick = { viewModel.handleAction(HubViewAction.DisplayBiometricView) },
                    onRestApiClick = { viewModel.handleAction(HubViewAction.DisplayRestApiView) }
                )
            )
        }
    }
    CollectViewEvent(viewModel) { event ->
        when (event) {
            HubViewEvent.NavigateToEmulateView -> navigateToEmulateTag()
            HubViewEvent.NavigateToHistoryView -> navigateToTagHistory()
            HubViewEvent.NavigateToScanView -> navigateToScanTag()
            HubViewEvent.NavigateToBiometricView -> navigateToBiometric()
            HubViewEvent.NavigateToRestApiView -> navigateToRestApi()
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun DefaultState(@PreviewParameter(DefaultStateProvider::class) defaultStateData: DefaultStateData) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(
                start = AppTheme.dimensions.commonSpaceXLarge,
                end = AppTheme.dimensions.commonSpaceXLarge
            ),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = defaultStateData.onScanClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.scan_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = defaultStateData.onHistoryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.history_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = defaultStateData.onEmulateClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.emulate_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = defaultStateData.onBiometricClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.biometric_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = defaultStateData.onRestApiClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.rest_api_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
    }
}
