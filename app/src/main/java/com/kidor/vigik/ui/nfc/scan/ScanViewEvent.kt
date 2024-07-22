package com.kidor.vigik.ui.nfc.scan

import com.kidor.vigik.ui.base.ViewEvent

/**
 * Events that can be sent to the view from [ScanViewModel].
 */
sealed interface ScanViewEvent : ViewEvent {

    /**
     * Event when a tag is successfully saved into local database.
     */
    data object SaveTagSuccess : ScanViewEvent

    /**
     * Event when we fail to save a tag into local database.
     */
    data object SaveTagFailure : ScanViewEvent
}
