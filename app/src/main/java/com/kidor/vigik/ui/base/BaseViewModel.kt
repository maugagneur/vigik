package com.kidor.vigik.ui.base

import androidx.annotation.VisibleForTesting
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
abstract class BaseViewModel<VIEW_STATE : ViewState, VIEW_EVENT : ViewEvent> : ViewModel(),
    IViewModel<VIEW_STATE, VIEW_EVENT> {

    /**
     * Internal state holder that can be modify by the view model.
     */
    @Suppress("PropertyName")
    protected open val _viewState = MutableLiveData<VIEW_STATE>()
    override val viewState: LiveData<VIEW_STATE> get() = _viewState

    /**
     * Internal event holder that can be modify by the view model.
     */
    @Suppress("PropertyName")
    protected open val _viewEvent = MutableLiveData<EventWrapper<VIEW_EVENT>>()
    override val viewEvent: LiveData<EventWrapper<VIEW_EVENT>> get() = _viewEvent

    /**
     * Forces the view model to emit the given state.
     *
     * Only use this method for testing purpose!
     *
     * @param viewState The state.
     */
    @VisibleForTesting
    internal fun forceState(viewState: VIEW_STATE) {
        _viewState.value = viewState
    }

    /**
     * Forces the view model to emit the given event.
     *
     * Only use this method for testing purpose!
     *
     * @param viewEvent The event.
     */
    @VisibleForTesting
    internal fun forceEvent(viewEvent: VIEW_EVENT) {
        _viewEvent.value = viewEvent.wrap()
    }
}