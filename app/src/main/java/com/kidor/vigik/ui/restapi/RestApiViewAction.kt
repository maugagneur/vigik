package com.kidor.vigik.ui.restapi

import com.kidor.vigik.data.apes.AvailabilityPreset
import com.kidor.vigik.ui.base.ViewAction

/**
 * User actions on the REST API (APES) screen.
 */
sealed class RestApiViewAction : ViewAction {

    /**
     * Refreshes all APES data (voice, availability, status now, me).
     */
    data object RefreshData : RestApiViewAction()

    /**
     * Saves a new user API Bearer token.
     *
     * @param token The Bearer token.
     */
    data class SaveUserToken(val token: String) : RestApiViewAction()

    /**
     * Clears the stored user API token.
     */
    data object ClearUserToken : RestApiViewAction()

    /**
     * Applies a quick availability preset for tonight.
     *
     * @param preset The chosen preset.
     */
    data class ApplyPreset(val preset: AvailabilityPreset) : RestApiViewAction()

    /**
     * Saves user preset start and end times for tonight.
     *
     * @param startTime The start time in "HH:mm" format.
     * @param endTime   The end time in "HH:mm" format.
     */
    data class SavePresetTimes(val startTime: String, val endTime: String) : RestApiViewAction()

    /**
     * Dismisses displayed snackbar/message.
     */
    data object DismissMessage : RestApiViewAction()
}
