package com.kidor.vigik.ui.biometric.home

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from biometric home view.
 */
sealed class BiometricHomeViewAction : ViewAction {

    /**
     * Action to log out.
     */
    object Logout : BiometricHomeViewAction()
}
