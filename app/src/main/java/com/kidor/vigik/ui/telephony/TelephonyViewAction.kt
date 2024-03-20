package com.kidor.vigik.ui.telephony

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions for telephony view.
 */
sealed class TelephonyViewAction : ViewAction {

    /**
     * Notifies that telephony related permissions are granted.
     */
    data object PermissionsGranted : TelephonyViewAction()
}
