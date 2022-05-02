package com.kidor.vigik.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Base class for [ViewModel] which exposes [VIEW_STATE] and [VIEW_EVENT].
 *
 * - If you do not need state or event implementation, use [Nothing] instead.
 * - To publish new state or new event, just do:
 * ```
 *      _viewState.value = VIEW_STATE
 *      ...
 *      _viewEvent.value = VIEW_EVENT.wrap()
 * ```
 */
open class BaseViewModel<VIEW_ACTION : ViewAction, VIEW_STATE : ViewState, VIEW_EVENT : ViewEvent> : ViewModel() {

    /**
     * Internal state holder that can be modify by the view model.
     */
    @Suppress("PropertyName", "VariableNaming")
    protected open val _viewState = MutableLiveData<VIEW_STATE>()

    /**
     * Observe this to be notify of every view state's changes.
     */
    val viewState: LiveData<VIEW_STATE> get() = _viewState

    /**
     * Internal event holder that can be modify by the view model.
     */
    @Suppress("PropertyName", "VariableNaming")
    protected open val _viewEvent = MutableLiveData<EventWrapper<VIEW_EVENT>>()

    /**
     * Observe this to be notify of every view event.
     */
    val viewEvent: LiveData<EventWrapper<VIEW_EVENT>> get() = _viewEvent

    /**
     * Defines how the ViewModel should react to en action from the view.
     *
     * Called each time a new action is emitted from the view.
     *
     * @param viewAction The action to handle.
     */
    open fun handleAction(viewAction: VIEW_ACTION) {
        // Default implementation
    }
}
