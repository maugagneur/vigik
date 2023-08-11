package com.kidor.vigik.ui.compose

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.kidor.vigik.R

/**
 * Metadata of each screen of the application.
 *
 * A screen is defined by its [name] and its [route] in the navigation graph.
 * You can also hide the back arrow in action by setting [showNavigateBack] to false.
 * If the screen is associated to an URI, this value can be find in [deeplinkPath].
 */
sealed class AppScreen(
    @StringRes private val nameId: Int,
    val route: String,
    val deeplinkPath: String = "",
    val showNavigateBack: Boolean = true
) {

    /**
     * Returns the string resource of the screen's name.
     */
    @Composable
    @ReadOnlyComposable
    fun name(): String = stringResource(id = nameId)

    /**
     * Metadata of 'home" screen.
     */
    data object HomeScreen : AppScreen(
        nameId = R.string.home_title,
        route = "home"
    )

    /**
     * Metadata of "biometric login" screen.
     */
    data object BiometricLoginScreen : AppScreen(
        nameId = R.string.biometric_title,
        route = "biometric_login"
    )

    /**
     * Metadata of "biometric home" screen.
     */
    data object BiometricHomeScreen : AppScreen(
        nameId = R.string.biometric_title,
        route = "biometric_home",
        showNavigateBack = false
    )

    /**
     * Metadata of "Bluetooth" screen.
     */
    data object BluetoothScreen : AppScreen(
        nameId = R.string.bluetooth_title,
        route = "bluetooth"
    )

    /**
     * Metadata of "check" screen.
     */
    data object NfcCheckScreen : AppScreen(
        nameId = R.string.nfc_check_title,
        route = "check"
    )

    /**
     * Metadata of "hub" screen.
     */
    data object NfcHubScreen : AppScreen(
        nameId = R.string.nfc_hub_title,
        route = "hub"
    )

    /**
     * Metadata of "scan" screen.
     */
    data object NfcScanScreen : AppScreen(
        nameId = R.string.nfc_scan_title,
        route = "scan"
    )

    /**
     * Metadata of "history" screen.
     */
    data object NfcHistoryScreen : AppScreen(
        nameId = R.string.nfc_tag_history_title,
        route = "history"
    )

    /**
     * Metadata of "emulate" screen.
     */
    data object NfcEmulateScreen : AppScreen(
        nameId = R.string.nfc_emulate_title,
        route = "emulate"
    )

    /**
     * Metadata of 'notification" screen.
     */
    data object NotificationScreen : AppScreen(
        nameId = R.string.notification_title,
        route = "notification",
        deeplinkPath = "${APP_BASE_URI}/notification"
    )

    /**
     * Metadata of "REST API" screen
     */
    data object RestApiScreen : AppScreen(
        nameId = R.string.rest_api_title,
        route = "rest_api"
    )

    companion object {
        private const val APP_BASE_URI = "myapp://navigation"
    }
}

private val allScreens = listOf(
    AppScreen.HomeScreen,
    AppScreen.BiometricLoginScreen,
    AppScreen.BiometricHomeScreen,
    AppScreen.BluetoothScreen,
    AppScreen.NfcCheckScreen,
    AppScreen.NfcHubScreen,
    AppScreen.NfcScanScreen,
    AppScreen.NfcHistoryScreen,
    AppScreen.NfcEmulateScreen,
    AppScreen.NotificationScreen,
    AppScreen.RestApiScreen
)

/**
 * Returns the screen matching given [route] if exists.
 */
fun getScreen(route: String?): AppScreen? = allScreens.find { it.route == route }
