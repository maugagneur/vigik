package com.kidor.vigik.ui.biometric.home

import com.kidor.vigik.ui.base.ViewEvent

/**
 * Events that can be sent to the view from [BiometricHomeViewModel].
 */
sealed class BiometricHomeViewEvent : ViewEvent {

    /**
     * Event when we have to navigate to biometric login view.
     */
    data object NavigateToBiometricLogin : BiometricHomeViewEvent()
}
