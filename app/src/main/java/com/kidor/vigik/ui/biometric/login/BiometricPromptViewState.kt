package com.kidor.vigik.ui.biometric.login

import androidx.biometric.BiometricPrompt

/**
 * Represents the state of the biometric prompt.
 */
data class BiometricPromptViewState(
    val isVisible: Boolean,
    val promptInfo: BiometricPrompt.PromptInfo,
    val cryptoObject: BiometricPrompt.CryptoObject
)
