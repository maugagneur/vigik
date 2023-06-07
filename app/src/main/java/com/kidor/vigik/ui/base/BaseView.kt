package com.kidor.vigik.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState

/**
 * Starts observing states sent from the [viewModel].
 */
@Composable
fun <VIEW_ACTION : ViewAction, VIEW_STATE : ViewState, VIEW_EVENT : ViewEvent> ObserveViewState(
    viewModel: BaseViewModel<VIEW_ACTION, VIEW_STATE, VIEW_EVENT>,
    stateRender: @Composable (VIEW_STATE) -> Unit
) {
    viewModel.viewState.observeAsState().let {
        it.value?.let { state -> stateRender(state) }
    }
}

/**
 * Starts collecting events sent from the [viewModel].
 */
@Composable
fun <VIEW_ACTION : ViewAction, VIEW_STATE : ViewState, VIEW_EVENT : ViewEvent> CollectViewEvent(
    viewModel: BaseViewModel<VIEW_ACTION, VIEW_STATE, VIEW_EVENT>,
    eventHandler: (VIEW_EVENT) -> Unit
) {
    LaunchedEffect(true) {
        viewModel.viewEvent.collect { event -> eventHandler(event) }
    }
}
