package com.kidor.vigik.ui.biometric.login

import android.content.Intent
import com.kidor.vigik.ui.base.ViewEvent

/**
 * Events that can be sent to the view from [BiometricLoginViewModel].
 */
sealed interface BiometricLoginViewEvent : ViewEvent {

    /**
     * Event when we should display the system's biometric enrollment.
     *
     * @param enrollIntent The intent to start.
     */
    data class DisplayBiometricEnrollment(val enrollIntent: Intent) : BiometricLoginViewEvent

    /**
     * Event when we have to navigate to biometric home view.
     */
    data object NavigateToBiometricHome : BiometricLoginViewEvent
}
