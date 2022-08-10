package com.kidor.vigik.ui.check

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.BaseFragment
import com.kidor.vigik.ui.compose.AppTheme
import dagger.hilt.android.AndroidEntryPoint

internal const val PROGRESS_BAR_TEST_TAG = "Progress bar"

/**
 * View that check if all prerequisite to use NFC are met.
 */
@AndroidEntryPoint
class CheckFragment : BaseFragment<CheckViewAction, CheckViewState, CheckViewEvent, CheckViewModel>() {

    override val viewModel by viewModels<CheckViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // Wait until the view model is ready to dismiss the splashscreen
                return if (viewModel.isReady) {
                    view.viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    false
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        viewModel.handleAction(CheckViewAction.RefreshNfcStatus)
    }

    @Composable
    override fun StateRender(viewState: CheckViewState) {
        when (viewState) {
            CheckViewState.Loading -> LoadingState()
            CheckViewState.NfcIsDisable ->
                NfcIsDisableState(NfcIsDisableStateData { action -> viewModel.handleAction(action) })
        }
    }

    @Composable
    override fun EventRender(viewEvent: CheckViewEvent) {
        when (viewEvent) {
            CheckViewEvent.NavigateToHub -> findNavController().navigate(CheckFragmentDirections.goToHub())
            CheckViewEvent.NavigateToSettings -> startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun LoadingState() {
    Box(
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
internal fun NfcIsDisableState(@PreviewParameter(NfcIsDisableStateProvider::class) nfcIsDisableStateData: NfcIsDisableStateData) {
    Column(
        modifier = Modifier.padding(
            start = AppTheme.dimensions.commonSpaceXLarge,
            end = AppTheme.dimensions.commonSpaceXLarge
        ),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { nfcIsDisableStateData.onViewAction(CheckViewAction.RefreshNfcStatus) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.nfc_state_refresh).uppercase(),
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
            onClick = { nfcIsDisableStateData.onViewAction(CheckViewAction.DisplayNfcSettings) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.nfc_settings).uppercase(),
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
