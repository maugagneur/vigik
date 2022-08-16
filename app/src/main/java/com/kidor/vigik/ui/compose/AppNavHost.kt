package com.kidor.vigik.ui.compose

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kidor.vigik.extensions.navigateSingleTopTo
import com.kidor.vigik.ui.check.CheckScreen

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
                navigateToHub = { navController.navigateSingleTopTo(AppNavigation.HubScreen.route) },
                navigateToSettings = { context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }
            )
        }
        composable(route = AppNavigation.HubScreen.route) {
            Text(text = "DEBUG: HUB")
        }
        composable(route = AppNavigation.ScanScreen.route) {
            Text(text = "DEBUG: SCAN")
        }
        composable(route = AppNavigation.HistoryScreen.route) {
            Text(text = "DEBUG: HISTORY")
        }
        composable(route = AppNavigation.EmulateScreen.route) {
            Text(text = "DEBUG: EMULATE")
        }
    }
}
