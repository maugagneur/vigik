package com.kidor.vigik.ui.notification

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kidor.vigik.R
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display the section dedicated to notifications.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.padding(all = AppTheme.dimensions.commonSpaceMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PermissionView()
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceLarge))
        Button(
            onClick = { viewModel.handleAction(NotificationViewAction.GenerateNotification) },
        ) {
            Text(
                text = stringResource(id = R.string.notification_generate_notification_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeLarge
            )
        }
    }
}

@ExperimentalPermissionsApi
@Composable
private fun PermissionView() {
    val notificationPermissionState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.notification_permission_status_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = AppTheme.dimensions.textSizeMedium
            )
            Spacer(modifier = Modifier.width(AppTheme.dimensions.commonSpaceSmall))
            if (notificationPermissionState.allPermissionsGranted) {
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
        if (!notificationPermissionState.allPermissionsGranted) {
            Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceSmall))
            Button(onClick = { notificationPermissionState.launchMultiplePermissionRequest() }) {
                Text(
                    text = stringResource(id = R.string.notification_permission_request_button_label).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeLarge
                )
            }
        }
    }
}
