package com.kidor.vigik.ui.nfc.check

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from check view.
 */
sealed interface CheckViewAction : ViewAction {

    /**
     * Refresh NFC status.
     */
    data object RefreshNfcStatus : CheckViewAction

    /**
     * Display device's NFC settings screen.
     */
    data object DisplayNfcSettings : CheckViewAction
}
