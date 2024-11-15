package com.kidor.vigik.ui.compass

import com.google.maps.android.compose.CameraPositionState
import com.kidor.vigik.ui.base.ViewState

/**
 * State of compass view.
 *
 * @param cameraPositionState Map's camera state.
 */
data class CompassViewState(
    val cameraPositionState: CameraPositionState
) : ViewState
