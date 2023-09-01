package com.kidor.vigik.ui.camera

import android.net.Uri
import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions for camera view.
 */
sealed class CameraViewAction : ViewAction {
    /**
     * Notifies that camera related permissions are granted.
     */
    data object PermissionGranted : CameraViewAction()

    /**
     * Notifies new captured photo.
     *
     * @param uri The photo's URI.
     */
    data class PhotoCaptured(val uri: Uri) : CameraViewAction()
}
