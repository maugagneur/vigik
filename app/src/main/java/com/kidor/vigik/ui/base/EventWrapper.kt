package com.kidor.vigik.ui.base

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
class EventWrapper<out T : ViewEvent>(private val event: T) {

    private var hasBeenHandled = false

    /**
     * Returns the event only the first time (as it is consumed) and prevents its use again.
     */
    fun getEventIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            event
        }
    }

    /**
     * Returns the event, even if it's already been handled.
     */
    fun peekEvent(): T = event
}