package com.kidor.vigik.data.biometric.model

import com.kidor.vigik.data.crypto.model.CryptoKeyStatus

/**
 * Contains information about biometric authentication capability.
 *
 * @param biometricTokenIsPresent       True if a biometric token is memorized, otherwise false.
 * @param biometricAuthenticationStatus Status of biometric authentication.
 * @param cryptoKeyStatus               Status of cryptographic key.
 */
data class BiometricInfo(
    val biometricTokenIsPresent: Boolean,
    val biometricAuthenticationStatus: BiometricAuthenticationStatus,
    private val cryptoKeyStatus: CryptoKeyStatus?
) {
    /**
     * Returns true if the device is ready to perform biometric authentication and if the crypto key is ready to be
     * used, otherwise returns false.
     */
    fun isAuthenticationAvailable(): Boolean =
        biometricAuthenticationStatus == BiometricAuthenticationStatus.READY
                && cryptoKeyStatus == CryptoKeyStatus.READY
}
