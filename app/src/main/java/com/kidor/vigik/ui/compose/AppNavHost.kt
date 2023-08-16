package com.kidor.vigik.ui.compose

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.kidor.vigik.extensions.findActivity
import com.kidor.vigik.extensions.navigate
import com.kidor.vigik.extensions.navigateSingleTopTo
import com.kidor.vigik.ui.biometric.home.BiometricHomeScreen
import com.kidor.vigik.ui.biometric.login.BiometricLoginScreen
import com.kidor.vigik.ui.bluetooth.BluetoothScreen
import com.kidor.vigik.ui.home.HomeScreen
import com.kidor.vigik.ui.nfc.check.CheckScreen
import com.kidor.vigik.ui.nfc.emulate.EmulateScreen
import com.kidor.vigik.ui.nfc.history.HistoryScreen
import com.kidor.vigik.ui.nfc.hub.HubScreen
import com.kidor.vigik.ui.nfc.scan.ScanScreen
import com.kidor.vigik.ui.notification.NotificationScreen
import com.kidor.vigik.ui.restapi.RestApiScreen

/**
 * Implementation of [NavHost] for this application.
 */
@Composable
fun AppNavHost(
    context: Context,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.HomeScreen.route,
        modifier = modifier
    ) {
        addHomeScreens(navGraphBuilder = this, navController = navController)
        addBiometricScreens(navGraphBuilder = this, navController = navController, context = context)
        addBluetoothScreens(navGraphBuilder = this)
        addNfcScreens(navGraphBuilder = this, navController = navController, context = context)
        addNotificationScreens(navGraphBuilder = this)
        addRestApiScreens(navGraphBuilder = this)
    }
}

/**
 * Add screens related to Home into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 * @param navController   The navigation controller.
 */
private fun addHomeScreens(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
    navGraphBuilder.composable(route = AppScreen.HomeScreen.route) {
        HomeScreen(
            navigateToBiometric = { navController.navigate(AppScreen.BiometricLoginScreen) },
            navigateToBluetooth = { navController.navigate(AppScreen.BluetoothScreen) },
            navigateToNfc = { navController.navigate(AppScreen.NfcCheckScreen) },
            navigateToNotification = { navController.navigate(AppScreen.NotificationScreen) },
            navigateToRestApi = { navController.navigate(AppScreen.RestApiScreen) }
        )
    }
}

/**
 * Add screens related to Biometric into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 * @param navController   The navigation controller.
 * @param context         The context of the application.
 */
private fun addBiometricScreens(navGraphBuilder: NavGraphBuilder, navController: NavHostController, context: Context) {
    navGraphBuilder.composable(route = AppScreen.BiometricLoginScreen.route) {
        BiometricLoginScreen(
            startBiometricEnrollment = { enrollIntent -> context.startActivity(enrollIntent) },
            navigateToBiometricHome = { navController.navigate(AppScreen.BiometricHomeScreen) }
        )
    }
    navGraphBuilder.composable(route = AppScreen.BiometricHomeScreen.route) {
        BackHandler(enabled = true) { }
        BiometricHomeScreen(
            navigateToBiometricLogin = {
                navController.navigate(destination = AppScreen.BiometricLoginScreen, popUpTo = true)
            }
        )
    }
}

/**
 * Add screens related to Bluetooth into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addBluetoothScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = AppScreen.BluetoothScreen.route) {
        LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        BluetoothScreen()
    }
}

/**
 * Add screens related to NFC into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 * @param navController   The navigation controller.
 * @param context         The context of the application.
 */
private fun addNfcScreens(navGraphBuilder: NavGraphBuilder, navController: NavHostController, context: Context) {
    navGraphBuilder.composable(route = AppScreen.NfcCheckScreen.route) {
        CheckScreen(
            navigateToHub = { navController.navigateSingleTopTo(AppScreen.NfcHubScreen) },
            navigateToSettings = { context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }
        )
    }
    navGraphBuilder.composable(route = AppScreen.NfcHubScreen.route) {
        HubScreen(
            navigateToScanTag = { navController.navigate(AppScreen.NfcScanScreen) },
            navigateToTagHistory = { navController.navigate(AppScreen.NfcHistoryScreen) },
            navigateToEmulateTag = { navController.navigate(AppScreen.NfcEmulateScreen) }
        )
    }
    navGraphBuilder.composable(route = AppScreen.NfcScanScreen.route) {
        ScanScreen()
    }
    navGraphBuilder.composable(route = AppScreen.NfcHistoryScreen.route) {
        HistoryScreen()
    }
    navGraphBuilder.composable(route = AppScreen.NfcEmulateScreen.route) {
        EmulateScreen()
    }
}

/**
 * Add screens related to Notification into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addNotificationScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(
        route = AppScreen.NotificationScreen.route,
        deepLinks = listOf(navDeepLink { uriPattern = AppScreen.NotificationScreen.deeplinkPath })
    ) {
        NotificationScreen()
    }
}

/**
 * Add screens related to REST API into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addRestApiScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = AppScreen.RestApiScreen.route) {
        RestApiScreen()
    }
}

/**
 * Locks the screen orientation then restores the original orientation when the view disappears.
 *
 * @param orientation The orientation to force. Must be a ScreenOrientation from [ActivityInfo].
 */
@Composable
private fun LockScreenOrientation(orientation: Int) {
    LocalContext.current.findActivity()?.let { activity ->
        DisposableEffect(Unit) {
            val originalOrientation = activity.requestedOrientation
            activity.requestedOrientation = orientation
            onDispose {
                // Restore original orientation when view disappears
                activity.requestedOrientation = originalOrientation
            }
        }
    }
}
