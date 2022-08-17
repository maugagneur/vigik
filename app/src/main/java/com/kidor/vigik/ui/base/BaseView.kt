package com.kidor.vigik.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState

/**
 * Starts observing the states sent from the [viewModel].
 */
@Composable
fun <VIEW_ACTION: ViewAction, VIEW_STATE: ViewState, VIEW_EVENT: ViewEvent> ObserveViewState(
    viewModel: BaseViewModel<VIEW_ACTION, VIEW_STATE, VIEW_EVENT>,
    stateRender: @Composable (VIEW_STATE) -> Unit
) {
    viewModel.viewState.observeAsState().let {
        it.value?.let { state -> stateRender(state) }
    }
}

/**
 * Starts observing the events sent from the [viewModel].
 */
@Composable
fun <VIEW_ACTION: ViewAction, VIEW_STATE: ViewState, VIEW_EVENT: ViewEvent> ObserveViewEvent(
    viewModel: BaseViewModel<VIEW_ACTION, VIEW_STATE, VIEW_EVENT>,
    eventRender: @Composable (VIEW_EVENT) -> Unit
) {
    viewModel.viewEvent.observeAsState().let {
        it.value?.let { eventWrapper ->
            // React on events only once
            eventWrapper.getEventIfNotHandled()?.let { event -> eventRender(event) }
        }
    }
}
