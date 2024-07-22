package com.kidor.vigik.ui.biometric.home

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from biometric home view.
 */
sealed interface BiometricHomeViewAction : ViewAction {

    /**
     * Action to remove biometric credentials.
     */
    data object RemoveCredentials : BiometricHomeViewAction

    /**
     * Action to log out.
     */
    data object Logout : BiometricHomeViewAction
}
