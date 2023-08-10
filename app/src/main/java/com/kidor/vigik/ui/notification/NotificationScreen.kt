package com.kidor.vigik.ui.notification

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kidor.vigik.R
import com.kidor.vigik.data.notification.NotificationIcon
import com.kidor.vigik.ui.base.ObserveViewState
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
        modifier = Modifier
            .padding(horizontal = AppTheme.dimensions.commonSpaceXLarge)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.commonSpaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PermissionView()
        ObserveViewState(viewModel) { state ->
            IconSelection(
                selectedIcon = state.notificationIcon,
                onIconClicked = { viewModel.handleAction(NotificationViewAction.ChangeNotificationIcon(it)) }
            )
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.commonSpaceSmall)) {
                AddTextContentSelection(
                    textContentSelected = state.addTextContentSelected,
                    onTextContentSelectionChanged = {
                        viewModel.handleAction(NotificationViewAction.ChangeTextContentSelection(it))
                    }
                )
                TextContentLengthSelection(
                    longContentSelected = state.longTextContentSelected,
                    longContentEnabled = state.addTextContentSelected,
                    onLongContentSelected = { viewModel.handleAction(NotificationViewAction.ChangeContentLength(it)) }
                )
                AddPictureSelection(
                    pictureSelected = state.addPictureSelected,
                    onPictureSelectionChanged = {
                        viewModel.handleAction(NotificationViewAction.ChangePictureSelection(it))
                    }
                )
            }
        }
        Divider()
        GenerateNotificationButton { viewModel.handleAction(NotificationViewAction.GenerateNotification) }
        RemovePreviousNotificationButton { viewModel.handleAction(NotificationViewAction.RemovePreviousNotification) }
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
        modifier = Modifier.padding(top = AppTheme.dimensions.commonSpaceMedium),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.commonSpaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.commonSpaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.notification_permission_status_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = AppTheme.dimensions.textSizeMedium
            )
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
            Button(onClick = { notificationPermissionState.launchMultiplePermissionRequest() }) {
                Text(
                    text = stringResource(id = R.string.notification_permission_request_button_label).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeLarge
                )
            }
        }
    }
}

@Composable
private fun IconSelection(selectedIcon: NotificationIcon, onIconClicked: (icon: NotificationIcon) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NotificationIcon.entries.forEach { icon ->
            // Highlight selected icon
            val iconTint = if (selectedIcon == icon) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.onBackground
            }
            Icon(
                imageVector = icon.vectorImage,
                contentDescription = "",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onIconClicked(icon) },
                tint = iconTint
            )
        }
    }
}

@Composable
private fun AddTextContentSelection(
    textContentSelected: Boolean,
    onTextContentSelectionChanged: (textContentSelected: Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = textContentSelected,
                role = Role.Switch,
                onValueChange = onTextContentSelectionChanged
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.notification_add_text_content_label),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
        Switch(
            checked = textContentSelected,
            onCheckedChange = null
        )
    }
}

@Composable
private fun TextContentLengthSelection(
    longContentSelected: Boolean,
    longContentEnabled: Boolean,
    onLongContentSelected: (longContentSelected: Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = longContentSelected,
                enabled = longContentEnabled,
                role = Role.Switch,
                onValueChange = onLongContentSelected
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.notification_long_content_label),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
        Switch(
            checked = longContentSelected,
            onCheckedChange = null,
            enabled = longContentEnabled
        )
    }
}

@Composable
private fun AddPictureSelection(
    pictureSelected: Boolean,
    onPictureSelectionChanged: (pictureSelected: Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = pictureSelected,
                role = Role.Switch,
                onValueChange = onPictureSelectionChanged
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.notification_add_picture_content_label),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
        Switch(
            checked = pictureSelected,
            onCheckedChange = null
        )
    }
}

@Composable
private fun GenerateNotificationButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.notification_generate_notification_button_label).uppercase(),
            fontSize = AppTheme.dimensions.textSizeLarge
        )
    }
}

@Composable
private fun RemovePreviousNotificationButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AppTheme.dimensions.commonSpaceMedium)
    ) {
        Text(
            text = stringResource(id = R.string.notification_remove_previous_notification_button_label).uppercase(),
            fontSize = AppTheme.dimensions.textSizeLarge,
            textAlign = TextAlign.Center
        )
    }
}
