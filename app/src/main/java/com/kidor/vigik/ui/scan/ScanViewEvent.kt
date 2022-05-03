package com.kidor.vigik.ui.scan

import com.kidor.vigik.ui.base.ViewEvent

/**
 * Events that can be sent to [ScanFragment] from [ScanViewModel].
 */
sealed class ScanViewEvent : ViewEvent() {

    /**
     * Event when a tag is successfully saved into local database.
     */
    object SaveTagSuccess : ScanViewEvent()

    /**
     * Event when we fail to save a tag into local database.
     */
    object SaveTagFailure : ScanViewEvent()
}
