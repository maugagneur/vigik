package com.kidor.vigik.ui.biometric.login

import android.content.Intent
import com.kidor.vigik.ui.base.ViewEvent

/**
 * Events that can be sent to the view from [BiometricLoginViewModel].
 */
sealed class BiometricLoginViewEvent : ViewEvent() {

    /**
     * Event when we should display the system's biometric enrollment.
     */
    data class DisplayBiometricEnrollment(val enrollIntent: Intent) : BiometricLoginViewEvent()

    /**
     * Event when we have to navigate to biometric home view.
     */
    object NavigateToBiometricHome : BiometricLoginViewEvent()
}
