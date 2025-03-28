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
import androidx.compose.material3.HorizontalDivider
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kidor.vigik.R
import com.kidor.vigik.data.notification.NotificationIcon
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.theme.dimensions

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
            .padding(horizontal = MaterialTheme.dimensions.commonSpaceXLarge)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PermissionView()
        ObserveViewState(viewModel) { state ->
            IconSelection(
                selectedIcon = state.notificationIcon,
                onIconClicked = { viewModel.handleAction(NotificationViewAction.ChangeNotificationIcon(it)) }
            )
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXSmall)) {
                ToggleableRow(
                    text = stringResource(id = R.string.notification_add_text_content_label),
                    value = state.addTextContentSelected,
                    onValueChange = { viewModel.handleAction(NotificationViewAction.ChangeTextContentSelection(it)) }
                )
                ToggleableRow(
                    text = stringResource(id = R.string.notification_long_content_label),
                    value = state.longTextContentSelected,
                    enabled = state.addTextContentSelected,
                    onValueChange = { viewModel.handleAction(NotificationViewAction.ChangeContentLength(it)) }
                )
                ToggleableRow(
                    text = stringResource(id = R.string.notification_add_picture_content_label),
                    value = state.addPictureSelected,
                    onValueChange = { viewModel.handleAction(NotificationViewAction.ChangePictureSelection(it)) }
                )
                ToggleableRow(
                    text = stringResource(id = R.string.notification_add_loader_label),
                    value = state.addLoaderSelected,
                    onValueChange = { viewModel.handleAction(NotificationViewAction.ChangeLoaderSelection(it)) }
                )
                ToggleableRow(
                    text = stringResource(id = R.string.notification_add_loader_infinite_label),
                    value = state.infiniteLoaderSelected,
                    enabled = state.addLoaderSelected,
                    onValueChange = { viewModel.handleAction(NotificationViewAction.ChangeInfiniteLoaderSelection(it)) }
                )
                ToggleableRow(
                    text = stringResource(id = R.string.notification_add_action_buttons_label),
                    value = state.addActionButtons,
                    onValueChange = { viewModel.handleAction(NotificationViewAction.ChangeActionButtonsSelection(it)) }
                )
            }
        }
        HorizontalDivider()
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
        modifier = Modifier.padding(top = MaterialTheme.dimensions.commonSpaceMedium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.notification_permission_status_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.dimensions.textSizeMedium
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
                    fontSize = MaterialTheme.dimensions.textSizeLarge
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
                    .size(MaterialTheme.dimensions.commonSpaceXXLarge)
                    .clickable { onIconClicked(icon) },
                tint = iconTint
            )
        }
    }
}

@Composable
private fun ToggleableRow(
    text: String,
    value: Boolean,
    enabled: Boolean = true,
    onValueChange: (newValue: Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = value,
                role = Role.Switch,
                enabled = enabled,
                onValueChange = onValueChange
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.dimensions.textSizeMedium
        )
        Switch(
            checked = value,
            onCheckedChange = null,
            enabled = enabled
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
            fontSize = MaterialTheme.dimensions.textSizeLarge
        )
    }
}

@Composable
private fun RemovePreviousNotificationButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = MaterialTheme.dimensions.commonSpaceMedium)
    ) {
        Text(
            text = stringResource(id = R.string.notification_remove_previous_notification_button_label).uppercase(),
            fontSize = MaterialTheme.dimensions.textSizeLarge,
            textAlign = TextAlign.Center
        )
    }
}
