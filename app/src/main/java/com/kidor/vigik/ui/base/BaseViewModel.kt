package com.kidor.vigik.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kidor.vigik.utils.Event

/**
 * Base class for [ViewModel] which exposes [VIEW_STATE] and [VIEW_EVENT].
 *
 * - If you do not need state or event implementation, use [Nothing] instead.
 * - To publish new state or new event, just do:
 * ```
 *      _viewState.value = VIEW_STATE
 *      ...
 *      _viewEvent.value = Event(VIEW_EVENT)
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
    protected open val _viewEvent = MutableLiveData<Event<VIEW_EVENT>>()
    override val viewEvent: LiveData<Event<VIEW_EVENT>> get() = _viewEvent
}