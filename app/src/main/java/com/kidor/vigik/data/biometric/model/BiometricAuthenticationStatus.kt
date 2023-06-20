package com.kidor.vigik.data.biometric.model

/**
 * Status of biometric authentication.
 */
enum class BiometricAuthenticationStatus {
    /**
     * App can authenticate using biometrics.
     */
    READY,

    /**
     * No biometric features available on this device.
     */
    NOT_AVAILABLE,

    /**
     * Biometric features are currently unavailable.
     */
    TEMPORARY_NOT_AVAILABLE,

    /**
     * Biometric support is available, but no biometry has enrolled.
     */
    AVAILABLE_BUT_NOT_ENROLLED
}
