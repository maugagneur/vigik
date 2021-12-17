package com.kidor.vigik.ui.scan

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from scan view.
 */
sealed class ScanViewAction : ViewAction {

    /**
     * Save the read tag in local database.
     */
    object SaveTag : ScanViewAction()
}
