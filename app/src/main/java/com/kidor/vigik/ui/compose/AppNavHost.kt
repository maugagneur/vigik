package com.kidor.vigik.ui.compose

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kidor.vigik.extensions.navigate
import com.kidor.vigik.extensions.navigateSingleTopTo
import com.kidor.vigik.ui.check.CheckScreen
import com.kidor.vigik.ui.emulate.EmulateScreen
import com.kidor.vigik.ui.history.HistoryScreen
import com.kidor.vigik.ui.hub.HubScreen
import com.kidor.vigik.ui.scan.ScanScreen

@Composable
fun AppNavHost(
    context: Context,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppNavigation.CheckScreen.route,
        modifier = modifier
    ) {
        composable(route = AppNavigation.CheckScreen.route) {
            CheckScreen(
                navigateToHub = { navController.navigateSingleTopTo(AppNavigation.HubScreen) },
                navigateToSettings = { context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }
            )
        }
        composable(route = AppNavigation.HubScreen.route) {
            HubScreen(
                navigateToScanTag = { navController.navigate(AppNavigation.ScanScreen) },
                navigateToTagHistory = { navController.navigate(AppNavigation.HistoryScreen) },
                navigateToEmulateTag = { navController.navigate(AppNavigation.EmulateScreen) }
            )
        }
        composable(route = AppNavigation.ScanScreen.route) {
            ScanScreen()
        }
        composable(route = AppNavigation.HistoryScreen.route) {
            HistoryScreen()
        }
        composable(route = AppNavigation.EmulateScreen.route) {
            EmulateScreen()
        }
    }
}
