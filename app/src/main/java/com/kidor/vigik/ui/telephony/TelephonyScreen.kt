package com.kidor.vigik.ui.telephony

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme

private const val DATA_PLACEHOLDER = "-"

/**
 * View that display the section dedicated to telephony.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TelephonyScreen(
    viewModel: TelephonyViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = AppTheme.dimensions.commonSpaceMedium)
    ) {
        ObserveViewState(viewModel = viewModel) { state ->
            when (state) {
                TelephonyViewState.CheckPermission -> PermissionView { viewModel.handleAction(TelephonyViewAction.PermissionsGranted) }
                is TelephonyViewState.ShowData -> {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(all = AppTheme.dimensions.commonSpaceMedium),
                        verticalArrangement = Arrangement.Center
                    ) {
                        ContactView(
                            totalContactNumber = state.totalContactNumber,
                            mobileContactNumber = state.mobileContactNumber
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = AppTheme.dimensions.commonSpaceMedium))
                        SmsView(totalSmsNumber = state.totalSmsNumber)
                    }
                }
            }
        }
    }
}

@ExperimentalPermissionsApi
@Composable
private fun PermissionView(onPermissionsGranted: () -> Unit) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS
        )
    )

    if (permissionsState.allPermissionsGranted) {
        onPermissionsGranted()
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.telephony_permission_status_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = AppTheme.dimensions.textSizeMedium
            )
            Spacer(modifier = Modifier.width(AppTheme.dimensions.commonSpaceSmall))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Permissions not granted",
                tint = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceSmall))
        Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
            Text(
                text = stringResource(id = R.string.telephony_permission_request_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeLarge
            )
        }
    }
}

@Composable
private fun ContactView(totalContactNumber: Int?, mobileContactNumber: Int?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.telephony_contact_section_title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeXLarge
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceSmall))
        Text(
            text = stringResource(
                id = R.string.telephony_contact_total_number_label, totalContactNumber ?: DATA_PLACEHOLDER
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
        Text(
            text = stringResource(
                id = R.string.telephony_contact_mobile_number_label, mobileContactNumber ?: DATA_PLACEHOLDER
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
    }
}

@Composable
private fun SmsView(totalSmsNumber: Int?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.telephony_sms_section_title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeXLarge
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceSmall))
        Text(
            text = stringResource(
                id = R.string.telephony_sms_total_number_label, totalSmsNumber ?: DATA_PLACEHOLDER
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 100)
private fun ContactViewWithoutData() {
    ContactView(totalContactNumber = null, mobileContactNumber = null)
}

@Composable
@Preview(widthDp = 400, heightDp = 100)
private fun ContactViewWithData() {
    ContactView(totalContactNumber = 1337, mobileContactNumber = 42)
}

@Composable
@Preview(widthDp = 400, heightDp = 100)
private fun SmsViewWithoutData() {
    SmsView(totalSmsNumber = null)
}

@Composable
@Preview(widthDp = 400, heightDp = 100)
private fun SmsViewWithData() {
    SmsView(totalSmsNumber = 1337)
}
