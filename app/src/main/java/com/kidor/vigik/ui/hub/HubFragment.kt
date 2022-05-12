package com.kidor.vigik.ui.hub

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.BaseFragment
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display all sections of the application.
 */
class HubFragment : BaseFragment<HubViewAction, HubViewState, HubViewEvent, HubViewModel>() {

    override val viewModel by viewModels<HubViewModel>()

    override val isComposeView: Boolean = true

    @Composable
    override fun StateRender(viewState: HubViewState) {
        if (viewState is HubViewState.Default) {
            DefaultState { action -> viewModel.handleAction(action) }
        }
    }

    @Composable
    override fun EventRender(viewEvent: HubViewEvent) {
        when (viewEvent) {
            HubViewEvent.NavigateToEmulateView -> navigateTo(HubFragmentDirections.goToEmulateTag())
            HubViewEvent.NavigateToHistoryView -> navigateTo(HubFragmentDirections.goToTagHistory())
            HubViewEvent.NavigateToScanView -> navigateTo(HubFragmentDirections.goToScanNfc())
        }
    }

    private fun navigateTo(direction: NavDirections) {
        findNavController().navigate(direction)
    }
}

@Composable
@VisibleForTesting
internal fun DefaultState(onViewAction: (HubViewAction) -> Unit) {
    Column(
        modifier = Modifier.padding(
            start = AppTheme.dimensions.commonSpaceXLarge,
            end = AppTheme.dimensions.commonSpaceXLarge
        ),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onViewAction(HubViewAction.DisplayScanTagView) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.scan_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = { onViewAction(HubViewAction.DisplayTagHistoryView) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.history_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = { onViewAction(HubViewAction.DisplayEmulateTagView) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.emulate_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
    }
}
