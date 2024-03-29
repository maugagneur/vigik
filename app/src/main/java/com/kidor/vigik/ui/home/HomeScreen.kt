package com.kidor.vigik.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kidor.vigik.R
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display all sections of the application.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
@Suppress("LongParameterList")
fun HomeScreen(
    navigateToAnimations: () -> Unit = {},
    navigateToBiometric: () -> Unit = {},
    navigateToBluetooth: () -> Unit = {},
    navigateToBottomSheet: () -> Unit = {},
    navigateToCamera: () -> Unit = {},
    navigateToEmoji: () -> Unit = {},
    navigateToNfc: () -> Unit = {},
    navigateToNotification: () -> Unit = {},
    navigateToRestApi: () -> Unit = {},
    navigateToTelephony: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(all = AppTheme.dimensions.commonSpaceXLarge),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.commonSpaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val homeButtonDataList = listOf(
            HomeButtonData(textId = R.string.home_animations_button_label, navigateToAnimations),
            HomeButtonData(textId = R.string.home_biometric_button_label, navigateToBiometric),
            HomeButtonData(textId = R.string.home_bluetooth_button_label, navigateToBluetooth),
            HomeButtonData(textId = R.string.home_bottom_sheet_button_label, navigateToBottomSheet),
            HomeButtonData(textId = R.string.home_camera_button_label, navigateToCamera),
            HomeButtonData(textId = R.string.home_emoji_button_label, navigateToEmoji),
            HomeButtonData(textId = R.string.home_nfc_button_label, navigateToNfc),
            HomeButtonData(textId = R.string.home_notification_button_label, navigateToNotification),
            HomeButtonData(textId = R.string.home_rest_api_button_label, navigateToRestApi),
            HomeButtonData(textId = R.string.home_telephony_button_label, navigateToTelephony)
        )
        items(homeButtonDataList) { buttonData ->
            Button(
                onClick = buttonData.onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = buttonData.textId).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeMedium
                )
            }
        }
    }
}
