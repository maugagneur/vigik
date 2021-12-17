package com.kidor.vigik.ui.check

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from check view.
 */
sealed class CheckViewAction : ViewAction {

    /**
     * Refresh NFC status.
     */
    object RefreshNfcStatus : CheckViewAction()

    /**
     * Display device's NFC settings screen.
     */
    object DisplayNfcSettings : CheckViewAction()
}
