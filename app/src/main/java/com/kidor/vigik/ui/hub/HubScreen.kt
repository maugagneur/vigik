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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.R
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display all sections of the application.
 */
@Composable
fun HubScreen(
    viewModel: HubViewModel = hiltViewModel(),
    navigateToScanTag: () -> Unit = {},
    navigateToTagHistory: () -> Unit = {},
    navigateToEmulateTag: () -> Unit = {}
) {
    viewModel.viewState.observeAsState().let {
        it.value?.let { state -> StateRender(state, viewModel) }
    }
    viewModel.viewEvent.observeAsState().let {
        it.value?.let { eventWrapper ->
            // React on event only once
            eventWrapper.getEventIfNotHandled()?.let { event ->
                EventRender(event, navigateToScanTag, navigateToTagHistory, navigateToEmulateTag)
            }
        }
    }
}

@Composable
private fun StateRender(viewState: HubViewState, viewModel: HubViewModel) {
    if (viewState is HubViewState.Default) {
        DefaultState(DefaultStateData { action -> viewModel.handleAction(action) })
    }
}

@Composable
private fun EventRender(
    viewEvent: HubViewEvent,
    navigateToScanTag: () -> Unit,
    navigateToTagHistory: () -> Unit,
    navigateToEmulateTag: () -> Unit
) {
    when (viewEvent) {
        HubViewEvent.NavigateToEmulateView -> navigateToEmulateTag()
        HubViewEvent.NavigateToHistoryView -> navigateToTagHistory()
        HubViewEvent.NavigateToScanView -> navigateToScanTag()
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
            )
        ,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { defaultStateData.onViewAction(HubViewAction.DisplayScanTagView) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.scan_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = { defaultStateData.onViewAction(HubViewAction.DisplayTagHistoryView) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.history_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = { defaultStateData.onViewAction(HubViewAction.DisplayEmulateTagView) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.emulate_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
    }
}
