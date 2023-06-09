package com.kidor.vigik.ui.biometric

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.ui.base.ObserveViewState

/**
 * View that display the section dedicated to biometric.
 */
@Composable
fun BiometricScreen(
    viewModel: BiometricViewModel = hiltViewModel()
) {
    ObserveViewState(viewModel) { state ->
        DefaultState()
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
private fun DefaultState() {

}
