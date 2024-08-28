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
import com.kidor.vigik.data.telephony.PhoneCall
import com.kidor.vigik.data.telephony.PhoneCallStatus
import com.kidor.vigik.data.telephony.Sms
import com.kidor.vigik.data.telephony.SmsType
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme
import com.kidor.vigik.ui.compose.dimensions

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
                TelephonyViewState.CheckPermission -> PermissionView {
                    viewModel.handleAction(TelephonyViewAction.PermissionsGranted)
                }

                is TelephonyViewState.ShowData -> {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(all = MaterialTheme.dimensions.commonSpaceLarge),
                        verticalArrangement = Arrangement.Center
                    ) {
                        ContactView(
                            totalContactNumber = state.totalContactNumber,
                            mobileContactNumber = state.mobileContactNumber
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dimensions.commonSpaceMedium))
                        SmsView(smsList = state.sms) { phoneNumber, message ->
                            viewModel.handleAction(TelephonyViewAction.SendSms(phoneNumber, message))
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dimensions.commonSpaceMedium))
                        CallView(phoneCalls = state.phoneCalls)
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
            Manifest.permission.READ_CALL_LOG,
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = MaterialTheme.dimensions.commonSpaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.telephony_permission_status_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.dimensions.textSizeMedium
            )
            Spacer(modifier = Modifier.width(MaterialTheme.dimensions.commonSpaceSmall))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Permissions not granted",
                tint = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
        Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
            Text(
                text = stringResource(id = R.string.telephony_permission_request_button_label).uppercase(),
                fontSize = MaterialTheme.dimensions.textSizeLarge
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
            fontSize = MaterialTheme.dimensions.textSizeXLarge
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
        Text(
            text = stringResource(
                id = R.string.telephony_contact_total_number_label,
                totalContactNumber ?: DATA_PLACEHOLDER
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.dimensions.textSizeMedium
        )
        Text(
            text = stringResource(
                id = R.string.telephony_contact_mobile_number_label,
                mobileContactNumber ?: DATA_PLACEHOLDER
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.dimensions.textSizeMedium
        )
    }
}

@Composable
private fun SmsView(smsList: List<Sms>?, sendSms: (phoneNumber: String, message: String) -> Unit) {
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
            fontSize = MaterialTheme.dimensions.textSizeXLarge
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
        Text(
            text = stringResource(
                id = R.string.telephony_sms_total_received_label,
                smsList?.filter { sms -> sms.type == SmsType.RECEIVED }?.size ?: DATA_PLACEHOLDER
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.dimensions.textSizeMedium
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
        Text(
            text = stringResource(
                id = R.string.telephony_sms_total_sent_label,
                smsList?.filter { sms -> sms.type == SmsType.SENT }?.size ?: DATA_PLACEHOLDER
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.dimensions.textSizeMedium
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(text = stringResource(id = R.string.telephony_sms_phone_number_hint)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
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
private fun CallView(phoneCalls: List<PhoneCall>?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.telephony_call_section_title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.dimensions.textSizeXLarge
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
        Text(
            text = stringResource(
                id = R.string.telephony_call_emitted_label,
                phoneCalls?.filter { call -> call.status == PhoneCallStatus.EMITTED }?.size ?: DATA_PLACEHOLDER
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.dimensions.textSizeMedium
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
        Text(
            text = stringResource(
                id = R.string.telephony_call_received_label,
                phoneCalls?.filter { call -> call.status == PhoneCallStatus.RECEIVED }?.size ?: DATA_PLACEHOLDER
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.dimensions.textSizeMedium
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
        Text(
            text = stringResource(
                id = R.string.telephony_call_ignored_label,
                phoneCalls?.filter { call -> call.status == PhoneCallStatus.MISSED }?.size ?: DATA_PLACEHOLDER
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.dimensions.textSizeMedium
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
        Text(
            text = stringResource(
                id = R.string.telephony_call_rejected_label,
                phoneCalls?.filter { call -> call.status == PhoneCallStatus.REJECTED }?.size ?: DATA_PLACEHOLDER
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.dimensions.textSizeMedium
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
@Preview(widthDp = 400, heightDp = 250)
private fun SmsViewWithoutData() {
    SmsView(smsList = null) { _, _ -> }
}

@Composable
@Preview(widthDp = 400, heightDp = 250)
@Suppress("kotlin:S1192")
private fun SmsViewWithData() {
    SmsView(
        smsList = listOf(
            Sms("SMS content", SmsType.SENT),
            Sms("SMS content", SmsType.RECEIVED),
            Sms("SMS content", SmsType.RECEIVED),
            Sms("SMS content", SmsType.SENT),
            Sms("SMS content", SmsType.RECEIVED),
            Sms("SMS content", SmsType.SENT),
            Sms("SMS content", SmsType.SENT),
            Sms("SMS content", SmsType.SENT)
        )
    ) { _, _ -> }
}

@Composable
@Preview(widthDp = 400, heightDp = 150)
private fun CallViewWithoutData() {
    CallView(phoneCalls = null)
}

@Composable
@Preview(widthDp = 400, heightDp = 150)
private fun CallViewWithData() {
    CallView(
        phoneCalls = listOf(
            PhoneCall("0123456789", PhoneCallStatus.EMITTED),
            PhoneCall("0123456789", PhoneCallStatus.RECEIVED),
            PhoneCall("0123456789", PhoneCallStatus.RECEIVED),
            PhoneCall("0123456789", PhoneCallStatus.MISSED),
            PhoneCall("0123456789", PhoneCallStatus.RECEIVED),
            PhoneCall("0123456789", PhoneCallStatus.MISSED),
        )
    )
}
