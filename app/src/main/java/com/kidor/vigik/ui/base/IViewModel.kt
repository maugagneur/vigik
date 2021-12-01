package com.kidor.vigik.ui.base

import androidx.lifecycle.LiveData
import com.kidor.vigik.utils.Event

interface IViewModel<VIEW_STATE, VIEW_EVENT> {

    /**
     * Observe this to be notify of every view state's changes.
     */
    val viewState: LiveData<VIEW_STATE>

    /**
     * Observe this to be notify of every view event.
     */
    val viewEvent: LiveData<Event<VIEW_EVENT>>
}