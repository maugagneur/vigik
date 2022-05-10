package com.kidor.vigik.ui.hub

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.BaseFragment
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display all sections of the application.
 */
class HubFragment : BaseFragment<HubViewAction, Nothing, HubViewEvent, HubViewModel>() {

    override val viewModel by viewModels<HubViewModel>()

    @Composable
    @Preview(widthDp = 400, heightDp = 700)
    override fun ComposableView() {
        Column(
            modifier = Modifier.padding(
                start = AppTheme.dimensions.commonSpaceXLarge,
                end = AppTheme.dimensions.commonSpaceXLarge
            ),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { viewModel.handleAction(HubViewAction.DisplayScanTagView) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.scan_button_label).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeMedium
                )
            }
            Button(
                onClick = { viewModel.handleAction(HubViewAction.DisplayTagHistoryView) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.history_button_label).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeMedium
                )
            }
            Button(
                onClick = { viewModel.handleAction(HubViewAction.DisplayEmulateTagView) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.emulate_button_label).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeMedium
                )
            }
        }
    }

    override fun eventRender(viewEvent: HubViewEvent) {
        when (viewEvent) {
            HubViewEvent.NavigateToEmulateView -> navigateTo(HubFragmentDirections.goToEmulateTag())
            HubViewEvent.NavigateToHistoryView -> navigateTo(HubFragmentDirections.goToTagHistory())
            HubViewEvent.NavigateToScanView -> navigateTo(HubFragmentDirections.goToScanNfc())
        }
    }

    private fun navigateTo(direction: NavDirections) {
        Navigation.findNavController(requireView()).navigate(direction)
    }
}
