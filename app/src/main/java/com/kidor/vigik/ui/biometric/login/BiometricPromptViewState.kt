package com.kidor.vigik.ui.biometric.login

import androidx.biometric.BiometricPrompt
import com.kidor.vigik.data.crypto.model.CryptoPurpose

/**
 * Represents the state of the biometric prompt.
 *
 * @param isVisible    True if the biometric prompt should be visible, otherwise false.
 * @param promptInfo   The info to display on biometric prompt.
 * @param cryptoObject The [BiometricPrompt.CryptoObject] associated with the authentication.
 * @param purpose      The purpose of the authentication.
 */
data class BiometricPromptViewState(
    val isVisible: Boolean,
    val promptInfo: BiometricPrompt.PromptInfo,
    val cryptoObject: BiometricPrompt.CryptoObject,
    val purpose: CryptoPurpose
)
