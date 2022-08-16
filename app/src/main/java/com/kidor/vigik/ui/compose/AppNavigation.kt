package com.kidor.vigik.ui.compose

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.kidor.vigik.R

/**
 * Metadata of each destination/screen of the application.
 *
 * A destination is defined by its [name] and its [route] in the navigation graph.
 */
abstract class AppDestination(@StringRes private val nameId: Int, val route: String) {
    @Composable
    @ReadOnlyComposable
    fun name(): String {
        return stringResource(id = nameId)
    }
}

object AppNavigation {

    object CheckScreen : AppDestination(
        nameId = R.string.check_nfc_title,
        route = "check"
    )

    object HubScreen : AppDestination(
        nameId = R.string.hub_title,
        route = "hub"
    )

    object ScanScreen : AppDestination(
        nameId = R.string.scan_nfc_title,
        route = "scan"
    )

    object HistoryScreen : AppDestination(
        nameId = R.string.tag_history_title,
        route = "history"
    )

    object EmulateScreen : AppDestination(
        nameId = R.string.emulate_tag_title,
        route = "emulate"
    )

    private val allScreens = listOf(
        CheckScreen,
        HubScreen,
        ScanScreen,
        HistoryScreen,
        EmulateScreen
    )

    /**
     * Returns the screen matching given [route] if exists.
     */
    fun getScreen(route: String?): AppDestination? = allScreens.find { it.route == route }
}
