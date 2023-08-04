package com.kidor.vigik.ui.bluetooth

import android.Manifest
import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kidor.vigik.R
import com.kidor.vigik.data.bluetooth.model.BluetoothDevice
import com.kidor.vigik.data.bluetooth.model.BluetoothDeviceType
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme

internal const val BLUETOOTH_ICON_ENABLE_TEST_TAG = "bluetooth_icon_enable"
internal const val BLUETOOTH_ICON_DISABLE_TEST_TAG = "bluetooth_icon_disable"
internal const val LOCATION_ICON_ENABLE_TEST_TAG = "location_icon_enable"
internal const val LOCATION_ICON_DISABLE_TEST_TAG = "location_icon_disable"
internal const val REFRESH_DEVICES_ICON_TEST_TAG = "refresh_devices_icon"
internal const val CIRCULAR_PROGRESS_TEST_TAG = "circular progress"
internal const val SCAN_ERROR_MESSAGE_TEST_TAG = "scan_error_message"

/**
 * View that display the section dedicated to Bluetooth.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothScreen(
    viewModel: BluetoothViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.padding(AppTheme.dimensions.commonSpaceMedium),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PermissionView()
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceLarge))
        ObserveViewState(viewModel) { state ->
            BluetoothAdapterStatus(isEnable = state.isBluetoothEnable)
            Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceLarge))
            LocationStatus(isEnable = state.isLocationEnable)
            Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceMedium))
            LowEnergyStatus(
                checked = state.leScanSelected,
                enabled = state.isBluetoothEnable && state.isLocationEnable && !state.isScanning,
                onLeScanStateChanged = { viewModel.handleAction(BluetoothViewAction.ChangeLeScanState(it)) }
            )
            Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceLarge))
            Divider()
            Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceSmall))
            BluetoothScanStatus(
                scanInProgress = state.isScanning,
                onRefreshDevicesClick = { viewModel.handleAction(BluetoothViewAction.StartBluetoothScan) }
            )
            if (state.errorMessage == null) {
                DetectedBluetoothDeviceList(devices = state.detectedDevices)
            } else {
                ErrorMessage(state.errorMessage)
            }
        }
    }
}

@ExperimentalPermissionsApi
@Composable
private fun PermissionView() {
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.bluetooth_permission_status_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = AppTheme.dimensions.textSizeMedium
            )
            Spacer(modifier = Modifier.width(AppTheme.dimensions.commonSpaceSmall))
            if (locationPermissionsState.allPermissionsGranted) {
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
        if (!locationPermissionsState.allPermissionsGranted) {
            Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceSmall))
            Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                Text(
                    text = stringResource(id = R.string.bluetooth_permission_request_button_label).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeLarge
                )
            }
        }
    }
}

@Composable
@VisibleForTesting
internal fun BluetoothAdapterStatus(isEnable: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(id = R.string.bluetooth_adapter_status_label),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
        Spacer(modifier = Modifier.width(AppTheme.dimensions.commonSpaceSmall))
        if (isEnable) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Bluetooth enable",
                modifier = Modifier.testTag(BLUETOOTH_ICON_ENABLE_TEST_TAG),
                tint = MaterialTheme.colorScheme.tertiary
            )
        } else {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Bluetooth disable",
                modifier = Modifier.testTag(BLUETOOTH_ICON_DISABLE_TEST_TAG),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
@VisibleForTesting
internal fun LocationStatus(isEnable: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(id = R.string.bluetooth_location_status_label),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
        Spacer(modifier = Modifier.width(AppTheme.dimensions.commonSpaceSmall))
        if (isEnable) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Location enable",
                modifier = Modifier.testTag(LOCATION_ICON_ENABLE_TEST_TAG),
                tint = MaterialTheme.colorScheme.tertiary
            )
        } else {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Location disable",
                modifier = Modifier.testTag(LOCATION_ICON_DISABLE_TEST_TAG),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun LowEnergyStatus(checked: Boolean, enabled: Boolean, onLeScanStateChanged: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Switch,
            onValueChange = onLeScanStateChanged
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.bluetooth_low_energy_scan_status_label),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
        Spacer(modifier = Modifier.width(AppTheme.dimensions.commonSpaceMedium))
        Switch(
            checked = checked,
            onCheckedChange = null,
            thumbContent = {
                Icon(
                    imageVector = if (checked) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize)
                )
            },
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedIconColor = MaterialTheme.colorScheme.tertiary,
                uncheckedThumbColor = MaterialTheme.colorScheme.error
            )
        )
    }
}

@Composable
@VisibleForTesting
internal fun BluetoothScanStatus(scanInProgress: Boolean, onRefreshDevicesClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.bluetooth_detected_devices_label),
            color = MaterialTheme.colorScheme.secondary,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
        Box(contentAlignment = Alignment.Center) {
            if (scanInProgress) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(18.dp)
                        .testTag(CIRCULAR_PROGRESS_TEST_TAG),
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier
                        .clickable(onClick = onRefreshDevicesClick)
                        .testTag(REFRESH_DEVICES_ICON_TEST_TAG),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
@VisibleForTesting
internal fun ErrorMessage(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage,
            modifier = Modifier.testTag(SCAN_ERROR_MESSAGE_TEST_TAG),
            color = MaterialTheme.colorScheme.error,
            fontSize = AppTheme.dimensions.textSizeLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 100)
private fun LowEnergyStatusPreview(@PreviewParameter(LowEnergyStatusDataProvider::class) status: LowEnergyStatusData) {
    AppTheme {
        Box(contentAlignment = Alignment.Center) {
            LowEnergyStatus(checked = status.checked, enabled = status.enabled, onLeScanStateChanged = {})
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 100)
private fun BluetoothScanStatusPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(AppTheme.dimensions.commonSpaceMedium),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BluetoothScanStatus(scanInProgress = false, onRefreshDevicesClick = {})
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(widthDp = 400, heightDp = 300)
@VisibleForTesting
internal fun DetectedBluetoothDeviceList(
    @PreviewParameter(DetectedBluetoothDevicesProvider::class) devices: List<BluetoothDevice>
) {
    if (devices.isNotEmpty()) {
        // Remove overscroll effect
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            LazyColumn {
                items(devices) { device ->
                    val deviceIcon: ImageVector = when (device.type) {
                        BluetoothDeviceType.COMPUTER -> Icons.Default.Laptop
                        BluetoothDeviceType.HEADPHONE -> Icons.Default.Headphones
                        BluetoothDeviceType.HEADSET -> Icons.Default.Headset
                        BluetoothDeviceType.IMAGING -> Icons.Default.Print
                        BluetoothDeviceType.PHONE -> Icons.Default.Phone
                        BluetoothDeviceType.PERIPHERAL -> Icons.Default.Keyboard
                        BluetoothDeviceType.UNKNOWN -> Icons.Default.Bluetooth
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = AppTheme.dimensions.commonSpaceMedium,
                                vertical = AppTheme.dimensions.commonSpaceSmall
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = deviceIcon,
                            contentDescription = "Device icon",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(AppTheme.dimensions.commonSpaceMedium))
                        Column {
                            Text(
                                text = device.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = AppTheme.dimensions.textSizeMedium
                            )
                            Text(
                                text = device.hardwareAddress,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = AppTheme.dimensions.textSizeSmall
                            )
                        }
                    }
                }
            }
        }
    }
}
