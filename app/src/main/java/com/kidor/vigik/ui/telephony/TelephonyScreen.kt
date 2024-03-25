package com.kidor.vigik.ui.telephony

import android.Manifest
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) {
        ObserveViewState(viewModel = viewModel) { state ->
            when (state) {
                TelephonyViewState.CheckPermission -> PermissionView { viewModel.handleAction(TelephonyViewAction.PermissionsGranted) }
                is TelephonyViewState.ShowData -> {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(all = AppTheme.dimensions.commonSpaceLarge),
                        verticalArrangement = Arrangement.Center
                    ) {
                        ContactView(
                            totalContactNumber = state.totalContactNumber,
                            mobileContactNumber = state.mobileContactNumber
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = AppTheme.dimensions.commonSpaceMedium))
                        SmsView(totalSmsNumber = state.totalSmsNumber) { phoneNumber, message ->
                            viewModel.handleAction(TelephonyViewAction.SendSms(phoneNumber, message))
                        }
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
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS
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
private fun SmsView(totalSmsNumber: Int?, sendSms: (phoneNumber: String, message: String) -> Unit) {
    val focusManager = LocalFocusManager.current
    var phoneNumber by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

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
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceSmall))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(text = stringResource(id = R.string.telephony_sms_phone_number_hint)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceSmall))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text(text = stringResource(id = R.string.telephony_sms_message_hint)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    focusManager.clearFocus(force = true)
                    sendSms(phoneNumber, message)
                    // Clear message after sending it
                    message = ""
                }
            )
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
@Preview(widthDp = 400, heightDp = 200)
private fun SmsViewWithoutData() {
    SmsView(totalSmsNumber = null) { _, _ -> }
}

@Composable
@Preview(widthDp = 400, heightDp = 200)
private fun SmsViewWithData() {
    SmsView(totalSmsNumber = 1337) { _, _ -> }
}
