package com.kidor.vigik.ui.check

sealed class CheckViewEvent {

    /**
     * Event when we have to navigate to phone's Settings.
     */
    object NavigateToSettings: CheckViewEvent()

    /**
     * Event when we have to navigate to Hub view.
     */
    object NavigateToHub: CheckViewEvent()

    override fun toString(): String = javaClass.simpleName
}