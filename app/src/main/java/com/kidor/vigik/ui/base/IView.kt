package com.kidor.vigik.ui.base

interface IView<VIEW_STATE, VIEW_EVENT> {

    /**
     * Defines how the UI must be displayed.
     *
     * Called each time a new state is emitted by the view model.
     *
     * @param viewState The new state of the view.
     */
    fun stateRender(viewState: VIEW_STATE) {
        // Default implementation
    }

    /**
     * Defines how the UI must react to en event.
     *
     * Called each time a new event is emitted by the view model.
     *
     * @param viewEvent The new event.
     */
    fun eventRender(viewEvent: VIEW_EVENT) {
        // Default implementation
    }
}