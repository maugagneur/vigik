package com.kidor.vigik.ui.biometric

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from biometric view.
 */
sealed class BiometricViewAction : ViewAction {

    /**
     * Updates username.
     *
     * @param username The new value of the username.
     */
    data class UpdateUsername(val username: String) : BiometricViewAction()

    /**
     * Updates password.
     *
     * @param password The new value of the password.
     */
    data class UpdatePassword(val password: String) : BiometricViewAction()

    /**
     * Action to log in.
     */
    object Login : BiometricViewAction()

    /**
     * Action to log out.
     */
    object Logout : BiometricViewAction()
}
