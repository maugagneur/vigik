package com.kidor.vigik.ui.scan

sealed class ScanViewEvent {

    /**
     * Event when a tag is successfully saved into local database.
     */
    object SaveTagSuccess: ScanViewEvent()

    /**
     * Event when we fail to save a tag into local database.
     */
    object SaveTagFailure: ScanViewEvent()

    override fun toString(): String = javaClass.simpleName
}