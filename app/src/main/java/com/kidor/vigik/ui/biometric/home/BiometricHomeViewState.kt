package com.kidor.vigik.ui.biometric.home

import com.kidor.vigik.ui.base.ViewState

/**
 * State that displays the biometric home screen.
 *
 * @param isBiometricCredentialsSaved Indicates if biometric credentials are saved.
 */
data class BiometricHomeViewState(val isBiometricCredentialsSaved: Boolean) : ViewState
