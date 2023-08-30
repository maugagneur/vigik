package com.kidor.vigik.ui.camera

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.kidor.vigik.R
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display the section dedicated to camera.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.dimensions.commonSpaceXLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PermissionView()
    }
}

@ExperimentalPermissionsApi
@Composable
private fun PermissionView() {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val permissionGranted = cameraPermissionState.status.isGranted

    Column(
        modifier = Modifier.padding(top = AppTheme.dimensions.commonSpaceMedium),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.commonSpaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.commonSpaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.camera_permission_status_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = AppTheme.dimensions.textSizeMedium
            )
            if (permissionGranted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Permissions granted",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Permissions not granted",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        if (!permissionGranted) {
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text(
                    text = stringResource(id = R.string.camera_permission_request_button_label).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeLarge
                )
            }
        }
    }
}
