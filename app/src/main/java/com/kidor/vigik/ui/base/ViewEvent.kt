package com.kidor.vigik.ui.base

/**
 * Generic view event.
 */
open class ViewEvent {

    /**
     * Wraps this [ViewEvent] into [EventWrapper].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : ViewEvent> wrap(): EventWrapper<T> = EventWrapper(this as T)

    override fun toString(): String = javaClass.simpleName
}
