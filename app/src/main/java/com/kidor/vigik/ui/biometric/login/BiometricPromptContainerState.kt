package com.kidor.vigik.ui.biometric.login

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

/**
 * Represents the state of the biometric prompt.
 */
class BiometricPromptContainerState {
    private lateinit var _promptInfo: BiometricPrompt.PromptInfo
    private lateinit var _cryptoObject: BiometricPrompt.CryptoObject
    private val _isPromptToShow = mutableStateOf(false)

    val promptInfo: BiometricPrompt.PromptInfo by lazy { _promptInfo }
    val cryptoObject: BiometricPrompt.CryptoObject by lazy { _cryptoObject }
    val isPromptToShow: State<Boolean> = _isPromptToShow

    fun hidePrompt() {
        _isPromptToShow.value = false
    }
}
