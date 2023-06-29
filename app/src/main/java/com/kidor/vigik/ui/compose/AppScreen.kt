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
 */
sealed class AppScreen(@StringRes private val nameId: Int, val route: String, val showNavigateBack: Boolean = true) {

    /**
     * Returns the string resource of the screen's name.
     */
    @Composable
    @ReadOnlyComposable
    fun name(): String = stringResource(id = nameId)

    /**
     * Metadata of 'home" screen.
     */
    object HomeScreen : AppScreen(
        nameId = R.string.home_title,
        route = "home"
    )

    /**
     * Metadata of "check" screen.
     */
    object NfcCheckScreen : AppScreen(
        nameId = R.string.nfc_check_title,
        route = "check"
    )

    /**
     * Metadata of "hub" screen.
     */
    object NfcHubScreen : AppScreen(
        nameId = R.string.nfc_hub_title,
        route = "hub"
    )

    /**
     * Metadata of "scan" screen.
     */
    object NfcScanScreen : AppScreen(
        nameId = R.string.nfc_scan_title,
        route = "scan"
    )

    /**
     * Metadata of "history" screen.
     */
    object NfcHistoryScreen : AppScreen(
        nameId = R.string.nfc_tag_history_title,
        route = "history"
    )

    /**
     * Metadata of "emulate" screen.
     */
    object NfcEmulateScreen : AppScreen(
        nameId = R.string.nfc_emulate_title,
        route = "emulate"
    )

    /**
     * Metadata of "biometric login" screen.
     */
    object BiometricLoginScreen : AppScreen(
        nameId = R.string.biometric_title,
        route = "biometric_login"
    )

    /**
     * Metadata of "biometric home" screen.
     */
    object BiometricHomeScreen : AppScreen(
        nameId = R.string.biometric_title,
        route = "biometric_home",
        showNavigateBack = false
    )

    /**
     * Metadata of "REST API" screen
     */
    object RestApiScreen : AppScreen(
        nameId = R.string.rest_api_title,
        route = "rest_api"
    )
}

private val allScreens = listOf(
    AppScreen.HomeScreen,
    AppScreen.NfcCheckScreen,
    AppScreen.NfcHubScreen,
    AppScreen.NfcScanScreen,
    AppScreen.NfcHistoryScreen,
    AppScreen.NfcEmulateScreen,
    AppScreen.BiometricLoginScreen,
    AppScreen.BiometricHomeScreen,
    AppScreen.RestApiScreen
)

/**
 * Returns the screen matching given [route] if exists.
 */
fun getScreen(route: String?): AppScreen? = allScreens.find { it.route == route }
