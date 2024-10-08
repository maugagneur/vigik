package com.kidor.vigik.ui.biometric.login

import androidx.biometric.BiometricPrompt.CryptoObject
import com.kidor.vigik.data.crypto.model.CryptoPurpose
import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from biometric login view.
 */
sealed interface BiometricLoginViewAction : ViewAction {

    /**
     * Updates username.
     *
     * @param username The new value of the username.
     */
    data class UpdateUsername(val username: String) : BiometricLoginViewAction

    /**
     * Updates password.
     *
     * @param password The new value of the password.
     */
    data class UpdatePassword(val password: String) : BiometricLoginViewAction

    /**
     * Action to log in.
     */
    data object Login : BiometricLoginViewAction

    /**
     * Action to log in with biometric.
     */
    data object LoginWithBiometric : BiometricLoginViewAction

    /**
     * Hides biometric prompt.
     */
    data object HideBiometricPrompt : BiometricLoginViewAction

    /**
     * Notify an error during biometric authentication.
     *
     * @param purpose The purpose of the authentication.
     */
    data class OnBiometricAuthError(val purpose: CryptoPurpose) : BiometricLoginViewAction

    /**
     * Notifies the success of biometric authentication.
     *
     * @param cryptoObject The [CryptoObject] associated with the authentication.
     * @param purpose      The purpose of the authentication.
     */
    data class OnBiometricAuthSuccess(
        val cryptoObject: CryptoObject,
        val purpose: CryptoPurpose
    ) : BiometricLoginViewAction
}
