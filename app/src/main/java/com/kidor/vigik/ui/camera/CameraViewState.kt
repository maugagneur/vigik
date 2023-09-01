package com.kidor.vigik.ui.camera

import android.net.Uri
import com.kidor.vigik.ui.base.ViewState

/**
 * Possible states of camera view.
 */
sealed class CameraViewState : ViewState {
    /**
     * State that displays only permission request view.
     */
    data object CheckPermission : CameraViewState()

    /**
     * State that displays the camera.
     */
    data object ShowCamera : CameraViewState()

    /**
     * State that displays the captured photo.
     *
     * @param uri The photo's URI.
     */
    data class ShowCapturedPhoto(val uri: Uri) : CameraViewState()
}
