package com.kidor.vigik.ui.base

import androidx.lifecycle.LiveData

interface IViewModel<VIEW_STATE : ViewState, VIEW_EVENT : ViewEvent> {

    /**
     * Observe this to be notify of every view state's changes.
     */
    val viewState: LiveData<VIEW_STATE>

    /**
     * Observe this to be notify of every view event.
     */
    val viewEvent: LiveData<EventWrapper<VIEW_EVENT>>
}