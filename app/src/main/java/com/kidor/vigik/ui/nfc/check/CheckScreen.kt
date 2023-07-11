package com.kidor.vigik.ui.nfc.check

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.CollectViewEvent
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme

internal const val PROGRESS_BAR_TEST_TAG = "Progress bar"

/**
 * View that check if all prerequisite to use NFC are met.
 */
@Composable
fun CheckScreen(
    viewModel: CheckViewModel = hiltViewModel(),
    navigateToHub: () -> Unit = {},
    navigateToSettings: () -> Unit = {}
) {
    ObserveViewState(viewModel) { state ->
        when (state) {
            CheckViewState.Loading -> LoadingState()
            CheckViewState.NfcIsDisable -> NfcIsDisableState(
                NfcIsDisableStateData(
                    onRefreshClick = { viewModel.handleAction(CheckViewAction.RefreshNfcStatus) },
                    onSettingsClick = { viewModel.handleAction(CheckViewAction.DisplayNfcSettings) }
                )
            )
        }
    }
    CollectViewEvent(viewModel) { event ->
        when (event) {
            CheckViewEvent.NavigateToHub -> navigateToHub()
            CheckViewEvent.NavigateToSettings -> navigateToSettings()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.handleAction(CheckViewAction.RefreshNfcStatus)
            }
        }
        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)
        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.testTag(PROGRESS_BAR_TEST_TAG),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun NfcIsDisableState(
    @PreviewParameter(NfcIsDisableStateProvider::class) nfcIsDisableStateData: NfcIsDisableStateData
) {
    Column(
        modifier = Modifier
            .padding(start = AppTheme.dimensions.commonSpaceXLarge, end = AppTheme.dimensions.commonSpaceXLarge)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = nfcIsDisableStateData.onRefreshClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.nfc_refresh_button_label).uppercase(),
                modifier = Modifier.weight(1f),
                fontSize = AppTheme.dimensions.textSizeMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(ButtonDefaults.IconSize))
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Refresh"
            )
        }
        Button(
            onClick = nfcIsDisableStateData.onSettingsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.nfc_settings_button_label).uppercase(),
                modifier = Modifier.weight(1f),
                fontSize = AppTheme.dimensions.textSizeMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(ButtonDefaults.IconSize))
            Icon(
                Icons.Default.Settings,
                contentDescription = "Refresh"
            )
        }
    }
}
