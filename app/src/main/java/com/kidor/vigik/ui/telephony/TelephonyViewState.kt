package com.kidor.vigik.ui.telephony

import com.kidor.vigik.ui.base.ViewState

/**
 * Possible states of telephony view.
 */
sealed class TelephonyViewState : ViewState {

    /**
     * State that displays only permission request view.
     */
    data object CheckPermission : TelephonyViewState()

    /**
     * State that display the telephony data of the device.
     *
     * @param totalContactNumber  Total number of contacts on the device.
     * @param mobileContactNumber Number of mobile contacts on the device.
     * @param totalSmsNumber      Total number of SMS on the device.
     */
    data class ShowData(
        val totalContactNumber: Int? = null,
        val mobileContactNumber: Int? = null,
        val totalSmsNumber: Int? = null
    ) : TelephonyViewState()
}
