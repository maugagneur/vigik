package com.kidor.vigik.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.kidor.vigik.ui.compose.AppTheme

/**
 * Base class for [Fragment] associated with given [VIEW_MODEL].
 *
 * - You must define the view model:
 * ```
 *      override val viewModel by viewModels<VIEW_MODEL>()
 * ```
 * - If you do not need state or event implementation, use [Nothing] instead.
 * - To react to state changes and events from the view model, override [StateRender] and [EventRender] methods.
 */
abstract class BaseFragment<VIEW_ACTION : ViewAction, VIEW_STATE : ViewState, VIEW_EVENT : ViewEvent,
        VIEW_MODEL : BaseViewModel<VIEW_ACTION, VIEW_STATE, VIEW_EVENT>> : Fragment() {

    protected abstract val viewModel: VIEW_MODEL

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    ComposableView()
                }
            }
        }
    }

    @Composable
    private fun ComposableView() {
        viewModel.viewState.observeAsState().let {
            it.value?.let { state -> StateRender(state) }
        }
        viewModel.viewEvent.observeAsState().let {
            it.value?.let { eventWrapper ->
                // React on events only once
                eventWrapper.getEventIfNotHandled()?.let { event -> EventRender(event) }
            }
        }
    }

    /**
     * Defines how the UI must be displayed.
     *
     * Called each time a new state is emitted by the view model.
     *
     * @param viewState The new state of the view.
     */
    @Composable
    open fun StateRender(viewState: VIEW_STATE) {
        // Default implementation
    }

    /**
     * Defines how the UI must react to en event.
     *
     * Called each time a new event is emitted by the view model.
     *
     * @param viewEvent The new event.
     */
    @Composable
    open fun EventRender(viewEvent: VIEW_EVENT) {
        // Default implementation
    }
}
