package com.kidor.vigik.ui.biometric.login

import androidx.biometric.BiometricPrompt.CryptoObject
import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from biometric view.
 */
sealed class BiometricLoginViewAction : ViewAction {

    /**
     * Updates username.
     *
     * @param username The new value of the username.
     */
    data class UpdateUsername(val username: String) : BiometricLoginViewAction()

    /**
     * Updates password.
     *
     * @param password The new value of the password.
     */
    data class UpdatePassword(val password: String) : BiometricLoginViewAction()

    /**
     * Action to log in.
     */
    object Login : BiometricLoginViewAction()

    /**
     * TODO
     */
    data class OnBiometricAuthError(val code: Int, val message: String) : BiometricLoginViewAction()

    /**
     * TODO
     */
    data class OnBiometricAuthSuccess(val cryptoObject: CryptoObject) : BiometricLoginViewAction()
}
