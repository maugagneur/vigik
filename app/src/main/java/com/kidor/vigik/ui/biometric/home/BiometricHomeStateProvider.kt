package com.kidor.vigik.ui.biometric.home

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Provides a set of data for the preview of [BiometricHome].
 */
class BiometricHomeStateProvider : PreviewParameterProvider<BiometricHomeStateData> {
    override val values: Sequence<BiometricHomeStateData> = sequenceOf(
        BiometricHomeStateData(false),
        BiometricHomeStateData(true)
    )
}

/**
 * Data used for the preview of [BiometricHome].
 *
 * @param isBiometricCredentialSaved True if some biometric credentials are saved, otherwise false.
 * @param onRemoveCredentialClick    The function called to remove biometric credentials.
 * @param onLogoutClick              The function called on logout.
 */
data class BiometricHomeStateData(
    val isBiometricCredentialSaved: Boolean,
    val onRemoveCredentialClick: () -> Unit = {},
    val onLogoutClick: () -> Unit = {}
)
