package com.kidor.vigik.ui.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * Base class for [Fragment] associated with given [VIEW_MODEL].
 *
 * - You must define the view model.
 * ```
 *      override val viewModel by viewModels<VIEW_MODEL>()
 * ```
 * - If you do not need state or event implementation, use [Nothing] instead.
 * - To react to state changes and events from the view model, override methods [stateRender] and [eventRender].
 */
abstract class BaseFragment<VIEW_STATE : ViewState, VIEW_EVENT : ViewEvent, VIEW_MODEL : BaseViewModel<VIEW_STATE, VIEW_EVENT>> :
    Fragment(), IView<VIEW_STATE, VIEW_EVENT> {

    protected abstract val viewModel: VIEW_MODEL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observe(this) { stateRender(it) }
        viewModel.viewEvent.observe(this) { eventWrapper ->
            // React on events only once
            eventWrapper.getEventIfNotHandled()?.let { event -> eventRender(event) }
        }
    }
}