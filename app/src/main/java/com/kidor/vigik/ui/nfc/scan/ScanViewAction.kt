package com.kidor.vigik.ui.nfc.scan

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from scan view.
 */
sealed interface ScanViewAction : ViewAction {

    /**
     * Save the read tag in local database.
     */
    data object SaveTag : ScanViewAction
}
