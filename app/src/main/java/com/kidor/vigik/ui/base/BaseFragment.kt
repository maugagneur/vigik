package com.kidor.vigik.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment

/**
 * Base class for [Fragment] associated with given [VIEW_MODEL].
 *
 * - You must define the view model:
 * ```
 *      override val viewModel by viewModels<VIEW_MODEL>()
 * ```
 * - If you do not need state or event implementation, use [Nothing] instead.
 * - To react to state changes and events from the view model, override methods [stateRender] and [eventRender].
 */
abstract class BaseFragment<VIEW_ACTION : ViewAction, VIEW_STATE : ViewState, VIEW_EVENT : ViewEvent,
        VIEW_MODEL : BaseViewModel<VIEW_ACTION, VIEW_STATE, VIEW_EVENT>> : Fragment() {

    protected abstract val viewModel: VIEW_MODEL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observe(viewLifecycleOwner) { stateRender(it) }
        viewModel.viewEvent.observe(viewLifecycleOwner) { eventWrapper ->
            // React on events only once
            eventWrapper.getEventIfNotHandled()?.let { event -> eventRender(event) }
        }
    }

    /**
     * Defines how the UI must be displayed.
     *
     * Called each time a new state is emitted by the view model.
     *
     * @param viewState The new state of the view.
     */
    protected open fun stateRender(viewState: VIEW_STATE) {
        // Default implementation
    }

    /**
     * Defines how the UI must react to en event.
     *
     * Called each time a new event is emitted by the view model.
     *
     * @param viewEvent The new event.
     */
    protected open fun eventRender(viewEvent: VIEW_EVENT) {
        // Default implementation
    }

    /**
     * Forces the view to go to the given state.
     *
     * Only use this method for testing purpose!
     *
     * @param viewState The state.
     */
    @VisibleForTesting
    internal fun forceState(viewState: VIEW_STATE) {
        viewModel.forceState(viewState)
    }

    /**
     * Forces the view to react to the given event.
     *
     * Only use this method for testing purpose!
     *
     * @param viewEvent The event.
     */
    @VisibleForTesting
    internal fun forceEvent(viewEvent: VIEW_EVENT) {
        viewModel.forceEvent(viewEvent)
    }
}