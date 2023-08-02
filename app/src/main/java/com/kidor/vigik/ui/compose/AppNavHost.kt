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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
        // Home
        composable(route = AppScreen.HomeScreen.route) {
            HomeScreen(
                navigateToBiometric = { navController.navigate(AppScreen.BiometricLoginScreen) },
                navigateToBluetooth = { navController.navigate(AppScreen.BluetoothScreen) },
                navigateToNfc = { navController.navigate(AppScreen.NfcCheckScreen) },
                navigateToRestApi = { navController.navigate(AppScreen.RestApiScreen) }
            )
        }

        // Biometric
        composable(route = AppScreen.BiometricLoginScreen.route) {
            BiometricLoginScreen(
                startBiometricEnrollment = { enrollIntent -> context.startActivity(enrollIntent) },
                navigateToBiometricHome = { navController.navigate(AppScreen.BiometricHomeScreen) }
            )
        }
        composable(route = AppScreen.BiometricHomeScreen.route) {
            BackHandler(enabled = true) { }
            BiometricHomeScreen(
                navigateToBiometricLogin = {
                    navController.navigate(destination = AppScreen.BiometricLoginScreen, popUpTo = true)
                }
            )
        }

        // Bluetooth
        composable(route = AppScreen.BluetoothScreen.route) {
            LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            BluetoothScreen()
        }

        // NFC
        composable(route = AppScreen.NfcCheckScreen.route) {
            CheckScreen(
                navigateToHub = { navController.navigateSingleTopTo(AppScreen.NfcHubScreen) },
                navigateToSettings = { context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }
            )
        }
        composable(route = AppScreen.NfcHubScreen.route) {
            HubScreen(
                navigateToScanTag = { navController.navigate(AppScreen.NfcScanScreen) },
                navigateToTagHistory = { navController.navigate(AppScreen.NfcHistoryScreen) },
                navigateToEmulateTag = { navController.navigate(AppScreen.NfcEmulateScreen) }
            )
        }
        composable(route = AppScreen.NfcScanScreen.route) {
            ScanScreen()
        }
        composable(route = AppScreen.NfcHistoryScreen.route) {
            HistoryScreen()
        }
        composable(route = AppScreen.NfcEmulateScreen.route) {
            EmulateScreen()
        }

        // REST API
        composable(route = AppScreen.RestApiScreen.route) {
            RestApiScreen()
        }
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
