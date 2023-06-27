package com.kidor.vigik.ui.compose

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kidor.vigik.extensions.navigate
import com.kidor.vigik.extensions.navigateSingleTopTo
import com.kidor.vigik.ui.biometric.home.BiometricHomeScreen
import com.kidor.vigik.ui.biometric.login.BiometricLoginScreen
import com.kidor.vigik.ui.check.CheckScreen
import com.kidor.vigik.ui.emulate.EmulateScreen
import com.kidor.vigik.ui.history.HistoryScreen
import com.kidor.vigik.ui.hub.HubScreen
import com.kidor.vigik.ui.scan.ScanScreen

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
        startDestination = AppScreen.CheckScreen.route,
        modifier = modifier
    ) {
        composable(route = AppScreen.CheckScreen.route) {
            CheckScreen(
                navigateToHub = { navController.navigateSingleTopTo(AppScreen.HubScreen) },
                navigateToSettings = { context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }
            )
        }
        composable(route = AppScreen.HubScreen.route) {
            HubScreen(
                navigateToScanTag = { navController.navigate(AppScreen.ScanScreen) },
                navigateToTagHistory = { navController.navigate(AppScreen.HistoryScreen) },
                navigateToEmulateTag = { navController.navigate(AppScreen.EmulateScreen) },
                navigateToBiometric = { navController.navigate(AppScreen.BiometricLoginScreen) }
            )
        }
        composable(route = AppScreen.ScanScreen.route) {
            ScanScreen()
        }
        composable(route = AppScreen.HistoryScreen.route) {
            HistoryScreen()
        }
        composable(route = AppScreen.EmulateScreen.route) {
            EmulateScreen()
        }
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
    }
}
