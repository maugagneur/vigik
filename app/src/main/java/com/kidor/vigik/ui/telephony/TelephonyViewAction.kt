package com.kidor.vigik.ui.telephony

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions for telephony view.
 */
sealed interface TelephonyViewAction : ViewAction {

    /**
     * Notifies that telephony related permissions are granted.
     */
    data object PermissionsGranted : TelephonyViewAction

    /**
     * Requires to send [message] to [phoneNumber] through SMS.
     *
     * @param phoneNumber The phone number to send SMS to.
     * @param message     The message to send.
     */
    data class SendSms(val phoneNumber: String, val message: String) : TelephonyViewAction
}
