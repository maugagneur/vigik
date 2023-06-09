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
 */
sealed class AppScreen(@StringRes private val nameId: Int, val route: String) {

    /**
     * Returns the string resource of the screen's name.
     */
    @Composable
    @ReadOnlyComposable
    fun name(): String = stringResource(id = nameId)

    /**
     * Metadata of "check" screen.
     */
    object CheckScreen : AppScreen(
        nameId = R.string.check_nfc_title,
        route = "check"
    )

    /**
     * Metadata of "hub" screen.
     */
    object HubScreen : AppScreen(
        nameId = R.string.hub_title,
        route = "hub"
    )

    /**
     * Metadata of "scan" screen.
     */
    object ScanScreen : AppScreen(
        nameId = R.string.scan_nfc_title,
        route = "scan"
    )

    /**
     * Metadata of "history" screen.
     */
    object HistoryScreen : AppScreen(
        nameId = R.string.tag_history_title,
        route = "history"
    )

    /**
     * Metadata of "emulate" screen.
     */
    object EmulateScreen : AppScreen(
        nameId = R.string.emulate_tag_title,
        route = "emulate"
    )

    /**
     * Metadata of "biometric" screen.
     */
    object BiometricScreen : AppScreen(
        nameId = R.string.biometric_title,
        route = "biometric"
    )
}

private val allScreens = listOf(
    AppScreen.CheckScreen,
    AppScreen.HubScreen,
    AppScreen.ScanScreen,
    AppScreen.HistoryScreen,
    AppScreen.EmulateScreen,
    AppScreen.BiometricScreen
)

/**
 * Returns the screen matching given [route] if exists.
 */
fun getScreen(route: String?): AppScreen? = allScreens.find { it.route == route }
