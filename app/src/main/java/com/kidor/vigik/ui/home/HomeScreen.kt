package com.kidor.vigik.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kidor.vigik.R
import com.kidor.vigik.navigation.AppScreen
import com.kidor.vigik.ui.common.NavigationButton
import com.kidor.vigik.ui.theme.dimensions

/**
 * View that display all sections of the application.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun HomeScreen(
    navigateTo: (AppScreen) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(all = MaterialTheme.dimensions.commonSpaceXLarge),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val screens = listOf(
            AppScreen.AnimationsHubScreen,
            AppScreen.BiometricLoginScreen,
            AppScreen.BluetoothScreen,
            AppScreen.BottomSheetScreen,
            AppScreen.CameraScreen,
            AppScreen.CompassScreen,
            AppScreen.EmojiScreen,
            AppScreen.NfcCheckScreen,
            AppScreen.NotificationScreen,
            AppScreen.PagingScreen,
            AppScreen.RestApiScreen,
            AppScreen.SnackBarScreen,
            AppScreen.TelephonyScreen
        )
        items(screens) { screen ->
            val alternativeTitle = when (screen) {
                AppScreen.NfcCheckScreen -> stringResource(id = R.string.nfc_title)
                else -> null
            }
            NavigationButton(
                destination = screen,
                label = alternativeTitle
            ) { navigateTo(it) }
        }
    }
}
