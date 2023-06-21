package com.kidor.vigik.data.biometric

import android.content.Intent
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import com.kidor.vigik.data.biometric.model.BiometricInfo
import com.kidor.vigik.data.crypto.model.CryptoPurpose

/**
 * Interface of a biometric repository.
 */
interface BiometricRepository {

    /**
     * Gets information about biometric features and state on this device.
     */
    suspend fun getBiometricInfo(): BiometricInfo

    /**
     * Returns the [Intent] to start to display biometrics enrollment settings.
     */
    fun getBiometricEnrollIntent(): Intent

    /**
     * Returns the [BiometricPrompt.PromptInfo] for the given purpose.
     *
     * @param purpose The biometric prompt purpose.
     */
    fun getBiometricPromptInfo(purpose: CryptoPurpose): BiometricPrompt.PromptInfo

    /**
     * Creates a new [CryptoObject] instance for encryption purpose.
     */
    fun getCryptoObjectForEncryption(): CryptoObject
}
