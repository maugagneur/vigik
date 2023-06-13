package com.kidor.vigik.ui.biometric

/**
 * Status of biometric.
 */
enum class BiometricAuthenticateStatus {
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
